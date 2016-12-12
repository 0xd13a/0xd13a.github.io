---
layout: page
title: "SECCON 2016 Online CTF Writeup: Memory Analysis"
---

> 100 points
> 
> Find the website that the fake svchost is accessing.
> 
> You can get the flag if you access the website!!
>
> memoryanalysis.zip
>
> The challenge files are huge, please download it first.
> 
> Hint1: http://www.volatilityfoundation.org/
>
> Hint2: Check the hosts file
>
> password: fjliejflsjiejlsiejee33cnc 

The hints reveal a lot of what should be done for this challenge. The attached file is a Windows memory dump that we open in [HxD](https://mh-nexus.de/en/hxd/). 

Windows hosts file (```C:\Windows\System32\drivers\etc\hosts```) usually contains a header (```# This is a sample HOSTS file```) so let's search for it. 

Bingo! We find it and the contents indicate that they set up a host entry for a specific IP address:

```
153.127.200.178    crattack.tistory.com
```

However opening this IP in the browser shows what seems like an unconfigured web server:

![Site]({{ site.baseurl }}/ctfs/seccon2016/memory-analysis/site.png)

Let's search the memory dump again for the URLs that point to that IP or to ```crattack.tistory.com``` to see if we get lucky. A bunch of them are found, and we try them one by one, replacing the site name with the IP address. 

Lo and behold, we find the flag in ```http://153.127.200.178/entry/Data-Science-import-pandas-as-pd```:

```SECCON{_h3110_w3_h4ve_fun_w4rg4m3_}```
