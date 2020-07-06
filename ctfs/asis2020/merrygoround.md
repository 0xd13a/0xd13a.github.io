---
layout: page
title: "ASIS CTF Quals 2020 Writeup: Merry-go-round"
---

> Merry-go-round
>
> Funfair or Carousel? Which one is more fun? Does it matter at all?
> 
> Download: [mgr_9f9203cbab28aa57979b6a599d05307e72da8bbe.txz]({{ site.baseurl }}/ctfs/asis2020/merrygoround/mgr_9f9203cbab28aa57979b6a599d05307e72da8bbe.txz)

This is a reversing challenge, so let's pop it into Ghidra. Quick analysis brings us to the main function ```FUN_00101bba```. It does the following:

* Initializes some constant encryption key tables
* Process the input flag file one character at a time, encrypting it (see function ```FUN_001025f```)
* Encrypt the entire encrypted string again (in function ```FUN_0010250```), this time in its entirety
* Output encrypted flag into a file

The encryption key table intialization is not worth analyzing too deeply, we can simply debug the program and copy it from memory.

```FUN_001025f``` does a bunch XORing of the character to be encrypted with some constants and data from the key table. ```FUN_0010250``` works in a similar way but also includes a random value that will also be encoded at the end of the output file.

To get the flag we reverse the process: get the random value from the encoded flag, emulate reverse of ```FUN_0010250``` and then emulate reverse of ```FUN_001025f```.

Let's encode this in a script:

```python
import binascii

# Key table copied from memory
key = [0xDE, 0xD9, 0xA1, 0xF9, 0xED, 0xC0, 0xA8, 0xFF, 
0xE1, 0xD6, 0xAC, 0x91, 0xA0, 0xC6, 0xE7, 0xF2, 0xBC, 
0xA6, 0xE6, 0xF9, 0xB2, 0xAB, 0xAF, 0xAB, 0xBD, 0xEE, 
0xBC, 0xF7, 0xB1, 0xFC, 0xBB, 0xAE, 0xDD, 0x9A, 0xCB, 
0x80, 0xC8, 0xD8, 0x96, 0xD4, 0xBC]

flag = bytearray(open("flag.enc","rb").read())

# FUN_0010250 decoded:
rand = key[(83 ^ 0xA5) % 0x29] ^ 83 ^ flag[83]

for i in range(len(flag)-1):
	flag[i] ^= i ^ (key[(i ^ 0xA5) % 0x29]) ^ rand

# FUN_001025f decoded
for i in range(len(flag)-1):
	flag[i] ^= 0 ^ 0xA5 ^ (key[(0 ^ 0xA5) % 0x29]) 
	
# Overwrite the random value
flag[len(flag)-1] = " "

print(flag)

```

Script decodes the flag:

```
$ python solve.py 
ASIS{Kn0w_7h4t_th3_l1fe_0f_thi5_wOrld_1s_8Ut_amu5em3nt_4nd_div3rsi0n_aNd_adOrnmen7}
```

The flag is ```ASIS{Kn0w_7h4t_th3_l1fe_0f_thi5_wOrld_1s_8Ut_amu5em3nt_4nd_div3rsi0n_aNd_adOrnmen7}```.