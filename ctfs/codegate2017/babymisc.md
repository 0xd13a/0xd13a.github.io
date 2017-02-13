---
layout: page
title: "Codegate CTF 2017 Prequalification Writeup: BabyMISC"
---

> BabyMISC
> 
> 50 point
>
> [http://ctf.codegate.org/z/BabyMISC]({{ site.baseurl }}/ctfs/codegate2017/babymisc/BabyMISC)
> 
> nc 110.10.212.138 19090
> 
> nc 110.10.212.138 19091

We start by downloading the challenge code and reversing it in [Snowman](http://derevenets.com/). After some cleanup the code reveals that there are 3 stages to the challenge.

### Stage 1

In this stage we are dealing with a hardcoded Base64-encoded string (```TjBfbTRuX2M0bDFfYWc0aW5fWTNzdDNyZDR5Oig=```). Program asks us to enter another Base64-encoded string and will proceed to the next stage if the string is correct:

```c
    __puts("[*] -- STAGE 01 ----------", 0);
    __printf("[+] KEY : %s\n", v5);
    __puts("[+] Input > ", v5);
    rax6 = stdin;
    __setbuf(rax6, 0);
    ____isoc99_scanf("%99s", reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0x80);
    __base64decode("TjBfbTRuX2M0bDFfYWc0aW5fWTNzdDNyZDR5Oig=", reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0x98);
    __base64decode(reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0x80, reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0x90);
    rsi7 = v8;
    __printf("[*] USER : %s\n", rsi7);
    rax9 = __strlen("TjBfbTRuX2M0bDFfYWc0aW5fWTNzdDNyZDR5Oig=", rsi7);
    rdi10 = reinterpret_cast<void*>(reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0x80);
    rax11 = __strlen(rdi10, rsi7);
    if (rax9 != rax11 || ((rsi7 = v12, rdi10 = v13, eax14 = __strcmp(rdi10, rsi7), !!eax14) || (rsi7 = reinterpret_cast<int64_t*>(0x401400), rdi10 = reinterpret_cast<void*>(reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0x80), eax15 = __strcmp(rdi10, 0x401400), eax15 == 0))) {
        eax16 = 0;
    } else {
        eax16 = 1;
    }
```

What is interesting is that the string must be the same length as the hardcoded one, must be different from it, and must decode to the same value. It may seem like a contradicting set of requirements, but in fact [Base64 spec](https://en.wikipedia.org/wiki/Base64) makes such case possible. Because values are encoded using 6-bit octets it is sometimes possible that only a portion of the octet will be used in the encoding, and the rest can vary. This means that several different Base64 characters can produce the same decoded output.

In our case just varying the last character in the encoding achieves this (we replace ```g``` with ```h```) and we proceed to the next stage:

```sh
$ nc 110.10.212.138 19091
[*] Ok, Let's Start. Input the write string on each stage!:)
[*] -- STAGE 01 ----------
[+] KEY : H??x?H)?H.?H???L??H)???1???.D
[+] Input > 
TjBfbTRuX2M0bDFfYWc0aW5fWTNzdDNyZDR5Oih=
[*] USER : N0_m4n_c4l1_ag4in_Y3st3rd4y:(
[+] -- NEXT STAGE! ----------
```

### Stage 2

This stage is simpler, we are asked to input 2 Base64-encoded strings that are different in length, but whose decoded value is the same:

```c
    __puts("[*] -- STAGE 02 ----------", 0);
    __puts("[+] Input 1 ", 0);
    rax5 = stdin;
    __setbuf(rax5, 0);
    ____isoc99_scanf("%99s", reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0xf0);
    __puts("[+] Input 2 ", reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0xf0);
    rax6 = stdin;
    __setbuf(rax6, 0);
    ____isoc99_scanf("%99s", reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0x80);
    __base64decode(reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0xf0, reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0x100);
    rsi7 = reinterpret_cast<int64_t*>(reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0xf8);
    __base64decode(reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0x80, rsi7);
    rax8 = __strlen(reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0xf0, rsi7);
    rdi9 = reinterpret_cast<void*>(reinterpret_cast<int64_t>(__zero_stack_offset()) - 8 - 0x80);
    rax10 = __strlen(rdi9, rsi7);
    if (rax8 == rax10 || (rsi7 = v11, rdi9 = v12, eax13 = __strcmp(rdi9, rsi7), !!eax13)) {
        eax14 = 0;
    } else {
        eax14 = 1;
    }
```

This can be achieved by adding an extra equals sign to the end of one of the strings:

```sh
[*] -- STAGE 02 ----------
[+] Input 1 
TjBfbTRuX2M0bDFfYWc0aW5fWTNzdDNyZDR5Oih=
[+] Input 2 
TjBfbTRuX2M0bDFfYWc0aW5fWTNzdDNyZDR5Oih==
[+] -- NEXT STAGE! ----------
```

### Stage 3

Finally we are asked to enter a Base64-encoded shell command. However there is a filter to defeat: ```[/|$|-|_|&|>|`|'|"|%|;]|(cat)|(flag)|(bin)|(sh)|(bash)```. It can be easily bypassed by first entering ```ls -la``` to see if the flag is there (it is, in a file called ```flag```), and then by executing ```head *```.

```sh
[*] -- STAGE 03 ----------
[+] Ok, It's easy task to you, isn't it? :)
[+] So I will give a chance to execute one command! :)
[*] Input > 
bHMgLWxhCg==
#                                                               echo -n bHMgLWxhCg== | base64 -d | sh
total 12
drwxr-xr-x 2 root trick 4096 Feb  9 09:15 .
drw-r----- 8 root root  4096 Feb  9 09:15 ..
-rw-r----- 1 root trick   66 Feb  8 20:02 flag

...

[*] -- STAGE 03 ----------
[+] Ok, It's easy task to you, isn't it? :)
[+] So I will give a chance to execute one command! :)
[*] Input > 
aGVhZCAqCg==
#                                                               echo -n aGVhZCAqCg== | base64 -d | sh
FLAG{Nav3r_L3t_y0ur_L3ft_h4nd_kn0w_wh4t_y0ur_r1ghT_h4nd5_H4ck1ng}
```

The flag is ```FLAG{Nav3r_L3t_y0ur_L3ft_h4nd_kn0w_wh4t_y0ur_r1ghT_h4nd5_H4ck1ng}```.

