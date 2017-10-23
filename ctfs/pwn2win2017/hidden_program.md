---
layout: page
title: "Pwn2Win 2017 Writeup: Hidden Program"
---

This challenge demonstrates the difficulties associated with proper type conversion. We are given an address of a server and its [source code]({{ site.baseurl }}/ctfs/pwn2win2017/hidden_program/hiddenprogram.c). The server program asks for a string position, a string and a substring and then either confirms that the substring is found or prints what is found in the larger string at the given position.

In the source code we can see that the flag is stored right before the larger string so we need to find a way to print it:

```c
typedef struct
{
    char flag[SHRT_MAX+1];
    char in[SHRT_MAX+1];
    char sub[SHRT_MAX+1];
    int n;
} player;
```

When user enters the position it is checked for being over the maximum short number (```p1.n>SHRT_MAX```), which is ```32767```. However it is not checked for being incorrect negative position. The further attempt to make the number positive (```(short)abs((short)p1.n)```) does not work as it should have at the first glance, leaving the negative number the user enters intact.

This means that we can pass in a negative number to the application, and it will print out the substring _before_ the ```in``` field, not within it. Because flag field is ```SHRT_MAX+1=32768``` bytes long, passing in the index ```-32768``` does the trick and the flag ```CTF-BR{Th1s_1S_4_50_5Imp13_C_exp1017_}``` is printed:


```
$ nc 200.136.213.126 1988
Insert a short integer: -32768
Insert a string: a
Insert another string: a
	You lost!!!
        In the string a the substring in the position -32768 is CTF-BR{Th1s_1S_4_50_5Imp13_C_exp1017_}
        Try again...
```