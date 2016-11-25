---
layout: page
title: "RC3 CTF 2016 Writeup: Logmein"
---

> 100 points
>
> This has to be one of the safest and most secure login forms out there. Can you break it o 1337 h4x0r?
>
> Download Link: https://drive.google.com/file/d/0Bw7N3lAmY5PCUVR4WGloSGlkUG8/view?usp=sharing

The file that comes with the challenge is an ELF executable. When run it asks user for the password (flag), and then reports whether the password was entered correctly. 

Let's load the file in [Snowman](http://derevenets.com/) to get a sense of the decoding algorithm.

The following function looks interesting:

```c++
int64_t fun_400630() {
    void* rbp1;
    uint64_t rax2;
    uint64_t rax3;
    int32_t v4;
    uint64_t rax5;
    uint64_t rax6;
    signed char v7;
    signed char v8;
    uint32_t edx9;

    rbp1 = reinterpret_cast<void*>(reinterpret_cast<int64_t>(__zero_stack_offset()) - 8);
    fun_4004e0("Welcome to the RC3 secure password guesser.\n");
    fun_4004e0("To continue, you must enter the correct password.\n");
    fun_4004e0("Enter your guess: ", "Enter your guess: ");
    fun_400500("%32s", reinterpret_cast<int64_t>(rbp1) - 80);
    rax2 = fun_4004d0(reinterpret_cast<int64_t>(rbp1) - 80);
    rax3 = fun_4004d0(reinterpret_cast<int64_t>(rbp1) - 32);
    if (rax2 < rax3) {
        fun_4007c0();
    }
    v4 = 0;
    while (rax5 = fun_4004d0(reinterpret_cast<int64_t>(rbp1) - 80), reinterpret_cast<uint64_t>(static_cast<int64_t>(v4)) < rax5) {
        rax6 = fun_4004d0(reinterpret_cast<int64_t>(rbp1) - 32);
        if (reinterpret_cast<uint64_t>(static_cast<int64_t>(v4)) >= rax6) {
            fun_4007c0();
        }
        v7 = *reinterpret_cast<signed char*>(reinterpret_cast<int64_t>(rbp1) + v4 - 32);
        __asm__("cdq ");
        v8 = *reinterpret_cast<signed char*>(reinterpret_cast<int64_t>(rbp1) + v4 % 7 - 40);
        edx9 = reinterpret_cast<uint32_t>(static_cast<int32_t>(v7)) ^ reinterpret_cast<uint32_t>(static_cast<int32_t>(v8));
        if (static_cast<int32_t>(*reinterpret_cast<signed char*>(reinterpret_cast<int64_t>(rbp1) + v4 - 80)) != static_cast<int32_t>(*reinterpret_cast<signed char*>(&edx9))) {
            fun_4007c0();
        }
        ++v4;
    }
    fun_4007f0();
    return 0;
}
```

After analysis the algorithm boils down to the following:

```c++
curr_char_pos = 0;
while (input_str_len = strlen(input_str), curr_char_pos < input_str_len) {
        if (curr_char_pos >= strlen(flag_str)) {
            abort();
        }
        curr_flag_char = flag_str[curr_char_pos];
        xor_str_char = xor_str[curr_char_pos % 7];
        decoded_char = curr_flag_char ^ xor_str_char;
        if (input_str[curr_char_pos] != decoded_char) {
            abort();
        }
        ++curr_char_pos;
    }
```

We now open the application in the debugger to figure out the values of ```flag_str``` and ```xor_str```:

![Flag]({{ site.baseurl }}/ctfs/rc3ctf2016/logmein/flag.png)

![XOR str]({{ site.baseurl }}/ctfs/rc3ctf2016/logmein/xor_str.png)

Finally we put all this knowledge together in a script which gives us flag ```RC3-2016-XORISGUD```:

```
flag_str = ':"AL_RT^L*.?+6/46'
xor_str = 'harambe'
char_cnt = 0
s = ''
for x in flag_str:
	s += chr(ord(x)^ord(xor_str[char_cnt % len(xor_str)]))
	char_cnt += 1
print s
```
