---
layout: page
title: "FLARE-ON 2017 Writeup: zsud.exe"
---

Here we are presented with a maze traversal game:

![zsud.png]({{ site.baseurl }}/ctfs/flareon2017/zsud/zsud.png)

In the game we travel through Mandiant offices, examining different artifacts. In the East side of the office there is an endless maze of cubicles. As we later find out, by successfully traversing it we will decrypt the flag.

This challenge took me a long time because of all the wrong turns I took and the rabbit holes that I followed. I will describe the more straightforward way of analyzing it.

When we run the application we can take its memory dump using a tool like Sysinternals Process Explorer. If we search the dump for strings that we see in the maze screen we can find a PowerShell script that contains the application logic:

![zsud2.png]({{ site.baseurl }}/ctfs/flareon2017/zsud/zsud2.png)

Let's dump it out and study it: [script.ps1]({{ site.baseurl }}/ctfs/flareon2017/zsud/script.ps1)

Analysis reveals a whole lot of gems:

- Line 156 - it looks like there is a key hidden in the desk drawer of the lobby.

- Line 585 - as we walk through the office the turns that we take are combined together to be sent to an internal HTTP handler and to decrypt a part of some hidden message

- Line 463 - when talking to Kevin while wearing the helmet and after dropping the key in his office we will get some hexadecimal message (this will later turn out to be the flag)

- Line 582 - the steps seem to be guided by the sequence of ```rand()``` numbers (after PRNG is initialized with ```srand(42)``` at Line 432). 

- Line 602 - as we traverse the office with the key it prints out the message "The key emanates some warmth..." when we take the correct turn. This message can be used as a correct step "[oracle](https://en.wikipedia.org/wiki/Oracle_machine)".

I made a few mistakes after analyzing the script. First I tried to bruteforce the steps through the maze, which quickly became unmanageable as the step sequence grew. ```rand()``` logic stumped me for a while because I didn't notice the ```%6``` pushed all the way to the right (sneaky!). Reversing the logic of the HTTP handler took a while and could have been avoided altogether. Generating a sequence of ```rand()``` numbers from MSVCRT did not work because the application did something to replace the MSVCRT of rand() (the numbers it generated did not match successful steps), and I didn't have time to analyze the reason for discrepancy.

In the end the successful strategy was to simply make use of step oracle. As it turns out we can join individual commands a comma (see line 741) and try steps in sequence until we get a successful message from the oracle. If we don't succeed just exit the app and run it again, if we do - add the successful step to the total step sequence, and try the next step. When we restart the app just paste in the successful step sequence known so far, and within the next 1-3 tries we will know the next successful step. 

Here is an example of the first successful step (note the message from the key):

```
...
> get key drawers
You get a key.

> w
You go west
The key emanates some warmth...
the southwest hallway
This hallway links the snack/lunch area with the lobby.  

Exits: North East 
...
```

This is not a high-tech way of doing this, but it's ok - within the next hour or so we will know the full sequence. Solving CTF challenges is not always about fully understanding the logic; luck, imagination, and bruteforcing are also involved. :smile:

When we know the full path through the maze we land back at the beginning and the following message is displayed:

```
...
> u
...
You go up
The key emanates some warmth...
work area
This is someone's cubicle. The desk has a laptop docked on it, and is festooned with the personal effects of whoever works here. The whiteboard has a humorous drawing on it.  

Exits: North South East West Up Down 

> n
You go north
The key emanates some warmth...
the vestibule
This is surely the entrance to a great company.  

Exits: North South 

> l key
You can start to make out some words but you need to follow the RIGHT_PATH!@66696e646b6576696e6d616e6469610d0a
...
```

Number ```66696e646b6576696e6d616e6469610d0a``` in the key message decodes to ```findkevinmandia```. We now need to go and talk to Kevin.

The following is the full sequence of commands to execute from the very beginning: ```n,get key drawers,w,n,n,e,e,s,s,s,n,e,w,n,e,e,w,w,w,d,u,n,d,u,n,d,u,n,s,u,n,e,u,n,s,e,w,d,u,n,s,e,w,s,e,w,s,e,w,s,e,w,d,u,n,l key,n,w,n,n,e,e,n,get helmet,wear helmet,drop key,say kevin hi```.

When we talk to Kevin we get the following output:

```
...
> wear helmet
You put the helmet on your head. It looks objectively awesome.

> drop key
You drop a key

> say kevin hi

Kevin says, with a nod and a wink: '6D 75 64 64 31 6E 67 5F 62 79 5F 79 30 75 72 35 33 6C 70 68 40 66 6C 61 72 65 2D 6F 6E 2E 63 6F 6D'.

Bet you didn't know he could speak hexadecimal! :-)
```

Converting hex numbers to characters reveals the flag: ```mudd1ng_by_y0ur53lph@flare-on.com```.