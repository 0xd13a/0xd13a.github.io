---
layout: page
title: "Pwn2Win 2017 Writeup: Baby Regex"
---

>Baby Regex
>
>Our team has gotten hands on this text and we know that it has been used by BloodSuckers Corp. as an admittance test for their applicants in the Counter Intelligence(CI) team, since these guys have an eye for pattern recognition! Get this achievement by helping the members of Rebellious Fingers that will soon try to infiltrate the CI team and will need this test's results.
>
>[Link]({{ site.baseurl }}/ctfs/pwn2win2017/baby_regex/regexbaby.txt)
>
>Pay close attention to the text in order to capture the sequence as it appears, understanding these instructions is also part of the challenge. The engine used is python2, everything is working as expected!
>
>P.S: In the challenge, wildcard refers exclusively to the asterisk ( * ).
>
>Server: nc 200.136.213.148 5000
>
>Id: baby_regex
>
>Total solves: 19
>
>Score: 298
>
>Categories: Misc

This was a terrific refresher of finer points of regex expression syntax. One catch may be the specific Python function to use when verifying your expressions - not all of them behave the same way in all cases. I asked the CTF admins and they recommended using ```re.findall()``` function, which helped me overcome some discrepancies I saw in testing expressions locally vs. testing against the server.

To test expressions locally I threw together a small script:

```python
import re,sys

data = open("regexbaby.txt","r").read()

print sys.argv[1], "len: %d" % len(sys.argv[1])

m = re.findall(sys.argv[1], data)

for x in m:
	print x
```

After a lot of trial and error the following expressions worked (note that the expression with 38 characters has a trailing space):

```
$ nc 200.136.213.148 5000

Type the regex that capture: "from "Drivin" until the end of phrase, without 
using any letter, single quotes or wildcards, and capturing "Drivin'" in a 
group, and "blue." in another", with max. "16" chars: (.{7}).+-(.{5})$
Nice, next...
Type the regex that capture: "All "Open's", without using that word or [Ope-], 
and no more than one point", with max. "11" chars: (?i)(oPEn)
Nice, next...
Type the regex that capture: "(BONUS) What's the name of the big american 
television channel (current days) that matchs with this regex: .(.)\1", with 
max. "x" chars: CNN
Nice, next...
Type the regex that capture: "Chips" and "code.", and it is only allowed the 
letter "c" (insensitive)", with max. "15" chars: ([cC].{4})\n\n
Nice, next...
Type the regex that capture: "the follow words: "unfolds", "within" (just one 
time), "makes", "inclines" and "shows" (just one time), without using hyphen, 
a sequence of letters (two or more) or the words itself", with max. "38" chars: 
(?:t|d) ([^F]\w{2,5}(?:i|e|w|d)[^d]) 
Nice, next...
Type the regex that capture: "the only word that repeat itself in the same 
word, using a group called "a" (and use it!), and the group expression must 
have a maximum of 3 chars, without using wildcards, plus signal, the word 
itself or letters different than [Pa]", with max. "16" chars: (?P<a>..a)(?P=a)
Nice, next...
Type the regex that capture: "FLY until... Fly", without wildcards or the word 
"fly" and using backreference", with max. "14" chars: (FL.).+(?i)\1
Nice, next...
Type the regex that capture: "<knowing the truth. >, without using "line break"", 
with max. "8" chars: <[^z]*>
Nice, next...
CTF-BR{Counterintelligence_wants_you!}
```

The flag is ```CTF-BR{Counterintelligence_wants_you!}```.