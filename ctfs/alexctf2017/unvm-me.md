---
layout: page
title: "Alex CTF 2017 Writeup: unVM me"
---

> RE4: unVM me
> 
> 250
> 
> If I tell you what version of python I used .. where is the fun in that?
>
> unvm_me.pyc

The compiled Python script that we got here can be easily decompiled with [uncompyle6](https://github.com/rocky/python-uncompyle6):

```python
# uncompyle6 version 2.9.9
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.12+ (default, Sep  1 2016, 20:27:38) 
# [GCC 6.2.0 20160927]
# Embedded file name: unvm_me.py
# Compiled at: 2016-12-20 16:44:01
import md5
md5s = [174282896860968005525213562254350376167L, 137092044126081477479435678296496849608L, 126300127609096051658061491018211963916L, 314989972419727999226545215739316729360L, 256525866025901597224592941642385934114L, 115141138810151571209618282728408211053L, 8705973470942652577929336993839061582L, 256697681645515528548061291580728800189L, 39818552652170274340851144295913091599L, 65313561977812018046200997898904313350L, 230909080238053318105407334248228870753L, 196125799557195268866757688147870815374L, 74874145132345503095307276614727915885L]
print 'Can you turn me back to python ? ...'
flag = raw_input('well as you wish.. what is the flag: ')
if len(flag) > 69:
    print 'nice try'
    exit()
if len(flag) % 5 != 0:
    print 'nice try'
    exit()
for i in range(0, len(flag), 5):
    s = flag[i:i + 5]
    if int('0x' + md5.new(s).hexdigest(), 16) != md5s[i / 5]:
        print 'nice try'
        exit()

print 'Congratz now you have the flag'
# okay decompiling unvm_me.pyc
```

This code shows that the flag is split into 13 5-character chunks, and each chunk matches an MD5 hash. With no more information available it looks like we would have to bruteforce the solution:

```python
from pwn import *
import md5

def trycomb(pref, suff, length, mdhash):
    alphabet = string.ascii_letters + string.digits + "_"
    trylen = length - len(pref) - len(suff)

    s = iters.mbruteforce(lambda x: int('0x'+md5.new(pref + x + suff).hexdigest(),16) == mdhash, alphabet, trylen, 'fixed')

    if (s == None):
        s = '<not found>'
    return pref + s + suff

s = trycomb('ALEXC','',5,174282896860968005525213562254350376167L)
s += trycomb('TF{','',5,137092044126081477479435678296496849608L)
s += trycomb('','',5,126300127609096051658061491018211963916L)
s += trycomb('','',5,314989972419727999226545215739316729360L)
s += trycomb('','',5,256525866025901597224592941642385934114L)
s += trycomb('','',5,115141138810151571209618282728408211053L)
s += trycomb('','',5,8705973470942652577929336993839061582L)
s += trycomb('','',5,256697681645515528548061291580728800189L)
s += trycomb('','',5,39818552652170274340851144295913091599L)
s += trycomb('','',5,65313561977812018046200997898904313350L)
s += trycomb('','',5,230909080238053318105407334248228870753L)
s += trycomb('','',5,196125799557195268866757688147870815374L)
s += trycomb('','}',5,74874145132345503095307276614727915885L)

print "flag: " + s
```

It takes about an hour to run, depending on your hardware. In the end we get the flag ```ALEXCTF{dv5d4s2vj8nk43s8d8l6m1n5l67ds9v41n52nv37j481h3d28n4b6v3k}```:

```sh
$ python solve.py
[+] MBruteforcing: Found key: ""
[+] MBruteforcing: Found key: "dv"
[+] MBruteforcing: Found key: "5d4s2"
[+] MBruteforcing: Found key: "vj8nk"
[+] MBruteforcing: Found key: "43s8d"
[+] MBruteforcing: Found key: "8l6m1"
[+] MBruteforcing: Found key: "n5l67"
[+] MBruteforcing: Found key: "ds9v4"
[+] MBruteforcing: Found key: "1n52n"
[+] MBruteforcing: Found key: "v37j4"
[+] MBruteforcing: Found key: "81h3d"
[+] MBruteforcing: Found key: "28n4b"
[+] MBruteforcing: Found key: "6v3k"
flag: ALEXCTF{dv5d4s2vj8nk43s8d8l6m1n5l67ds9v41n52nv37j481h3d28n4b6v3k}
```

