---
layout: page
title: "Insomni'hack Teaser 2020 Writeup: getdents"
---

> getdents
>
> 115
>
> Oh shit! Data have been stolen from my computer... I looked for malicious activity but found nothing suspicious. Could ya give me a hand and find the malware and how it's hiding?
> 
> [Memory image](https://storage.googleapis.com/insomnihack/media/memory_a97a5b9a792b61131eb6193e09c69616df875bb43539359af0e421b1b0798ba7.zip)

I love CTF challenges that push me out of my comfort zone and force me to learn new skills and tools. 

Here we get a memory image that we want to analyze for malware and find the flag. [Volatility](https://www.volatilityfoundation.org/) is the "go-to" tool for such analysis, and there is a lot of information on the Web on how to use it. For example [this tutorial](https://apps.dtic.mil/dtic/tr/fulltext/u2/1004190.pdf) is very detailed and helpful.

Volatility requires an OS "profile" to be able to analyze a memory dump, and the organizers, helpfully, included one (```Ubuntu_4.15.0-72-generic_profile.zip```) in the memory dump archive. Let's verify that it works:

```sh
$ volatility --plugins=. --profile=LinuxUbuntu_4_15_0-72-generic_profilex64 -f memory.vmem linux_banner

Volatility Foundation Volatility Framework 2.6
Linux version 4.15.0-72-generic (buildd@lcy01-amd64-026) (gcc version 7.4.0 (Ubuntu 7.4.0-1ubuntu1~18.04.1)) #81-Ubuntu SMP Tue Nov 26 12:20:02 UTC 2019 (Ubuntu 4.15.0-72.81-generic 4.15.18)
```

We were told that there is a "malware" on the system, let's start with listing the running processes:

```sh
$ volatility --plugins=. --profile=LinuxUbuntu_4_15_0-72-generic_profilex64 -f memory.vmem linux_pslist

Volatility Foundation Volatility Framework 2.6
Offset             Name                 Pid             PPid            Uid             Gid    DTB                Start Time
------------------ -------------------- --------------- --------------- --------------- ------ ------------------ ----------
...
0xffff8a9dacd12e80 gnome-terminal-      1724            1237            1000            1000   0x0000000014c3a000 2020-01-16 14:00:57 UTC+0000
0xffff8a9db6dc0000 bash                 1733            1724            1000            1000   0x0000000014662000 2020-01-16 14:00:57 UTC+0000
0xffff8a9dcf5ec5c0 sudo                 1750            1733            0               0      0x000000005388a000 2020-01-16 14:01:22 UTC+0000
0xffff8a9dcf5edd00 meterpreter          1751            1750            0               0      0x0000000014540000 2020-01-16 14:01:22 UTC+0000
...
0xffff8a9dc3c40000 sh                   2964            1751            0               0      0x0000000076aec000 2020-01-16 14:02:57 UTC+0000
```

So there is a Meterpreter running, spawning a shell - that's not suspicious at all! :smile: Let's dump the memory image for the shell:

```sh
$ volatility --plugins=. --profile=LinuxUbuntu_4_15_0-72-generic_profilex64 -f memory.vmem linux_procdump -p 2964 -D ./m

Volatility Foundation Volatility Framework 2.6
Offset             Name                 Pid             Address            Output File
------------------ -------------------- --------------- ------------------ -----------
0xffff8a9dc3c40000 sh                   2964            0x000055f26b1a8000 ./m/sh.2964.0x55f26b1a8000
```

Quick examination of the shell image brings up execution of the ```insmod``` command:

```sh
$ strings m/sh.2964.0x55f26b1a8000 | grep insmod

insmod /home/julien/Downloads/rkit.ko hide=rJ/1g5PA5amy176A64akjuq/jryOug== hide_pid=1751
insmod
insmod
/sbin/insmod
```

```insmod``` inserts a kernel module into the kernel at runtime, and the name like ```rkit.ko``` does not inspire much confidence. Looks like a rootkit kernel module is being installed. Note the ```hide``` parameter passed to it on command line - we will need it later...

Not surprisingly this module does not show up on the list of modules:

```sh
$ volatility --plugins=. --profile=LinuxUbuntu_4_15_0-72-generic_profilex64 -f memory.vmem linux_lsmod | grep rkit

Volatility Foundation Volatility Framework 2.6
```

Yet it does seem to load:

```sh
$ volatility --plugins=. --profile=LinuxUbuntu_4_15_0-72-generic_profilex64 -f memory.vmem linux_dmesg

Volatility Foundation Volatility Framework 2.6
[0.0] Linux version 4.15.0-72-generic (buildd@lcy01-amd64-026) (gcc version 7.4.0 (Ubuntu 7.4.0-1ubuntu1~18.04.1)) #81-Ubuntu SMP Tue Nov 26 12:20:02 UTC 2019 (Ubuntu 4.15.0-72.81-generic 4.15.18)
[0.0] Command line: BOOT_IMAGE=/boot/vmlinuz-4.15.0-72-generic root=UUID=aee60f3a-b82f-4d1a-92ca-98ab58c5f506 ro find_preseed=/preseed.cfg auto noprompt priority=critical locale=en_US quiet
[0.0] KERNEL supported cpus:
[0.0]   Intel GenuineIntel
[0.0]   AMD AuthenticAMD
[0.0]   Centaur CentaurHauls
[0.0] Disabled fast string operations
...
[168682653963.168] rkit: loading out-of-tree module taints kernel.
[168682697557.168] rkit: module verification failed: signature and/or required key missing - tainting kernel
```

If we use special commands to list the hidden modules and to check the module, it does show up:

```sh
$ volatility --plugins=. --profile=LinuxUbuntu_4_15_0-72-generic_profilex64 -f memory.vmem linux_hidden_modules

Volatility Foundation Volatility Framework 2.6
Offset (V)         Name
------------------ ----
0xffffffffc011b3e0 resetafter
0xffffffffc05a0300 qni
0xffffffffc0943080 rkit


$ volatility --plugins=. --profile=LinuxUbuntu_4_15_0-72-generic_profilex64 -f memory.vmem linux_check_modules

Volatility Foundation Volatility Framework 2.6
    Module Address       Core Address       Init Address Module Name             
------------------ ------------------ ------------------ ------------------------
0xffffffffc0943080 0xffffffffc0941000                0x0 rkit  
```

Let's dump the module contents and look inside:

```sh
$ volatility --plugins=. --profile=LinuxUbuntu_4_15_0-72-generic_profilex64 -f memory.vmem linux_moddump -D ./m -b 0xffffffffc0943080

Volatility Foundation Volatility Framework 2.6
Wrote 4146551 bytes to rkit.0xffffffffc0943080.lkm
```

Analysis in Ghidra is complicated by the fact that addresses of some functions cannot be resolved without further digging, but we can make some educated observations:

* There is code in method ```FUN_ffffffffc0a41300()``` that seems to use a key of length ```0x3e``` to XOR a piece of data:

```c
...
      do {
        s__ffffffffc09433d8[lVar3 + 8] =
             *(byte *)(lVar2 + (int)(uVar4 % 6)) ^ s_H_}_ffffffffc0943000[lVar3];
        lVar3 = lVar3 + 1;
        uVar4 = uVar4 + 0x2a;
      } while (lVar3 != 0x3e);
...
```

* There is a piece of data that is exactly ```0x3e``` bytes in length in the dump that may be the key:

```sh
$ xxd -g 1 -s $((0x2420)) -l $((0x3e)) m/rkit.0xffffffffc0943080.lkm 
00002420: e5 d1 a6 f8 c1 f0 d5 dd f9 e6 ca c6 db f4 f6 e1  ................
00002430: da d4 e7 d9 fd c7 a5 fc c8 c4 e4 de e3 a2 f7 c5  ................
00002440: fe a3 ff d0 c3 e0 ab c2 a7 d8 d7 e2 df eb dc aa  ................
00002450: a1 a0 d3 cb a4 f1 fa c0 fb f5 d6 f3 ea e8        ..............
```

* Function ```FUN_ffffffffc0a41575()``` seems to unpack a base64-encoded string by calling ```FUN_ffffffffc0a413b0()``` and then XOR it with the key by calling ```FUN_ffffffffc0a41300()```:

```c
...
  DAT_ffffffffc0a43428 = FUN_ffffffffc0a413b0(uVar1,uVar2,0xffffffffc0943420);
  FUN_ffffffffc0a41300();
...
```

If we think back to the parameters to ```insmod``` there was a base64-encoded string specified there. Let's try and decode it:

```sh
$ python
Python 2.7.15+ (default, Nov 28 2018, 16:27:22) 
[GCC 8.2.0] on linux2
Type "help", "copyright", "credits" or "license" for more information.
>>> import base64
>>> import binascii
>>> data = base64.b64decode("rJ/1g5PA5amy176A64akjuq/jryOug==")
>>> key = binascii.unhexlify("e5d1a6f8c1f0d5ddf9e6cac6dbf4f6e1dad4e7d9fdc7a5fcc8c4e4dee3a2f7c5fea3ffd0c3e0abc2a7d8d7e2dfebdcaaa1a0d3cba4f1fac0fbf5d6f3eae8")
>>> flag = ""
>>> for x in range(len(data)):
...     flag += chr(ord(data[x]) ^ ord(key[x]))
... 
>>> flag
'INS{R00tK1tF0rRo0kies}' 
```

Bingo! The flag is ```INS{R00tK1tF0rRo0kies}```.



