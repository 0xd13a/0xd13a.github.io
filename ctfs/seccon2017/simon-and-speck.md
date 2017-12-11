---
layout: page
title: "SECCON 2016 Online CTF Writeup: Simon and Speck Block Ciphers"
---

> Simon and Speck Block Ciphers
>
> 100 points
>
> [https://eprint.iacr.org/2013/404.pdf](https://eprint.iacr.org/2013/404.pdf)
>
> Simon_96_64, ECB, key="SECCON{xxxx}", plain=0x6d564d37426e6e71, cipher=0xbb5d12ba422834b5

This was an opportunity to learn about yet more encryption algorithms. [Simon](https://en.wikipedia.org/wiki/Simon_(cipher)) and [Speck](https://en.wikipedia.org/wiki/Speck_(cipher)) were developed by NSA and put into public domain. One of the goals behind developing them was to have algorithms that are optimized for performance, both in software and in hardware.

Based on the description the key size is 96 bits (12 bytes) and the block size is 64 bits (8 bytes). The key template given is exactly 12 bytes, so it seems that we only need to find the missing 4-byte sequence - something that should be easily bruteforced, especially given the optimized nature of the algorithm.

Luckily for us a number of implementations exist online, including [this one](https://github.com/inmcm/Simon_Speck_Ciphers/tree/master/Python).

Let's plug it into ```pwntools``` bruteforcing facility:

```python
from pwn import *
from simon import SimonCipher
import binascii

s = iters.mbruteforce(lambda x: SimonCipher(int(binascii.hexlify("SECCON{"+x+"}"),16),mode='ECB',key_size=96, block_size=64).encrypt(0x6d564d37426e6e71) == 0xbb5d12ba422834b5, string.printable, 4, 'fixed')

print s
```

Running the script very quickly gets us the answer:

```
$ python solve.py 
[+] MBruteforcing: Found key: "6Pz0"
6Pz0
```

The flag is ```SECCON{6Pz0}```.