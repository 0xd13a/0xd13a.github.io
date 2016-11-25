---
layout: page
title: RC3 CTF 2016 Writeup: Some Pang
---

> 50 points
>
> we captured some iseeempee pang pakets. do the thing. get the flag. win the POINTS!
> Download Link: here
>
> author: wumb0

The zip file that comes with the challenge contains a PCAP file ```somepang.pcap```. Once we open it in [Wirehark](https://www.wireshark.org/) we see a bunch of ICMP packets. The send and receive packets look identical. The data fields of the packets contain some kind of character sequence, with 2 characters being repeated 12 times:

![Wireshark screencap]({{ site.baseurl }}/ctfs/rc3ctf2016/somepang/wireshark.png)

When we scroll to the end of the PCAP file we see that the last packet contains sequence ```==```, which likely indicates that a large base64-encoded blob is being transmitted:

![Wireshark screencap 2]({{ site.baseurl }}/ctfs/rc3ctf2016/somepang/wireshark2.png)

Let's use [tshark](https://www.wireshark.org/docs/man-pages/tshark.html) to extract data from either send or receive part of the conversation:

```
root@kali:~/rc3# tshark -r somepang.pcap -T fields -e data.data -Y "ip.dst eq 192.168.1.198" > out.txt
```

The output is in the following format:

```
2f:39:2f:39:2f:39:2f:39:2f:39:2f:39:2f:39:2f:39:2f:39:2f:39:2f:39:2f:39
6a:2f:6a:2f:6a:2f:6a:2f:6a:2f:6a:2f:6a:2f:6a:2f:6a:2f:6a:2f:6a:2f:6a:2f
34:41:34:41:34:41:34:41:34:41:34:41:34:41:34:41:34:41:34:41:34:41:34:41
...
```

We then put together and run a small script that decodes last 2 characters in each line of the file, combines all characters, and base64-decodes them into an output file:

```python
import base64

str = ""
with open("out.txt") as f:
	for x in f.readlines():
		b1,b2 = x.strip().split(':')[-2:]
		str += chr(int(b1, 16)) + chr(int(b2, 16))
				
with open("out.dat", "wb") as o:
	o.write(base64.b64decode(str))
```

The output file turns out to be a JPEG:

```
root@kali:~/rc3# file out.dat
out.dat: JPEG image data, JFIF standard 1.01, aspect ratio, density 72x72, segment length 16, Exif Standard: [TIFF image data, big-endian, direntries=2, orientation=upper-left], baseline, precision 8, 720x449, frames 3
```

When we rename and open it we see that it contains the flag: ```RC3-2016-PANG-ME-LIKE-ONE-OF-YOUR-FRENCH-GORILLAZ```

![Solution]({{ site.baseurl }}/ctfs/rc3ctf2016/somepang/out.jpg)
