---
layout: page
title: "Alex CTF 2017 Writeup: Ultracoded"
---

> CR1: Ultracoded
>
> 50
>
> Fady didn't understand well the difference between encryption and encoding, so instead of encrypting some secret message to pass to his friend, he encoded it!
>
> Hint: Fady's encoding doens't handly any special character
>
> zero_one

The input file is a set of words ```ZERO``` and ```ONE```. Further analysis shows that it is a Base64-encoded Morse code. Let's put this knowledge in a script:

```python
from pwn import *
import morse_talk as mtalk

with open('zero_one', 'r') as f:
    data = f.read().translate(None, ' \n')

data = data.replace("ZERO","0").replace("ONE","1")
data = b64d(''.join(chr(int(data[i:i+8], 2)) for i in xrange(0, len(data), 8)))

data = mtalk.decode(data)

print data
```

When we run the script we get the flag:

```sh
$ python solve.py 
ALEXCTFTH15O1SO5UP3RO5ECR3TOTXT
```

Because Morse code does not handle special characters we have to fiddle with the flag a bit more. Here is the final version: ```ALEXCTF{TH15_1S_5UP3R_5ECR3T_TXT}```.