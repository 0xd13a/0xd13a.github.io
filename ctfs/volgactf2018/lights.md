---
layout: page
title: "VolgaCTF 2018 Quals Writeup: Lights"
---

> What is this app? What does it do? Where shall we run it? What shall we do with it?
> 
> [lights]({{ site.baseurl }}/ctfs/volgactf2018/lights/lights.zip)

The executable is an ARM binary:

```
$ file lights
lights: ELF 32-bit LSB executable, ARM, EABI5 version 1 (SYSV), dynamically linked, interpreter /lib/ld-linux-armhf.so.3, 
for GNU/Linux 2.6.32, BuildID[sha1]=dfc36f976d652267c6a4feac861e7daa2681f5ae, stripped
```

Let's open it in ```IDA Pro```. ```Hex Rays``` and ```Snowman``` can decompile the executable into fairly easy to read form. 

The following function looks intriguing:

```c
signed int sub_1063C()
{
  signed int result; // r0@2

  if ( sub_14A3C() )
  {
    sub_12E08(3u, 1);
    sub_1184C();
    sub_12828();
    sub_115CC();
    sub_1265C();
    sub_12518();
    sub_12578();
    sub_10920();
    sub_13250(0x2EEu);
    sub_113C0();
    sub_13250(0x2EEu);
    sub_12A6C();
    sub_126E4();
    sub_10920();
    sub_13250(0x2EEu);
...
```

It contains a long list of functions called one after another. The functions that are called have some interesting characteristics:

* Function ```sub_13250``` is essentially a ```sleep()```:

```c
int __fastcall sub_13250(unsigned int a1)
{
  unsigned int v2; // [sp+0h] [bp-10h]@1
  unsigned int v3; // [sp+4h] [bp-Ch]@1

  v2 = a1 / 0x3E8;
  v3 = 1000000 * (a1 % 0x3E8);
  return nanosleep((const struct timespec *)&v2, 0);
}
```

* Other functions call some service function (```sub_132A0```) and sleep (```sub_13250```) intermittently. Let's consider ```sub_12518```, for example:

```c
int sub_12518()
{
  sub_132A0(3u, 1);
  sub_13250(0xFAu);
  sub_132A0(3u, 0);
  sub_13250(0xFAu);
  sub_132A0(3u, 1);
  sub_13250(0x2EEu);
  sub_132A0(3u, 0);
  return sub_13250(0x2EEu);
}
```

The service function is called with 1 and 0 as parameters, and sleep is called with delay ```0xFA (250)``` and ```0x2EE (750)```. Considering the title of the challenge it seems like the code is controlling a light of some kind, turning it on an off and sleeping for different periods of time. 

If this guess is correct then this function does the following:

* turn light on
* wait for 250 ms
* turn light off 
* wait for 250 ms
* turn light on
* wait for 750 ms
* turn light off 
* wait for 750 ms

...which looks a lot like [Morse code](https://en.wikipedia.org/wiki/Morse_code) letter ```A```. :smile:

Going through other similar functions confirms this and gives us the following message relayed in Morse code:

```VOLGACTF WITH A BLINK OF AN EYE YOU FINALLY SEE THE LIGHT```

[Great song...](https://youtu.be/zSmOvYzSeaQ?t=1m11s) The flag is ```VolgaCTF{WITH_A_BLINK_OF_AN_EYE_YOU_FINALLY_SEE_THE_LIGHT}```.

