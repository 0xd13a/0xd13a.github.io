---
layout: page
title: "DEF CON CTF Qualifier 2017 Writeup: alchemy"
---

> alchemy Crackme 2000
> 
> 48 points
> 
> cm2k-alchemy_c745e862098878b8052e1e9588c59bff.quals.shallweplayaga.me:12004
> 
> [Files](https://2017.notmalware.ru/db5af503ef25636f450d5e682ca8c6ea9c1b0771/ae5b9a51e1d20b010e736c935f96a23ae5115c54824816170d9acb85a8feaeb3.tar.bz2)


The solution to this challenge is similar to ```magic```, ```sorcery```, and others in that category.

The binaries in the challenge all take a secret code as an input and verify it:

```sh
$ ./024ae029889401df92b0646be0394557b28c602740951e70cdecbc2ea5544f99 
enter code:
XYZ
$
```

Supposedly the binaries mostly differ in a small way, e.g the characters of the secret code. To verify, let's disassemble some of them and diff the outputs. Disassembly can be done by running ```objdump -M intel -d filename```.

After some visual inspection it does seem that the code checks the input character by character:

![diffs]({{ site.baseurl }}/ctfs/defcon2017/alchemy/diffs.png)

Let's note the opcode byte pattern surrounding these occurrences and search for it in a script:

```python
import os, base64
from pwn import *

dict = {}

# go through all challenge files and pick up embedded codes
for file in os.listdir("."):
	data = open(file, "rb").read()
	txt = ""
	for i in range(len(data)):
		# skip an odd occurrence
		if (i+3 < len(data)) and data[i+3] == b"\xff":
			continue
		# collect individual characters of the code, account for all variations of opcodes
		if data[i:i+3] == b"\x48\x83\xf9" and (data[i+4:i+6] == b"\x0f\x85" or data[i+4:i+6] == b"\x74\x19"):
			txt += data[i+3]
		# collect the last character and exit
		if data[i:i+3] == b"\x48\x83\xf8" and (data[i+4:i+6] == b"\x0f\x85" or data[i+4:i+6] == b"\x74\x19"):
			txt += data[i+3]
			break
	dict[file] = base64.b64encode(txt)
	
r = remote('cm2k-alchemy_c745e862098878b8052e1e9588c59bff.quals.shallweplayaga.me',12004)

r.recvline_contains('send your solution as base64, followed by a newline')
while True:
	s = r.recvline(False)
	print s
	if not dict.has_key(s):
		break
	r.sendline(dict[s])
	print dict[s]
```


Running the script gives us the flag ```end of the world sun clyigujheo```:

```
$ python solve.py 
[+] Opening connection to cm2k-alchemy_c745e862098878b8052e1e9588c59bff.quals.shallweplayaga.me on port 12004: Done
634e069f9f15b5e3d45173344db78a16ed76e77a21402193f862734d7efe9244
bGstdGhyb3VnaCBzZWdtZW50IHRvIHRoZSByaWRlLCBpbmNyZWFzaQ==
e993eeb5d8c1c5f0736b60b3fbfbc2ec65a71af843ff1c094791d3c3bd834290
bWl4ZWQgaXQgd2l0aCBhIGJyaWxsaWFudCBzdG9yeSwgYW5kIGI=
ab0ccb6a8b8462a29c401f36e30e479166fa7cc5c0869222c2a771c103d452a3
a2luZyBHaG9zdHMgaW4gYSBibGE=
adedcc2c3fcdad8ea7af924cfcae1209b095bd3c589d0cde3065cd15320986e6
cmUgd2UgY2FuIA==
64057a1e16030eef768fb9cff3fc8d9fae63dad39363945ccb8ccb40374ad46d
IGFnYWluIGluIG15IHNraW4uIEFnYWluLCBteSBsaWZlIHJlYm9vdGVkLCBhbg==
74480f1ee6981ecfbcf818aed0fe7f788805ed4f544919154134e432639568f1
bmFtZSBlYWNoIHRpbWUgdGhleSByb2Rl
f471e83348171e18b23ebc8eb005564fefa882745e60d706a9161384fbcb91e7
aW5jdCBwbGVhc3VyZSB0byBnaXY=
a5f6e04d1589b4bcc88170880e2bae376b31fe4c87d3b2275bd09cd94b125376
aHRvc2hlZCBhbmQgc2FpZCw=
2160a582d7a6b4970f3324bb16634826dcf340fd9c41251a2ff24b44245f3697
bmNsZSBnb2luZyB0byBzbGVlcCBmb3IgdGhyZWUgbWlsbGllbm4=
7b4927ce87f11684e3586a389ed7e3f38dcb9d16688422e0b67a8dbd6b8e16d9
ZSBCaXRjaHVuIFNvY2lldHkuIFNv
The flag is: end of the world sun clyigujheo
[*] Closed connection to cm2k-alchemy_c745e862098878b8052e1e9588c59bff.quals.shallweplayaga.me port 12004
```