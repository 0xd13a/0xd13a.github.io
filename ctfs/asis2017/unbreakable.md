---
layout: page
title: "ASIS CTF 2017 Quals Writeup: Unbreakable"
---

> Unbreakable
>
> 193
>
> We think this challenge is unbreakable, even for you? Try it now!
>
> [unbreakable]({{ site.baseurl }}/ctfs/asis2017/unbreakable/unbreakable) 
>
> [flag.enc.orig]({{ site.baseurl }}/ctfs/asis2017/unbreakable/flag.enc.orig) 

For this challenge we are given an executable and an encoded flag. When we run an executable it gives an error:

```sh
$ ./unbreakable
error, input not found 
```

Let's run it with ```strace```:

```sh
$ strace ./unbreakable
...
open("key", O_RDONLY)                   = -1 ENOENT (No such file or directory)
fstat(1, {st_mode=S_IFCHR|0620, st_rdev=makedev(136, 0), ...}) = 0
write(1, "error, input not found\n", 23error, input not found
) = 23
exit_group(1)                           = ?
+++ exited with 1 +++
```

So it needs a ```key``` file. When we supply one it encodes it and puts the output into ```flag.enc``` - so the ```key``` is not an encryption key, it's the plaintext that gets encoded. Our job is to reverse the process and decode ```flag.enc``` we are supplied.

Reversing the executable in Snowman gives some scary looking encryption code. But before we jump into reversing let's experiment with encoding a little bit. Supplying ```key``` composed of different character combinations produces some interesting results:

**key**|**flag.enc**
---|--------
```A```|```4D 16```
```AA```|```4D 16 16 4D```
```AAA```|```4D 16 16 4D 4D 16```
```B```|```2E 4D```
```BB```|```2E 4D 4D 2E```
```BBB```|```2E 4D 4D 2E 2E 4D```
```AABB```|```4D 16 16 4D 2E 4D 4D 2E```

Things are not that scary any more. We can see that:

* Every character is encoded as a 2-byte sequence
* Every character is encoded independently of others
* The 2-byte sequence is reversed depending on whether the position is odd or even

So what we can do now is simply build a ```key``` file with a full range of bytes, encode it, and use the result as the translation table to reverse the original ```flag.enc```:

```python
from subprocess import call

tab = b""

# build a translation table for full range of bytes, 
# with each byte used twice (to account for odd and even positions)
for x in range(0x100):
	tab += chr(x)*2
	
open("key","wb").write(tab)

call(["./unbreakable"])

conv = open("flag.enc","rb").read()

# build a dictionary out of encoded values
dict = {}
for x in range(0x100):
	dict[conv[x*4:x*4+2]] = x
	dict[conv[x*4+2:x*4+4]] = x
	
puzzle = open("flag.enc.orig","rb").read()

# use the dictionary to decode the flag
out = b""
for x in range(len(puzzle)/2):
	out += chr(dict[puzzle[x*2:x*2+2]])
	
open("flag.dec","wb").write(out)
```

Once the flag file is recovered it turns out that it is a PNG image. The image contains the flag - ```ASIS{Ju5t_C0py_And_paSte_on_Sc0rebo4rd!!}```.

![flag.png]({{ site.baseurl }}/ctfs/asis2017/unbreakable/flag.png)