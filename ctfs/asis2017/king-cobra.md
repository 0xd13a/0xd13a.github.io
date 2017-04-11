---
layout: page
title: "ASIS CTF 2017 Quals Writeup: King Cobra"
---

> King Cobra
> 
> 128
>
> King Cobra can swallows her victim like Python, do you want to test it?
>
> [king_cobra]({{ site.baseurl }}/ctfs/asis2017/king-cobra/king_cobra) 

```king_cobra``` is an executable. When we run it, it returns an error:

```sh
$ ./king_cobra
Oops, do you know the usage?!
```

This error message is not visible in the executable body, however. ```binwalk``` reveals that there are a number of compressed sections in the executable, let's extract them:

```sh
$ binwalk -e ./king_cobra

DECIMAL       HEXADECIMAL     DESCRIPTION
--------------------------------------------------------------------------------
0             0x0             ELF, 64-bit LSB executable, AMD x86-64, version 1 (SYSV)
29849         0x7499          Zlib compressed data, best compression
30007         0x7537          Zlib compressed data, best compression
30178         0x75E2          Zlib compressed data, best compression
31317         0x7A55          Zlib compressed data, best compression
35518         0x8ABE          Zlib compressed data, best compression
41527         0xA237          Zlib compressed data, best compression
43099         0xA85B          Zlib compressed data, best compression
43567         0xAA2F          Zlib compressed data, best compression
...
```

When we search the extracted sections for the error message above, it is found in section ```A85B```:

```sh
$ strings _king_cobra.extracted/A85B
GHn]
GHWn
argvc
chrt
ord(
datat
rest
reverse_1.1.pyt
encode
Oops, do you know the usage?!i
flag.enct
your encoded file is ready :Ps$
huh?!, what do you mean by this arg?N(
sysR
lent
opent
readR
writet
close(
reverse_1.1.pyt
<module>
```

Other strings indicate that it is a compiled Python file. Closer inspection reveals that it is missing the required header, which is easy to add: ```03 F3 0D 0A 04 F5 E7 58```.

Now we reverse it with ```uncompyle6```:

```python
# uncompyle6 version 2.9.9
# Python bytecode 2.7 (62211)
# Decompiled from: Python 2.7.13 (default, Jan 19 2017, 14:48:08) 
# [GCC 6.3.0 20170118]
# Embedded file name: reverse_1.1.py
# Compiled at: 2017-04-07 16:22:28
from sys import argv

def encode(data):
    res = ''
    for b in data:
        res += chr((ord(b) & 15) << 4 | (ord(b) & 240) >> 4)

    return res


if len(argv) < 2:
    print 'Oops, do you know the usage?!'
else:
    try:
        data = open(argv[1], 'r').read()
        f = open('flag.enc', 'w')
        f.write(encode(data))
        f.close()
        print 'your encoded file is ready :P'
    except:
        print 'huh?!, what do you mean by this arg?'
# okay decompiling a.pyc
```

The algorithm seems pretty simple - it takes a file on command line and builds ```flag.enc```. And the encoding is trivial - a simple swap of lower 4 bits and higher 4 bits in each byte (this script can be used both to encode and decode).

But where is ```flag.enc```? This name can be found inside ```king_cobra```, so it must be embedded in it in some way. This stumped me for a while until I ran ```strace``` over ```king_cobra```:

```sh
$ strace ./king_cobra
...
stat("/tmp/_MEI1F6ohE/flag.enc", 0x7ffcbadb7ba0) = -1 ENOENT (No such file or directory)
open("/tmp/_MEI1F6ohE/flag.enc", O_WRONLY|O_CREAT|O_TRUNC, 0666) = 3
...
stat("/tmp/_MEI1F6ohE/flag.enc", {st_mode=S_IFREG|0700, st_size=7265, ...}) = 0
unlink("/tmp/_MEI1F6ohE/flag.enc")      = 0
...
```

So when the application is run the encoded flag is dumped into a temporary folder, and then deleted. So let's run the application in debugger (e.g. ```EDB```) and break before the files are deleted - we can then simply take the file from the temp folder. 

Once we run the encoding script over it, it is revealed that the file is in fact a PNG image containing the flag - ```ASIS{20a87eb1e30361e19ef48940f9573fe3}```.

![flag.png]({{ site.baseurl }}/ctfs/asis2017/king-cobra/flag.png)