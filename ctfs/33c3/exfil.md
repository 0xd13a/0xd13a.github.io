---
layout: page
title: "33C3 CTF Writeup: Exfil"
---

> 100 points
> 
> Solves: 53
> 
> We hired somebody to gather intelligence on an enemy party. But apparently they managed to lose the secret document they extracted. They just sent us this and said we should be able to recover everything we need from it.
> 
> Can you help?

In this challenge we are given a PCAP file ```dump.pcap``` and a Python source code for the server - ```server.py```. 

PCAP file contains a recorded conversation between a DNS client and a server, where DNS queries and CNAME responses seem to contain encoded messages:

Close examination of the server code reveals that DNS queries and responses are used as transport for a remote shell session. DNS client initiates the conversation and the server responds by sending shell commands, for which the client then sends output. Commands are sent in CNAME responses to DNS queries, while the output is encoded in subdomain names in DNS queries. Pretty neat so far...

Each payload is encoded in Base32 and split into 62-character chunks to account for the maximum length of the domain name, with chunks separated with periods and ending in ```.eat-sleep-pwn-repeat.de```. A typical payload would look like this:

```G4JQAAADAB2WSZB5GEYDAMJIMZYGK5DSPEUSAZ3JMQ6TCMBQGEUGM4DFORZHSK.JAM5ZG65LQOM6TCMBQGEUGM4DFORZHSKIK.eat-sleep-pwn-repeat.de```

Since DNS packets go over UDP, the protocol includes special handling for things like duplicate packets. To account for that the first 6 bytes in each payload contain the conversation ID, sequence number, and the acknowledgement. There is no time to develop a fully robust decoding solution, but at the very least it would be necessary to account for duplicate packets. 

Based on the the information gathered so far (and much trial and error :) ) I wrote following script. It goes through all packets in PCAP file, extracts and decodes payloads, discards duplicate packets, and dumps the output to the screen:

```python
import base64
import struct
import dpkt
import sys

# packet sequence numbers that we will keep track of
sseq = -1 
dseq = -1 

def decode_b32(s):
    s = s.upper()
    for i in range(10):
        try:
            return base64.b32decode(s)
        except:
            s += b'='
    raise ValueError('Invalid base32')

def parse(name):
    # split payload data at periods, remove the top level domain name, and decode the data
    data = decode_b32(b''.join(name.split('.')[:-2]))
    (conn_id, seq, ack) = struct.unpack('<HHH', data[:6])
    return (seq, data[6:])

def handle(val, port):
    global sseq, dseq
    (seq,data) = parse(val)

    # remove empty packets
    if len(data) == 0:
        return

    #remove duplicates
    if port == 53:
        if sseq < seq:
            sseq = seq
        else:
            return
    else:
        if dseq < seq:
            dseq = seq
        else:
            return
    sys.stdout.write(data)

# main execution loop - go through all DNS packets, decode payloads and dump them to the screen
for ts, pkt in dpkt.pcap.Reader(open('dump.pcap','r')):
    eth = dpkt.ethernet.Ethernet(pkt)
    if eth.type == dpkt.ethernet.ETH_TYPE_IP:
        ip = eth.data
        if ip.p == dpkt.ip.IP_PROTO_UDP:
            udp = ip.data
            
            dns = dpkt.dns.DNS(udp.data)

            # extract commands from CNAME records and output from queries
            if udp.sport == 53: 
                for rr in dns.an:
                    if rr.type == dpkt.dns.DNS_CNAME:
                        handle(rr.cname, udp.sport)
            else:
                if dns.opcode == dpkt.dns.DNS_QUERY:
                    handle(dns.qd[0].name, udp.sport)
```

Running it (```root@kali:/33c3/exfil# python decode.py > output.bin```) gives us the (output file).

The output is a treasure trove of information:

* There is a public and private key (which we promptly save in ```key.txt```)
* There are commands the user executed to encrypt a document
* And there is the encrypted document itself, written to stdout and captured in the log. The document body is output between tags ```START_OF_FILE``` and ```END_OF_FILE``` (and we use a binary editor to extract its body in a local file ```secret.docx.gpg```)

Now all is left is to backtrace the user's steps from the output log and decrypt the document:

```
root@kali:/33c3/exfil# gpg --import key.txt
gpg: key D43CC062D0D8161F: public key "operator from hell <team@kitctf.de>" imported
gpg: key D43CC062D0D8161F: "operator from hell <team@kitctf.de>" not changed
gpg: key D43CC062D0D8161F: secret key imported
gpg: Total number processed: 2
gpg:               imported: 1
gpg:              unchanged: 1
gpg:       secret keys read: 1
gpg:   secret keys imported: 1


root@kali:/33c3/exfil# gpg --decrypt --recipient team@kitctf.de --trust-model always secret.docx.gpg > secret.docx
gpg: encrypted with 2048-bit RSA key, ID 4C2B141BBF30A26A, created 2016-12-11
      "operator from hell <team@kitctf.de>"
```
File ```secret.docx``` contains the key:

```
The secret codeword is 

33C3_g00d_d1s3ct1on_sk1llz_h0mie
```
