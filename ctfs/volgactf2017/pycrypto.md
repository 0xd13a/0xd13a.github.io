---
layout: page
title: "VolgaCTF 2017 Quals Writeup: PyCrypto"
---

> PyCrypto
> 
> 150
> 
> This crypto algorithm uses a huge key and it's implementation is not so trivial to reverse engineer. Isn't it wonderful?
>
> [encrypt.py]({{ site.baseurl }}/ctfs/volgactf2017/pycrypto/encrypt.py)
>
> [flag.enc]({{ site.baseurl }}/ctfs/volgactf2017/pycrypto/flag.enc)
>
> [pycryptography.so]({{ site.baseurl }}/ctfs/volgactf2017/pycrypto/pycryptography.so)

Before we jump into reversing the shared library let's experiment with encryption utility a little bit:

```
$ python3
Python 3.5.3 (default, Jan 19 2017, 14:11:04) 
[GCC 6.3.0 20170118] on linux
Type "help", "copyright", "credits" or "license" for more information.
>>> from pycryptography import encrypt
>>> print(encrypt(('a'*20).encode(), b"\x00\x00\x00\x00"))
b'aaaaaaaaaaaaaaaaaaaa'
>>> print(encrypt(('a'*20).encode(), b"\x00\x00\x00\x01"))
b'aaa`aaa`aaa`aaa`aaa`'
```

Encrypting with 0's gives us back the original plaintext, and encrypting with a slightly changed key gives us a repeated pattern where only one character is changed. All this points to a simple XOR with repeating encryption key. We can solve that without having to reverse the shared library by employing the super useful [CryptoAttacks](https://github.com/GrosQuildu/CryptoAttacks) library:

```python
from CryptoAttacks.Classic import one_time_pad

print one_time_pad.break_repeated_key(open("flag.enc","rb").read(), lang='English', no_of_comparisons=10, key_size=20, max_key_size=20)
```

When we execute the code we get the flag ```VolgaCTF{N@me_is_Pad_Many_Times_P@d_Mi$$_me?}```:

```sh
$ python solve.py 
[('\x94\xffc\xa3\x8du\xd8\xc4\x1a\xc1\xca$\x1ef\x0c\x1f\xc6\xe2\xcc\xea', 'VolgaCTF{N@me_is_Pad_Many_Times_P@d_Mi$$_me?}
Gilbert Vernam was an AT&T Bell Labs engineer who, in 1917, invented an additive polyalphabetic stream cipher and later co-invented...

...
```
