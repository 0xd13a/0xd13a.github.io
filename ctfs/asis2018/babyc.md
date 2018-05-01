---
layout: page
title: "ASIS CTF Quals 2018 Writeup: baby C"
---

> baby C
> 
> This [babyc]({{ site.baseurl }}/ctfs/asis2018/babyc/babyc.tar.xz) needed swaddling!
> 
> flag is ASIS{sha1(input[:14])}

We are supplied with an executable that asks for a string and then responds with a ```Wrong!``` or ```Correct :)```:

```
$ ./babyc
test
Wrong!
```

Let's load it into IDA Pro. The code is a long sequence of ```MOV``` statements, so it's likely protected by [Movfuscator](https://github.com/xoreaxeaxeax/movfuscator):

```
...
.text:0804838A                 mov     word ptr dword_81F5FF8, dx
.text:08048391                 mov     dword_81F5FFE+2, edx
.text:08048397                 mov     ax, word ptr dword_81F5FF0+2
.text:0804839D                 mov     cx, word ptr dword_81F5FF4+2
.text:080483A4                 mov     edx, off_8060F30[eax*4]
.text:080483AB                 mov     edx, [edx+ecx*4]
.text:080483AE                 mov     cx, word_81F6002
.text:080483B5                 mov     edx, off_8060F30[edx*4]
.text:080483BC                 mov     edx, [edx+ecx*4]
.text:080483BF                 mov     word ptr dword_81F5FF8+2, dx
.text:080483C6                 mov     dword_81F5FFE+2, edx
.text:080483CC                 mov     eax, dword_81F5FF8
.text:080483D1                 mov     eax, eax
.text:080483D3                 mov     dword_81F6110, eax
.text:080483D8                 mov     eax, offset off_83F6130
.text:080483DD                 mov     edx, dword_83F6158
...
```

Running [demovfuscator](https://github.com/kirschju/demovfuscator) over it helps clean up the code a little bit. Let's take a closer look at the updated code and debug the executable. 

A few things become apparent:

* String at position ```3``` is expected to be ```m0vfu3c4t0r!```

```
...
.text:0804933D                 mov     eax, offset aM0vfu3c4t0r ; "m0vfu3c4t0r!"
...
.text:08049552                 mov     dword_83F6174, eax
.text:08049557                 mov     eax, off_83F6170[edx*4]
.text:0804955E                 mov     edx, dword_81F6110
.text:08049564                 mov     [eax], edx
.text:08049566                 mov     esp, off_83F6130
.text:0804956C                 mov     dword_85F61C4, offset _strncmp
.text:08049576                 mov     eax, 1
.text:0804957B                 jmp     sub_8048290
...
```

* First 3 characters are expected to be ```Ah_```

```
...
.text:080498DB                 mov     dword_804D058, 41h ; 'A'
...
.text:08049C05                 mov     dword_804D058, 68h ; 'h'
...
.text:08049F2F                 mov     dword_804D058, 5Fh ; '_'
...
```

* String at position ```19``` is expected to be ```0y1ng:(``` (that string is built on the fly):

```
...
.text:0804A3FB                 mov     dword_83F6174, eax
.text:0804A400                 mov     eax, off_83F6170[edx*4]
.text:0804A407                 mov     edx, dword_81F6110
.text:0804A40D                 mov     [eax], edx
.text:0804A40F                 mov     esp, off_83F6130
.text:0804A415                 mov     dword_85F61C4, offset _strncmp
.text:0804A41F                 mov     eax, 1
.text:0804A424                 jmp     sub_8048290
...
```

* Finally the 4 characters at position ```17``` are expected to be ```nn0y```:

```
...
.text:0804A7BD                 mov     dword_804D058, 79306E6Eh ; 'nn0y'
...
```

When we put all these requirements together the expected string turns out to be ```Ah_m0vfu3c4t0r!..nn0y1ng:(``` (the 2 periods in the middle are not checked and can be anything):

```
$ ./babyc
Ah_m0vfu3c4t0r!_Ann0y1ng:(

Correct :)
```

The flag is the SHA1 of the first ```14``` characters: ```ASIS{574a1ebc69c34903a4631820f292d11fcd41b906}```
