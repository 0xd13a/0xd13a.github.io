---
layout: page
title: "Google CTF Quals 2019 Writeup: Dial Tone"
---

> Dial Tone
>
> 189
>
> You might need a pitch-perfect voice to solve this one. Once you crack the code, the flag is CTF{code}.
> 
> [Download Attachment](https://storage.googleapis.com/gctf-2019-attachments/5200e49479e71df95cbb2a373904b7d8fe181eee8fc5b63435dee1d0629b2c48)

Challenge comes with an application that we must reverse. Running it gives an error:

```sh
$ ./a.out 
FAILED
```

Let's reverse it in Ghidra. The code in the main function is very simple:

* Open connection to PulseAudio stream 
* Read data from it and process the data with functions ```x``` and ```r```
* If the return value of ```r``` is greater than ```0``` - keep reading and processing the data, if it is ```0``` - finish successfully, and otherwise - fail

```c
...
  local_10 = pa_simple_new(0,*puParm2,2,0,"record",ss.3811,0,0,local_18);
  if (local_10 == 0) {
    uVar2 = pa_strerror((ulong)local_18[0]);
    fprintf(stderr,"pa_simple_new() failed: %s\n",uVar2);
    uVar2 = 1;
  }
  else {
    local_24 = 0;
    local_20 = 0;
    local_1c = 0;
    do {
      puVar3 = local_18;
      iVar1 = pa_simple_read(local_10,auStack163880,0x8000);
      if (iVar1 < 0) {
        uVar2 = pa_strerror((ulong)local_18[0]);
        fprintf(stderr,"pa_simple_read() failed: %s\n",uVar2);
        return 1;
      }
      x(auStack163880,auStack131112,auStack131112);
      r(&local_24,auStack131112,(int)auStack131112,(char *)puVar3,(int)pcVar4,(int)puVar5);
      if (extraout_EAX < 0) {
        fwrite("FAILED\n",1,7,stderr);
        return 1;
      }
    } while (extraout_EAX != 0);
    fwrite("SUCCESS\n",1,8,stderr);
    pa_simple_free(local_10);
...
```

Instead of doing full analysis of how the data is processed let's take a look at ```r```. Luckily there are clues there that will help us.

Closer inspection shows that the data is processed using ```f``` function with values like ```0x4b9``` and ```0x538``` passed to it.

As it turns out these values are frequency values for [tone dialing keys](https://en.wikipedia.org/wiki/Dual-tone_multi-frequency_signaling#Keypad) - ```0x4b9``` is ```1209 Hz```, ```0x538``` is ```1336 Hz```, and so on:

```c
...
    local_58[0] = (double)f(param_2,0x4b9);
    local_58[1] = f(param_2,0x538);
    local_58[2] = f(param_2,0x5c5);
    local_58[3] = f(param_2,0x661);
    local_c = 0xffffffff;
    local_18 = 1.00000000;
    local_1c = 0;
    while ((int)local_1c < 4) {
      if (local_18 < local_58[(long)(int)local_1c]) {
        local_c = local_1c;
        local_18 = local_58[(long)(int)local_1c];
      }
      local_1c = local_1c + 1;
    }
    local_78[0] = (double)f(param_2,0x2b9);
    local_78[1] = f(param_2,0x302);
    local_78[2] = f(param_2,0x354);
    f(param_2,0x3ad);
    local_20 = -1;
    local_28 = 1.00000000;
    local_2c = 0;
    while (local_2c < 4) {
      if (local_28 < local_78[(long)local_2c]) {
        local_20 = local_2c;
        local_28 = local_78[(long)local_2c];
      }
      local_2c = local_2c + 1;
    }
    if (*(char *)((long)param_1 + 8) == '\0') {
      if ((-1 < (int)local_c) && (-1 < local_20)) {
        local_c = local_20 << 2 | local_c;
        bVar1 = false;
        switch(*(undefined4 *)((long)param_1 + 4)) {
        case 0:
          bVar1 = local_c == 9;
          break;
        case 1:
          bVar1 = local_c == 5;
          break;
        case 2:
          bVar1 = local_c == 10;
          break;
        case 3:
          bVar1 = local_c == 6;
          break;
        case 4:
          bVar1 = local_c == 9;
          break;
        case 5:
          bVar1 = local_c == 8;
          break;
        case 6:
          bVar1 = local_c == 1;
          break;
        case 7:
          bVar1 = local_c == 0xd;
          break;
        case 8:
          if (local_c == 0) {
            return;
          }
        }
...
```

The frequency value positions are then encoded in 4 bits of a state byte - low tone is the upper 2 bits, and high tone in the lower 2 bits. The state byte is checked in a switch statement, which is easy to decode:

* ```9``` (```0b1001```) - ```852 Hz``` (position ```2``` (```0b10```)) and ```1336 Hz``` (position ```1``` (```0b01```)) - ```Key 8```
* ```5``` (```0b0101```) - ```770 Hz``` (position ```1``` (```0b01```)) and ```1336 Hz``` (position ```1``` (```0b01```)) - ```Key 5```
* ...and so forth

The full key sequence decodes to ```859687201``` and the flag is ```CTF{859687201}```.
