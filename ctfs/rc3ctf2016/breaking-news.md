---
layout: page
title: RC3 CTF 2016 Writeup: Breaking News
---

> 300 points
>
> We just received this transmission from our news correspondents. We need to find out what they are telling us.
>
> Download Link: https://drive.google.com/file/d/0B_AQp5s_S-khWjExSllLYjFRR0E/view?usp=sharing
> 
> author:orkulus

The tarball for this challenge contains 20 zip files named ```ChapterX.zip``` that in turn contain short text files. Examination of text files does not reveal anything of value, so let's look at the zip files in a binary editor. 

Files 0 through 3 do not show anything interesting, however ```Chapter4.zip``` has something unusual at the end of the file:

```
00000110  00 00 00 50 4B 05 06 00 00 00 00 01 00 01 00 52  ...PK..........R
00000120  00 00 00 C1 00 00 00 00 00 55 6B 4D 4B           ...Á.....UkMK
```

The character sequence ```UkMK``` is not part of the zip file. Other zip files contain some more characters:

```
UkMK
My0yMAo=
MTYtRFUK
S1lGCg==
QkxTCg==
```

These look like base64-encoded strings. After we decode them we get:

```
RC
3-20
16-DU
KYF
BLS
```

The key is ```RC3-2016-DUKYFBLS```.
