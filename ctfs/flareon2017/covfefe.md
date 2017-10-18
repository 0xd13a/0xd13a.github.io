---
layout: page
title: "FLARE-ON 2017 Writeup: covfefe.exe"
---

At a first glance this challenge looks simple - the code is just a few pages long. However the closer look reveals the dreaded [Subleq esolang](https://esolangs.org/wiki/Subleq). The code is essentially an interpreter for the "machine code" with thousands of subleq instructions :worried:

Interpreter simplicity, however, is our ally here. We can quickly implement it in Python and run the program while printing out our own debug information - basically take full control of the execution. Here I implemented a simple interpreter for the program, and added a bunch of debug output that helped me analyze the logic: [interp.py]({{ site.baseurl }}/ctfs/flareon2017/covfefe/interp.py)

We can run the program with a bunch of inputs:

```
C:\work\flareon17\covfefe>\Python27\python.exe interp.py test
Welcome to the magic box.
Enter the password: test
Password Failed. You have used insufficient computer science. Have you considered a career in sales instead?
```

Running it with a ```-t``` parameter prints out the full run trace; we can capture a number of them and compare them to analyze what is happening:

```
C:\work\flareon17\covfefe>\Python27\python.exe interp.py -t test
    463: 0(0) 0(0) =(0) 467 jmp 467
    467: 466(0) 466(0) =(0) 0 next 46a
    46a: 476(5) 0(0) =(fffffffb) 0 next 46d
    46d: 0(fffffffb) 466(0) =(5) 0 next 470
    470: 0(fffffffb) 0(fffffffb) =(0) 0 next 473
    473: 0(0) 0(0) =(0) 477 jmp 477
    477: 49b(0) 49b(0) =(0) 0 next 47a
    47a: a1(9d) 0(0) =(ffffff63) 0 next 47d
    47d: 0(ffffff63) 49b(0) =(9d) 0 next 480
    480: 0(ffffff63) 0(ffffff63) =(0) 0 next 483
    483: 49c(0) 49c(0) =(0) 0 next 486
...
```

As we look over the execution traces a number of interesting facts are discovered:

- ```0x111``` is the location of the error message that is printed when the input is incorrect

- ```0xefa``` is the location of each character to be printed

- Read input characters are stored in the address range [```0x4d```,```0x6c```]

- The following address ranges are loops that contribute to decoding of individual characters:  [```0xb50```,```0xc77```], [```0xca1```,```0xdc8```], [```0xa79```,```0xab1```], and [```0xaf6```,```0xb32```]

When we run the program with those locations annotated we can see that when the input string is accepted by the program it is processed two characters at a time. Those characters are put through the 4 loops that we identified followed by a small block of instructions that looks like it is checking correctness of characters that were specified. After that the next 2 characters are retrieved and the process repeats.

In that small block of instructions there is an interesting sequence of steps:

```
...
def: e98(f7de) e99(35e8a) =(266ac) 0 next df2
df2: e99(266ac) 0(0) =(fffd9954) df8 jmp df8
df8: 0(fffd9954) 0(fffd9954) =(0) 0 next dfb
dfb: 0(0) e99(266ac) =(266ac) e04 next dfe
dfe: ead(1) ead(1) =(0) 0 next e01
...
```

Note that at ```0xdef``` we are subtracting value at location ```0xe98``` from value at location ```0xe99```. Value at location ```0xe98``` changes as we supply different first 2 characters as input. In this run they did not match and what looks like a "success" flag is cleared at location ```0xead```. This means that we can "bruteforce" the characters to make the values at position ```0xdef``` match, which will leave the "success" flag untouched. (Bruteforcing here does not mean blind checking of all possible character combinations but rather trying of most likely candidates in order to approximate the values)

Within several attempts we find that characters ```su``` make the values match:

```
...
def: e98(35e8a) e99(35e8a) =(0) 0 next df2
df2: e99(0) 0(0) =(0) df8 jmp df8
df8: 0(0) 0(0) =(0) 0 next dfb
dfb: 0(0) e99(0) =(0) e04 jmp e04
...
```

We record them and move on to next section in the trace and repeat the same process for the next 2 characters. With this approach we can recover the complete input string within an hour or two: ```subleq_and_reductio_ad_absurdum```. Entering it as the input to the original application gives us the flag:

```
C:\work\flareon17\covfefe>covfefe.exe
Welcome to the magic box.
Enter the password: subleq_and_reductio_ad_absurdum
The password is correct. Good Job. You win Flare-On 4.
Your key to victory is: subleq_and_reductio_ad_absurdum@flare-on.com
```


