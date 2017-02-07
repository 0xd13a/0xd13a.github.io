---
layout: page
title: "Alex CTF 2017 Writeup: Many time secrets"
---

> CR2: Many time secrets
>
> 100
> 
> This time Fady learned from his old mistake and decided to use onetime pad as his encryption technique, but he never knew why people call it one time pad!
>
> msg

As suggested by the description we need to break the repeating pad, which is essentially a multibyte XOR. As a shortcut let's use [cryptanalib](https://github.com/nccgroup/featherduster) for that. Unfortunately it does not output the found key, so we have to XOR again with ciphertext to get it:

```python
import cryptanalib as ca

with open('msg', 'r') as f:
    ciphertext = f.read().translate(None, ' \n').decode('hex')

output = ca.break_multi_byte_xor(ciphertext,verbose=True)
print output[0]

flag = ''
for x in range(len(ciphertext)):
    flag += chr(ord(ciphertext[x]) ^ ord(output[0][x]))

print flag
```

The flag is ```ALEXCTF{HERE_GOES_THE_KEY}```:

```sh
$ python solve.py 
Trying keysize 26
Processing chunk 26 of 94
Trying keysize 13
Processing chunk 39 of 94
Trying keysize 12
Processing chunk 51 of 94
Trying keysize 35
Processing chunk 86 of 94
Trying keysize 8
Processing chunk 94 of 94
Dear Friend, This time I understood my mistake and used One time pad encryption scheme, I heard that it is the only encryption method that is mathematically proven to be not cracked ever if the key is kept secure, Let Me know if you agree with me to use this encryption scheme always.
ALEXCTF{HERE_GOES_THE_KEY}ALEXCTF{HERE_GOES_THE_KEY}ALEXCTF{HERE_GOES_THE_KEY}ALEXCTF{HERE_GOES_THE_KEY}ALEXCTF{HERE_GOES_THE_KEY}ALEXCTF{HERE_GOES_THE_KEY}ALEXCTF{HERE_GOES_THE_KEY}ALEXCTF{HERE_GOES_THE_KEY}ALEXCTF{HERE_GOES_THE_KEY}ALEXCTF{HERE_GOES_THE_KEY}ALEXCTF{HERE_GOES_THE_KE
```
