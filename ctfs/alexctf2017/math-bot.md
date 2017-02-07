---
layout: page
title: "Alex CTF 2017 Writeup: Math bot"
---

> SC1: Math bot
>
> 100
>
> It is well known that computers can do tedious math faster than human.
>
> nc 195.154.53.62 1337
> 
> Update
> we got another mirror here
>
> nc 195.154.53.62 7331

When talking to the remote bot system we see that it gives us a number of simple expressions to solve:

```sh
$ nc 195.154.53.62 1337
                __________
         ______/ ________ \______
       _/      ____________      \_
     _/____________    ____________\_
    /  ___________ \  / ___________  \
   /  /XXXXXXXXXXX\ \/ /XXXXXXXXXXX\  \
  /  /############/    \############\  \
  |  \XXXXXXXXXXX/ _  _ \XXXXXXXXXXX/  |
__|\_____   ___   //  \\   ___   _____/|__
[_       \     \  X    X  /     /       _]
__|     \ \                    / /     |__
[____  \ \ \   ____________   / / /  ____]
     \  \ \ \/||.||.||.||.||\/ / /  /
      \_ \ \  ||.||.||.||.||  / / _/
        \ \   ||.||.||.||.||   / /
         \_   ||_||_||_||_||   _/
           \     ........     /
            \________________/

Our system system has detected human traffic from your IP!
Please prove you are a bot
Question  1 :
202442497028903948486871061450208 % 191074133703182836787330064501304 =
11368363325721111699540996948904                                       
Question  2 :
306327109688119887963854945995024 % 289503955876579516432389611624816 =
16823153811540371531465334370208 

...
```

There are too many of them to solve by hand however, so we will use Python's ```eval()``` function to complete them:

```python
from pwn import *

r = remote('195.154.53.62', 1337)

r.recvline_contains('Please prove you are a bot')
while True:
    s = r.recvline(False)
    print s
    if s.endswith('='):
        answer = str(eval(s[:-1]))
        print answer
        r.sendline(answer)
```

Running the script gives us the key ```ALEXCTF{1_4M_l33t_b0t}```:

```sh
$ python solve.py

...

Question  250 :
221674459966639462312539617249524 + 199262348569153796979171354903174 =
420936808535793259291710972152698
Well no human got time to solve 500 ridiculous math challenges
Congrats MR bot!
Tell your human operator flag is: ALEXCTF{1_4M_l33t_b0t}
```