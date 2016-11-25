---
layout: page
title: RC3 CTF 2016 Writeup: Klaatu Barada N...
---

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
