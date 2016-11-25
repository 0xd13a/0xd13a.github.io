---
layout: page
title: "RC3 CTF 2016 Writeup: Klaatu Barada N..."
---

> 300 points
>
> Whilst fighting of hordes of Deadites, Ash seems to have forgotten something. Help Ash remember the words, because he'd rather be in Jacksonville.
>
> nc ctf.rc3.club 6050
>
> author:orkulus

"Klaatu Barada Nikto", as it turns out, is a phrase from both [The Day the Earth Stood Still](http://www.imdb.com/title/tt0043456/) and [Army of Darkness](http://www.imdb.com/title/tt0106308/) (CTFs teach you all kinds of trivia :) ). 

The application that we connect to returns a bunch of base64-encoded strings and immediately disconnects. It does not seem to accept any input, so let's proceed on assumption that the flag is encoded in the output. 

The application output changes all the time both in content and in the number of lines returned. However all lines seem to be quotes from "Army of Darkness", and there is a limited set of them. We also notice that the most frequently returned number of lines is 136, which **divides by 8**. So there is a possibility that each line encodes a single bit of data, and 8 lines represent a single character from the flag. Let's verify that.

We capture a sample output, decode and save it into a local file [nikto.txt]({{ site.baseurl }}/ctfs/rc3ctf2016/klaatu-barada-n/nikto.txt). Let's massage it to get a list of unique quotes to work with:

```
root@kali:~/rc3# cat nikto.txt | uniq | sort > nikto_table.txt
```

Now we look at the first 8 lines in ```nikto.txt```:

```
Look, maybe I didn't say every single little tiny syllable, no. But basically I said them, yeah.
I may be bad... but I feel gooood.
Good. Bad. I'm the guy with the gun.
Groovy.
Yo, she-bitch! Let's go!
Oh, you wanna know? 'Cause the answer's easy! I'm BAD Ash... and you're GOOD Ash! You're a goody little two-shoes! Little goody two-shoes! Little goody two-shoes!
I may be bad... but I feel gooood.
Shut up, Linda!
```

If our theory is correct, they are encoding the first letter in ```RC3-2016-xxx```. Letter ```R``` is binary ```01010010```. Let's mark corresponding quotes in ```nikto_table.txt``` with either ```0``` or ```1```:

```
0	Look, maybe I didn't say every single little tiny syllable, no. But basically I said them, yeah.
1	I may be bad... but I feel gooood.
0	Good. Bad. I'm the guy with the gun.
1	Groovy.
0	Yo, she-bitch! Let's go!
0	Oh, you wanna know? 'Cause the answer's easy! I'm BAD Ash... and you're GOOD Ash! You're a goody little two-shoes! Little goody two-shoes! Little goody two-shoes!
0	Shut up, Linda!
```

We then repeat the same process for remaining characters in ```RC3-2016-```. The result can be seen in [nikto_table.txt]({{ site.baseurl }}/ctfs/rc3ctf2016/klaatu-barada-n/nikto_table.txt).

Now let's use these two files to try and decode the flag using the following script:

```python
import binascii

xtable = {}
with open("nikto_table.txt") as f:
	for x in f.readlines():
		bit, line = x.strip().split('\t')
		xtable[line] = bit
		
bitstr = '0b'
with open("nikto.txt") as f:
	for x in f.readlines():
		bitstr += xtable[x.strip()]
		
n = int(bitstr, 2)

print binascii.unhexlify('%x' % n)
```

We run the script and get the flag: ```RC3-2016-CHRLSD3D```

```
root@kali:~/rc3# python nikto.py
RC3-2016-CHRLSD3D
```
