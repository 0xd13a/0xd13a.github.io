---
layout: page
title: "PlaidCTF 2017 Writeup: zipper"
---

> zipper
> 
> Misc (50 pts)
>
> Something doesn't seem quite right with this zip file. 
> 
> Can you fix it and get the flag?
>
> [zipper.zip]({{ site.baseurl }}/ctfs/pctf2017/zipper/zipper.zip)

We are given a damaged ZIP file that 7-Zip and other archive tools cannot open. The ```zipinfo``` tool reveals at least part of the problem:

```sh
$ zipinfo -lv zipper.zip 
Archive:  zipper.zip
There is no zipfile comment.

End-of-central-directory record:
-------------------------------

  Zip archive file size:                       236 (00000000000000ECh)
  Actual end-cent-dir record offset:           214 (00000000000000D6h)
  Expected end-cent-dir record offset:         214 (00000000000000D6h)
  (based on the length of the central directory and its expected offset)

  This zipfile constitutes the sole disk of a single-part archive; its
  central directory contains 1 entry.
  The central directory is 78 (000000000000004Eh) bytes long,
  and its (expected) offset in bytes from the beginning of the zipfile
  is 136 (0000000000000088h).

warning:  filename too long--truncating.

Central directory entry #1:
---------------------------

  

  offset of local header from start of archive:   0
                                                  (0000000000000000h) bytes
  file system or operating system of origin:      Unix
  version of encoding software:                   3.0
  minimum file system compatibility required:     MS-DOS, OS/2 or NT FAT
  minimum software version required to extract:   2.0
  compression method:                             deflated
  compression sub-type (deflation):               maximum
  file security status:                           not encrypted
  extended local header:                          no
  file last modified on (DOS date/time):          2017 Apr 18 19:15:56
  32-bit CRC value (hex):                         532ea93e
  compressed size:                                70 bytes
  uncompressed size:                              246 bytes
  length of filename:                             9001 characters
  length of extra field:                          24 bytes
  length of file comment:                         0 characters
  disk number on which file begins:               disk 1
  apparent file type:                             text
  Unix file attributes (100664 octal):            -rw-rw-r--
  MS-DOS file attributes (00 hex):                none
```

The file name in the ZIP file is 9001 characters long, which is a bit much for a 236-byte archive file :smile:.

In [ZIP file format](https://en.wikipedia.org/wiki/Zip_%28file_format%29) file names are located in both ```Central directory file header``` (```50 4b 01 02```) and ```Local file header``` (```50 4b 03 04```). 

Let's open the file in a binary editor (e.g. ```HxD```) and search for size 9001 (```29 23```). It is found at offsets ```0x1a``` and ```0xa4```. We can correct it there, but what do we set it to?

By studying file name location offsets in the specification we determine that in both locations the current file names are sequences of 8 NULL bytes each, located at offsets ```0x1e``` and ```0xb6```, respectively. When we set each length to 8 and each name to ```AAAAAAAA``` we are able to open the zip file and see the flag ```PCTF{f0rens1cs_yay}``` inside.