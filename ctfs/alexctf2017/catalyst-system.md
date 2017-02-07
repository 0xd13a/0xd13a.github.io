---
layout: page
title: "Alex CTF 2017 Writeup: Catalyst system"
---

> RE3: Catalyst system
>
> 150
>
>CEO of catalyst systems decided to build his own log in system from scratch, he thought that it is so safe that no one can fool around with him!
>
>catalyst

When run, the application seems to hang:

```sh
$ ./catalyst 
 ___________  _            ___________  _       _  ___________  ___________  ___________ 
||||||||||||||||          ||||||||||||||||     ||||||||||||||||||||||||||||||||||||||||||
|||-------||||||          |||---------  |||   ||| |||---------  ----|||---- |||--------- 
|||       ||||||          |||            ||| |||  |||               |||     |||          
|||_______||||||          |||_________    |||||   |||               |||     |||_________ 
||||||||||||||||          |||||||||||||    |||    |||               |||     |||||||||||||
|||-------||||||          |||---------    |||||   |||               |||     |||--------- 
|||       ||||||          |||            ||| |||  |||               |||     |||          
|||       ||||||_________ |||_________  |||   ||| |||_________      |||     |||          
|||       ||||||||||||||||||||||||||||||||     ||||||||||||||||     |||     |||          
 -         -  -----------  -----------  -       -  -----------       -       -           
Welcome to Catalyst systems
Loading..
```

Reversal to C in [Snowman](http://derevenets.com/) helps explain why - there is a random sleep on startup in ```fun_400760```:

```c
    ...
  
    fun_4006f0("Loading");
    rax7 = stdout;
    rdi8 = rax7;
    fun_400730(rdi8);
    v9 = 0;
    while (v9 <= 29) {
        eax10 = fun_400770(rdi8);
        *reinterpret_cast<int32_t*>(&rax11) = v9 + v9 + v9;
        *reinterpret_cast<int32_t*>(reinterpret_cast<int64_t>(&rax11) + 4) = 0;
        __asm__("cdq ");
        *reinterpret_cast<int32_t*>(&rdi12) = eax10 % static_cast<int32_t>(rax11 + 1);
        *reinterpret_cast<int32_t*>(reinterpret_cast<int64_t>(&rdi12) + 4) = 0;
        fun_400760(rdi12); /* sleep() */
        fun_4006c0(46);
        rax13 = stdout;
        rdi8 = rax13;
        fun_400730(rdi8);
        ++v9;
    }
    fun_4006c0(10);
    fun_4006f0("Username: ");
    fun_400740("%s", v2);
    fun_4006f0("Password: ", "Password: ");
    rsi14 = v4;
    fun_400740("%s", rsi14);
    fun_4006f0("Logging in", "Logging in");
    rax15 = stdout;
    rdi16 = rax15;
    fun_400730(rdi16, rdi16);
    v17 = 0;
    while (v17 <= 29) {
        eax18 = fun_400770(rdi16, rdi16);
        *reinterpret_cast<int32_t*>(&rax19) = v17;
        *reinterpret_cast<int32_t*>(reinterpret_cast<int64_t>(&rax19) + 4) = 0;
        __asm__("cdq ");
        *reinterpret_cast<int32_t*>(&rdi20) = eax18 % static_cast<int32_t>(rax19 + 1);
        *reinterpret_cast<int32_t*>(reinterpret_cast<int64_t>(&rdi20) + 4) = 0;
        fun_400760(rdi20, rdi20); /* sleep() */
        fun_4006c0(46, 46);
        rax21 = stdout;
        rdi16 = rax21;
        fun_400730(rdi16, rdi16);
        ++v17;
    }
    fun_4006c0(10, 10);
    fun_400c9a(v2, rsi14);
    fun_400cdd(v2, rsi14);
    fun_4008f7(v2, rsi14);
    fun_400977(v2, v4);
    fun_400876(v2, v4);
    return 0;
```

So the logic here is to get username and password from the user and perform a number of manipulations on them in the functions at the bottom of the main routine (```fun_400c9a``` through ```fun_400876```).

Analyzing the code further we see that the flag is output in routine ```fun_400876``` XOR'ed with an embedded key (we will collect it and save it for later use), but that still does not explain what the flag should be. 

Routine ```fun_400977``` has a bunch of checks that will help us derive the flag from the password (```v5```) using ```rand()``` function:

```c
    ...
  
    *reinterpret_cast<int32_t*>(&rdi14) = v4->f8 + (v4->f0 + v4->f4);
    *reinterpret_cast<int32_t*>(reinterpret_cast<int64_t>(&rdi14) + 4) = 0;
    fun_400700(rdi14); /* srand() */
    ebx15 = v5->f0;
    eax16 = fun_400770(rdi14); /* rand() */
    if (ebx15 - eax16 != 0x55eb052a) {
        fun_4006d0("invalid username or password");
        *reinterpret_cast<int32_t*>(&rdi14) = 0;
        *reinterpret_cast<int32_t*>(reinterpret_cast<int64_t>(&rdi14) + 4) = 0;
        fun_400750(0);
    }
    ebx17 = v5->f4;
    eax18 = fun_400770(rdi14); /* rand() */
    if (ebx17 - eax18 != 0xef76c39) {
        fun_4006d0("invalid username or password");
        *reinterpret_cast<int32_t*>(&rdi14) = 0;
        *reinterpret_cast<int32_t*>(reinterpret_cast<int64_t>(&rdi14) + 4) = 0;
        fun_400750(0);
    }
    ebx19 = v5->f8;
  
  ...
```

The only trouble is that the PRNG is initialized (```srand()```) with the contents of the first 12 characters in the username (```v4```). Let's dig further.

Routine ```fun_400cdd``` helps complete the puzzle. It shows the dependence between the first 12 characters of the username: 

```c
void fun_400cdd(struct s0* rdi, struct s0* rsi) {
    int64_t rax3;
    int64_t rax4;
    int64_t rax5;
    int64_t rdx6;

    *reinterpret_cast<int32_t*>(&rax3) = rdi->f0;  /* username characters 0-3 */
    *reinterpret_cast<int32_t*>(reinterpret_cast<int64_t>(&rax3) + 4) = 0;
    *reinterpret_cast<int32_t*>(&rax4) = rdi->f4;  /* username characters 4-7 */
    *reinterpret_cast<int32_t*>(reinterpret_cast<int64_t>(&rax4) + 4) = 0;
    *reinterpret_cast<int32_t*>(&rax5) = rdi->f8;  /* username characters 8-11 */
    *reinterpret_cast<int32_t*>(reinterpret_cast<int64_t>(&rax5) + 4) = 0;
    if (rax5 + (rax3 - rax4) != 0x5c664b56 || ((rdx6 = rax3 + rax5, rdx6 + (rdx6 + rdx6) + rax4 != 0x2e700c7b2) || rax4 * rax5 != 0x32ac30689a6ad314)) {
        fun_4006d0("invalid username or password");
        fun_400750(0);
    }
    return;
}
```

So the rules are:

```
  rax5 - rax3 + rax4 = 0x5c664b56
  rax4 + 3 * (rax5 + rax3) = 0x2e700c7b2
  rax4 * rax5 = 0x32ac30689a6ad314
```

```rax4``` can be solved as follows, and then substituted to derive ```rax3``` and ```rax5```:

```
  rax5 = 0x32ac30689a6ad314 / rax4
  rax3 = 0x5c664b56 - 0x32ac30689a6ad314 / rax4 + rax4
  rax4 + 3 * (0x32ac30689a6ad314 / rax4 + 0x5c664b56 - 0x32ac30689a6ad314 / rax4 + rax4) = 0x2e700c7b2
  rax4 + 3 * (0x5c664b56 + rax4) = 0x2e700c7b2
  rax4 = (0x2e700c7b2 - 3 * 0x5c664b56) / 4
```

Putting all this knowledge together in a C program we have the following:

```c
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main()
{
    unsigned int rax3, rax4, rax5;

    char u[12];
    char p[40];
    char key[] = {0x42, 0x13, 0x27, 0x62, 0x41, 0x35, 0x6b, 0x0f, 0x7b, 0x46, 0x3c, 0x3e, 0x67, 0x0c, 0x08, 0x59, 0x44, 0x72, 0x36, 0x05, 0x0f, 0x15, 0x54, 0x43, 0x38, 0x17, 0x1d, 0x18, 0x08, 0x0e, 0x5c, 0x31, 0x21, 0x16, 0x02, 0x09, 0x18, 0x14, 0x54, 0x59};

    rax4 = (0x2e700c7b2L - 0x5c664b56L * 3) / 4;
    rax5 = 0x32ac30689a6ad314L / rax4;
    rax3 = 0x5c664b56L - rax5 + rax4;

    ((unsigned int*)u)[0] = rax3;
    ((unsigned int*)u)[1] = rax4;
    ((unsigned int*)u)[2] = rax5;

    printf("username: %.*s\n", sizeof(u), u);

    srand(rax3 + rax4 + rax5);

    ((unsigned int*)p)[0] = 0x55eb052a + rand();
    ((unsigned int*)p)[1] = 0x0ef76c39 + rand();
    ((unsigned int*)p)[2] = 0xcc1e2d64 + rand();
    ((unsigned int*)p)[3] = 0xc7b6c6f5 + rand();
    ((unsigned int*)p)[4] = 0x26941bfa + rand();
    ((unsigned int*)p)[5] = 0x260cf0f3 + rand();
    ((unsigned int*)p)[6] = 0x10d4caef + rand();
    ((unsigned int*)p)[7] = 0xc666e824 + rand();
    ((unsigned int*)p)[8] = 0xfc89459c + rand();
    ((unsigned int*)p)[9] = 0x2413073a + rand();
    printf("password: %.*s\n", sizeof(p), p);

    printf("flag: ALEXCTF{");
    for (int i = 0; i < sizeof(p); i++) {
        printf("%c", p[i] ^ key[i]);
    }
    printf("}\n");
}
```

When compiled and run the program gives us the username, password and the flag ```ALEXCTF{1_t41d_y0u_y0u_ar3__gr34t__reverser__s33}```:

```sh
$ gcc -o solve solve.c && ./solve
username: catalyst_ceo
password: sLSVpQ4vK3cGWyW86AiZhggwLHBjmx9CRspVGggj
flag: ALEXCTF{1_t41d_y0u_y0u_ar3__gr34t__reverser__s33}
```