---
layout: page
title: "Pwn2Win 2017 Writeup: Top Secret"
---

>Top Secret
>
>Molly was able to take pictures of a strange digital circuit sketch, along with an also strange message. All of these things were inside an envelope in a safe, which was labeled "Top Secret".
>
>We believe it might contain Butcher Corp's plans for the future, can you help us read the message?
>
>[Link]({{ site.baseurl }}/ctfs/pwn2win2017/top_secret/topsecret.tar.gz)
>
>Id: top_secret
>
>Total solves: 40
>
>Score: 223
>
>Categories: Eletronics

At first this challenge seemed intimidating because I forgot most of my high school physics :smile: but it turned out that it requires very little physics or electronics knowledge.

We are given a circuit diagram with 9 input pins (0-8) and a text file with binary data representing signals sent to those pins:

```
0 1 2 3 4 5 6 7 8

0 1 0 0 1 1 1 0 0
1 1 0 0 1 1 1 0 0
0 1 0 0 1 1 1 0 0
1 1 1 1 0 0 0 1 0
0 1 0 0 1 1 1 0 0
1 1 1 0 1 1 1 0 0
0 1 0 0 1 1 1 0 0
1 1 1 0 1 1 1 0 0
0 1 0 0 1 1 1 0 0
1 1 1 0 1 1 1 0 0
0 1 0 0 1 1 1 0 0
1 1 1 0 1 1 1 0 0
0 1 0 0 1 1 1 0 0
1 1 1 1 0 0 0 1 0
0 1 0 0 1 1 1 0 0
1 1 0 0 1 1 1 0 0

...
```

From the diagram we can see that pins 1,4,5, and 6 have single transistors going in and the others have 2 transistors chained. From my limited knowledge the single transistor inverts the signal, and therefore two transistors chained essentially leave it untouched. Let's invert columns 1,4,5, and 6 in the first block of the message:

```
0 1 2 3 4 5 6 7 8

0 0 0 0 0 0 0 0 0
1 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0
1 0 1 1 1 1 1 1 0
0 0 0 0 0 0 0 0 0
1 0 1 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0
1 0 1 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0
1 0 1 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0
1 0 1 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0
1 0 1 1 1 1 1 1 0
0 0 0 0 0 0 0 0 0
1 0 0 0 0 0 0 0 0

...
```

As you can see the image now becomes suspiciously similar to a bitmap of an uppercase letter ```C```, which would also be the first letter in the expected flag prefix. :smile: Continuing to other blocks confirms that they are bitmaps for letters ```T```, ```F```, and so on. The signal in the first column seems to be the indicator of whether other pin signals in the row should be considered at all.

Let's automate the process. The following script inverts proper inputs and cleans up the data, printing the character images:

```python
lines = open("Message.txt","r").readlines()[1:]

for x in lines:
	pieces = x.split()
	
	if len(pieces) == 0 or pieces[0] == "0":
		continue
		
	out = ""
	for y in range(1,len(pieces)):
			
		if y in [1,4,5,6]:
			out += "#" if (pieces[y] == "0") else " " 
		else:
			out += " " if (pieces[y] == "0") else "#"
		
	print out
```

When we run it, clear letter pictures appear:

```
$ python solve.py

 ###### 
 #      
 #      
 #      
 #      
 ###### 

 ###### 
   ##   
   ##   
   ##   
   ##   
   ##   

 #####  
 #      
 #      
 #####  
 #      
 #      
 #      

...
```

The flag is ```CTF-BR{LOCATE_AND_KILL_REMS}```.