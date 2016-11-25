---
layout: page
title: RC3 CTF 2016 Writeup: Salad
---

> a
> a
> a

> 100 points
>
> “The fault, dear Brutus, is not in our stars, but in ourselves.” (I.ii.141) Julius Caesar in William Shakespeare’s Julius Caesar
>
> Cipher Text: 7sj-ighm-742q3w4t

Right off the bat there are a lot of clues to how the flag is encoded. The Caesar quote and challenge name both point to [Caesar cipher](https://en.wikipedia.org/wiki/Caesar_cipher). Also we know that the flags are in format ```RC3-2016-xxx```, so the first two sections of the encoded flag are likely ```RC3-2016-```.

Let's try substituting letters and numbers from that section into a simple encoding table:

```
0123456789 ABCDEFGHIJKLMNOPQRSTUVWXYZ
ghij  m      s              7
```
