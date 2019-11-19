---
layout: page
title: "ASIS CTF Finals 2019 Writeup: Snake"
---

> Snake
>
> 253
>
> Hercules travelled to the city of snakes to find a venomous, snake which lived underwater. For this task, 
> Hercules was given a handbook and a mysterious box in order to find out about the snake.
> Can you help him to find the way to defeat the snake?
> 
> You need this information to solve the task:
> 
> File: /bin/bash
>
> Size: 1113504 Blocks: 2176 IO Block: 4096 regular file
>
> Device: 806h/2054d Inode: 1050418 Links: 1
>
> Access: (0755/-rwxr-xr-x) Uid: ( 0/ root) Gid: ( 0/ root)
>
> Access: 2019-11-16 00:09:47.788589318 +0330
>
> Modify: 2019-06-07 02:58:15.000000000 +0430
>
> Change: 2019-07-09 09:30:05.424100060 +0430
>
> Birth: -
> 
> Download: [venomous_snake.txz]({{ site.baseurl }}/ctfs/asis2019/snakes/venomous_snake.txz)

This is another reversing challenge, but decoding of the flag requires a lot of dynamic operations, so we will use Ghidra together with GDB or EDB.

Close inspection reveals a number of interesting functions:

* ```FUN_001012f5()``` is the main function where all processing happens
* ```FUN_00101102()``` does some intial checks on the correctness of the environment
* ```FUN_00100d6a()``` initializes internal structures for decryption functions
* ```FUN_00100dcb()``` decrypts piece of data correctly only when conditions are right
* ```FUN_00100ec2()``` decrypts piece of data independent of whether the conditions are set correctly (less important data is decoded using it)
* ```FUN_00100fb6()``` decodes the shell script that will produce the flag

Let's annotate the main function with notes of how to carefully step over dangerous parts:

```c

...

undefined * FUN_001012f5(uint param_1,char **param_2)
{

...

// This function checks the integrity of its own code, so the presence
// of the breakpoint could mess things up - we can use hardware 
// breakpoints, or step over this function altogether.
  local_3c = FUN_00101102((ulong)param_1); 
  
  FUN_00100d6a();
  FUN_00100dcb(&DAT_0030238c,0x100);
  
// Decode a message about executable being expired  
  FUN_00100ec2(&DAT_003024d6,0x3c); 

// Decode a constant timestamp
  FUN_00100ec2(&DAT_00302572,0xb);
  
// Verify that the current time is older than the timestamp - jump over this code
  if (DAT_00302572 != '\0') {
    lVar2 = atoll(&DAT_00302572);
    tVar3 = time((time_t *)0x0);
    if (lVar2 < tVar3) {
      return &DAT_003024d6;
    }
  }

// Decode text '/bin/bash'
  FUN_00100ec2(&DAT_0030252b,10);
  
// Decode text '-c'
  FUN_00100ec2(&DAT_003024ad,3);
  
// Decode 'exec' command  
  FUN_00100ec2(&DAT_00302518,0xf);
  
  FUN_00100ec2(&DAT_00302514,1);
  
  FUN_00100ec2(&DAT_0030253b,0x16);
  
// Decode a sanity check constant 
  FUN_00100dcb(&DAT_0030253b,0x16);
  FUN_00100ec2(&DAT_00302557,0x16);
  iVar1 = memcmp(&DAT_0030253b,&DAT_00302557,0x16);
  if (iVar1 == 0) {
  
// Decode another sanity check
    FUN_00100ec2(&DAT_003024b4,0x13);
    if (local_3c < 0) {
      puVar4 = &DAT_003024b4;
    }
    else {
	
// At this point program starts to build external command to execute
      __argv = (char **)calloc((long)(int)(param_1 + 10),8);

...

          FUN_00100ec2(&DAT_00302513,1);
		  
// Decode the in-memory script that will help produce the flag (see below)		  
          if ((DAT_00302513 == '\0') && (iVar1 = FUN_00100fb6(&DAT_0030252b), iVar1 != 0)) {
            return &DAT_0030252b;
          }
		  
// Decode remaining pieces of the command to execute - at this point
// we can simply extract the script we need from memory and execute it
          FUN_00100ec2(&DAT_0030257f,1);
          FUN_00100ec2(&DAT_003020c3,0x283);
          FUN_00100ec2(&DAT_00302581,0x13);
          FUN_00100dcb(&DAT_00302581,0x13);
          FUN_00100ec2(&DAT_00302022,0x13);
          iVar1 = memcmp(&DAT_00302581,&DAT_00302022,0x13);
          if (iVar1 != 0) {
            return &DAT_00302581;
          }
          local_30 = (char *)malloc(0x1283);
          if (local_30 == (char *)0x0) {
            return (undefined *)0;
          }
          memset(local_30,0x20,0x1000);
          memcpy(local_30 + 0x1000,&DAT_003020c3,0x283);
        }
...
``` 

Function ```FUN_00100fb6()``` is of particular interest, it gets a ```stat``` of ```/bin/bash``` and decodes the hidden script based on its data. Here is the stat data on my machine:

```
00007fff:ffffe140|01 08 00 00 00 00 00 00 71 02 1a 00 00 00 00 00|........q.......| Device ID, Inode
00007fff:ffffe150|00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00|................|
00007fff:ffffe160|00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00|................|
00007fff:ffffe170|c8 04 11 00 00 00 00 00 00 00 00 00 00 00 00 00|................| File size
00007fff:ffffe180|00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00|................|
00007fff:ffffe190|00 00 00 00 00 00 00 00 3a b3 26 5b 00 00 00 00|........:.&[....| Modification time
00007fff:ffffe1a0|00 00 00 00 00 00 00 00 76 1d 1b 5c 00 00 00 00|........v..\....| Creation time
00007fff:ffffe1b0|00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00|................|
00007fff:ffffe1c0|00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00|................|
```

Correct values were specified in the task description. We can replace them in memory as we debug the program:

```
00007fff:ffffe140|06 08 00 00 00 00 00 00 32 07 10 00 00 00 00 00|........q.......|
00007fff:ffffe150|00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00|................|
00007fff:ffffe160|00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00|................|
00007fff:ffffe170|a0 fd 10 00 00 00 00 00 00 00 00 00 00 00 00 00|................|
00007fff:ffffe180|00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00|................|
00007fff:ffffe190|00 00 00 00 00 00 00 00 7f 93 f9 5c 00 00 00 00|........:.&[....|   
00007fff:ffffe1a0|00 00 00 00 00 00 00 00 55 1f 24 5d 00 00 00 00|........v..\....|
00007fff:ffffe1b0|00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00|................|
00007fff:ffffe1c0|00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00|................|
```

With the correct data set, the script is correctly decoded in memory:

```sh
#!/bin/bash
# In the name of Allah
secret='L5rI#`8D+e4FqFQy.t?E'
top_secret='s7.#"{EE*(T+H!R\c#e=WMd^C'
version=`openssl version | cut -d" " -f 2`
if [[ $version == '1.1.1d' ]]; 
then
  if [[ $# -eq 1337 ]]; 
  then
    if [[ $2 == 'unl0ck_M3__PlE4ze__n0W' ]];
	then
	  openssl enc -aes-256-cbc -nosalt -d -in $1 -k $secret
	  echo ''
	  echo 'Your file unlocked successfully'
	fi
    if [[ $114 == 'l3T_m3_kN0w_fL49_Pl3Az3' ]];
    then
      openssl enc -aes-256-cbc -nosalt -d -in 'asis_flag.enc' -iter $((114 * ${#top_secret})) -k $top_secret
    fi
  else
    echo 'Try harder!!'
  fi
else
  echo 'Your OS is not satisfied to run this program, sorry!'
fi
```

We don't need to run the whole script as it requires that we set up a crazy number of parameters, let's simply run the important part of it:

```sh
$ openssl enc -aes-256-cbc -nosalt -d -in asis_flag.enc -iter 2850 -k 's7.#"{EE*(T+H!R\c#e=WMd^C' > flag.png
```

An image is produced and it contains the flag: ```ASIS{Rans0mw4R3_1nf3c7_tHE_W0rlD}```.
