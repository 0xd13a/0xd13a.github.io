---
layout: page
title: "ASIS CTF Quals 2018 Writeup: Echo"
---

> Echo
> 
> The simplest [echo]({{ site.baseurl }}/ctfs/asis2018/echo/echo.tar.xz) in the world.
>
> Note: flag{whatyoufound}, submit ASIS{sha1(whatyoufound)}

The executable requires a command line parameter:

```
$ ./Echo
Missing argument
```

Quick look inside reveals the needed argument. When it is specified the executable echoes back everything we enter:

```
$ ./Echo GIVEMEFLAG
a
a
b
b
abc
abc
```

Let's analyze the code more closely and debug. First, notice that an encoded flag blob is built in memory on the fly:

```
...
.text:000055A06C699DC2                 mov     [rbp+var_274A], 15h
.text:000055A06C699DC9                 mov     [rbp+var_2749], 0F3h
.text:000055A06C699DD0                 mov     [rbp+var_2748], 1
.text:000055A06C699DD7                 mov     [rbp+var_2747], 0EBh
.text:000055A06C699DDE                 mov     [rbp+var_2746], 0CEh
.text:000055A06C699DE5                 mov     [rbp+var_2745], 0C5h
.text:000055A06C699DEC                 mov     [rbp+var_2744], 0Dh
.text:000055A06C699DF3                 mov     [rbp+var_2743], 0C6h
.text:000055A06C699DFA                 mov     [rbp+var_2742], 0C7h
.text:000055A06C699E01                 mov     [rbp+var_2741], 0C1h
.text:000055A06C699E08                 mov     [rbp+var_2740], 0CBh
.text:000055A06C699E0F                 mov     [rbp+var_273F], 0F4h
.text:000055A06C699E16                 mov     [rbp+var_273E], 0D8h
.text:000055A06C699E1D                 mov     [rbp+var_273D], 0C2h
.text:000055A06C699E24                 mov     [rbp+var_273C], 0DBh
.text:000055A06C699E2B                 mov     [rbp+var_273B], 0F6h
.text:000055A06C699E32                 mov     [rbp+var_273A], 0C6h
.text:000055A06C699E39                 mov     [rbp+var_2739], 0BFh
.text:000055A06C699E40                 mov     [rbp+var_2738], 0FEh
.text:000055A06C699E47                 mov     [rbp+var_2737], 0FFh
.text:000055A06C699E4E                 mov     [rbp+var_2736], 12h
.text:000055A06C699E55                 mov     [rbp+var_2735], 0Ch
.text:000055A06C699E5C                 mov     [rbp+var_2734], 0EAh
.text:000055A06C699E63                 mov     [rbp+var_2733], 0F8h
.text:000055A06C699E6A                 mov     [rbp+var_2732], 0F9h
.text:000055A06C699E71                 mov     [rbp+var_2731], 11h
.text:000055A06C699E78                 mov     [rbp+var_274E], 66h ; 'f'
.text:000055A06C699E7F                 mov     [rbp+var_274D], 6Ch ; 'l'
.text:000055A06C699E86                 mov     [rbp+var_274C], 61h ; 'a'
.text:000055A06C699E8D                 mov     [rbp+var_274B], 67h ; 'g'
...
```

```
00007FFDA51FDD10  00 00 66 6C 61 67 15 F3  01 EB CE C5 0D C6 C7 C1  ..flag..........
00007FFDA51FDD20  CB F4 D8 C2 DB F6 C6 BF  FE FF 12 0C EA F8 F9 11  ......? ........
```

As the program runs, function ```sub_55AE72994970``` generates a sequence of characters that turns out to be [Brainf*ck](https://en.wikipedia.org/wiki/Brainfuck) code. It is then executed by a simple embedded interpreter:

```
>>[<+<+>>-]<<[->>+<<]>[>>>>>+<<<<<-]<>>>[<<+<+>>>-]<<<[->>>+<<<]>[>>>
>>>+<<<<<<-]<>>>>[<<<+<+>>>>-]<<<<[->>>>+<<<<]>[>>>>>>>+<<<<<<<-]<>>>
>>[<<<<+<+>>>>>-]<<<<<[->>>>>+<<<<<]>[>>>>>>>>+<<<<<<<<-]<>>[<+<+>>-]
<<[->>+<<]>[>>>>>>>>>+<<<<<<<<<-]<>>>[<<+<+>>>-]<<<[->>>+<<<]>[>>>>>>
>>>>+<<<<<<<<<<-]<>>>>[<<<+<+>>>>-]<<<<[->>>>+<<<<]>[>>>>>>>>>>>+<<<<
<<<<<<<-]<>>>>>[<<<<+<+>>>>>-]<<<<<[->>>>>+<<<<<]>[>>>>>>>>>>>>+<<<<<
<<<<<<<-]<>>[<+<+>>-]<<[->>+<<]>[>>>>>>>>>>>>>+<<<<<<<<<<<<<-]<>>>[<<
+<+>>>-]<<<[->>>+<<<]>[>>>>>>>>>>>>>>+<<<<<<<<<<<<<<-]<>>>>[<<<+<+>>>
>-]<<<<[->>>>+<<<<]>[>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<-]<>>>>>[<<<<+<+>
>>>>-]<<<<<[->>>>>+<<<<<]>[>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<-]<>>[<+<
+>>-]<<[->>+<<]>[>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<<-]<>>>[<<+<+>>>-]
<<<[->>>+<<<]>[>>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<<<-]<>>>>[<<<+<+>>>
>-]<<<<[->>>>+<<<<]>[>>>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<<<<-]<>>>>>[
<<<<+<+>>>>>-]<<<<<[->>>>>+<<<<<]>[>>>>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<
<<<<<<<-]<>>[<+<+>>-]<<[->>+<<]>[>>>>>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<
<<<<<<<-]<>>>[<<+<+>>>-]<<<[->>>+<<<]>[>>>>>>>>>>>>>>>>>>>>>>+<<<<<<<
<<<<<<<<<<<<<<<-]<>>>>[<<<+<+>>>>-]<<<<[->>>>+<<<<]>[>>>>>>>>>>>>>>>>
>>>>>>>+<<<<<<<<<<<<<<<<<<<<<<<-]<>>>>>[<<<<+<+>>>>>-]<<<<<[->>>>>+<<
<<<]>[>>>>>>>>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<<<<<<<<<-]<>>[<+<+>>-]
<<[->>+<<]>[>>>>>>>>>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<<<<<<<<<<-]<>>>
[<<+<+>>>-]<<<[->>>+<<<]>[>>>>>>>>>>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<
<<<<<<<<<<-]<>>>>[<<<+<+>>>>-]<<<<[->>>>+<<<<]>[>>>>>>>>>>>>>>>>>>>>>
>>>>>>+<<<<<<<<<<<<<<<<<<<<<<<<<<<-]<>>>>>[<<<<+<+>>>>>-]<<<<<[->>>>>
+<<<<<]>[>>>>>>>>>>>>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<<<<<<<<<<<<<-]<
>>[<+<+>>-]<<[->>+<<]>[>>>>>>>>>>>>>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<
<<<<<<<<<<<<<-]<>>>[<<+<+>>>-]<<<[->>>+<<<]>[>>>>>>>>>>>>>>>>>>>>>>>>
>>>>>>+<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<-]<>>>>>>>[+]>[+]>[+]>[+]>[+]>[+
]>[+]>[+]>[+]>[+]>[+]>[+]>[+]>[+]>[+]>[+]>[+]>[+]>[+]>[+]>[+]>[+]>[+]
>[+]>[+]><<<<<<<<<<<<<<<<<<<<<<<<<,[.,]
```

The last sequence in the script is what produces the echo behavior in a loop - ```[.,]```.

When the Brainf*ck code is executed one would expect the flag to be revealed but instead it's cleared out competely:

```
00007FFDA51FDD10  00 00 66 6C 61 67 7B 00  00 00 00 00 00 00 00 00  ..flag{.........
00007FFDA51FDD20  00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00  ................
```

It turns out that the repeated sequence ```>[+]``` in the script clears the flag contents (it essentilly means ```"shift to the right and increment the value in a loop until you hit 0"```). Once we replace that sequence with ```>>>>``` and re-execute we can capture the true flag contents:

```
00 00 66 6C 61 67 7B 5F  62 52 34 31 6E 2D 2D 2D  ..flag{_bR41n---
2C 5B 3E 2E 3C 5D 2C 2B  5F 66 78 78 4B 5F 5F 7D  ,[>.<],+_fxxK__}
```

The final flag is the SHA1 of the string that we found: ```ASIS{7928cc0d0f66530a42d5d3a06f94bdc24f0492ff}```.