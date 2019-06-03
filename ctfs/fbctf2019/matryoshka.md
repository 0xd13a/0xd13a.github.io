---
layout: page
title: "Facebook CTF 2019 Writeup: matryoshka"
---

> matryoshka
>
> 738
>
> There was a downloader found on a Mac desktop. It's your job to have layers of fun getting the flag.
> 
> Written by malwareunicorn
>
> Download: [matryoshka.tar.gz]({{ site.baseurl }}/ctfs/fbctf2019/matryoshka/matryoshka.tar.gz)

[Matryoshka](https://en.wikipedia.org/wiki/Matryoshka_doll) is a fitting name for this challenge as there is a number of nested levels one has to unroll to solve it. It was a lot of fun (and hair pulling) to solve and a great opportunity to try [Ghidra](https://ghidra-sre.org/).

We start with a Mach-O 64-bit executable. It reverses in Ghidra easily (in absence of a Mac or an emulator we will be doing static analysis). Here are the most important pieces:

```c
  ...
  
  _send_request(&local_1c);
  _receive_response(&local_1c,&local_28);

  ...
  
  png_header_offset = _check_png_header(local_28);

  ...
  
  pcVar2 = local_28 + png_header_offset
  lVar3 = (*pcVar2)(0);
  
  ...
  
  pcVar2 = local_28 + png_header_offset + lVar3
  _data = (*pcVar2)(0);

  ...
  
  _data = __OSSwapInt64(_data);

  ...
  
  intflag_idx1 = 7;
  while (-1 < intflag_idx1) {
    local_60 = local_60 << 8 | (ulong)intermid_flag[(long)intflag_idx1];
    intflag_idx1 = intflag_idx1 + -1;
  }
  intflag_idx2 = 0xf;
  while (7 < intflag_idx2) {
    local_68 = local_68 << 8 | (ulong)intermid_flag[(long)intflag_idx2];
    intflag_idx2 = intflag_idx2 + -1;
  }
  intflag_idx3 = 0x17;
  while (0xf < intflag_idx3) {
    local_70 = local_70 << 8 | (ulong)intermid_flag[(long)intflag_idx3];
    intflag_idx3 = intflag_idx3 + -1;
  }
  intflag_idx4 = 0x1b;
  while (0x17 < intflag_idx4) {
    local_74 = local_74 << 8 | (uint)intermid_flag[(long)intflag_idx4];
    intflag_idx4 = intflag_idx4 + -1;
  }
  if (((((local_60 ^ local_68) == 0x3255557376f68) && ((local_68 ^ local_70) == 0x393b415f5a590044))
      && ((local_70 ^ (ulong)local_74) == 0x665f336b1a566b19)) &&
     (((ulong)local_74 ^ 0x115c28da834feffd) == _data)) {
  
  ...

```

Application goes through the following steps:

* Send a GET request to address [http://157.230.132.171/pickachu_wut.png]({{ site.baseurl }}/ctfs/fbctf2019/matryoshka/pickachu_wut.png). The image contains embedded code that we will analyze next. 

* Find PNG header in the response and count offset ```0x60000``` from it

* Call code at that location and receive second offset as a response

* Call code at the second location and receive a 64bit value response

* XOR that value with 4 other 64bit constants to verify flag contents


Let's download the image, cut it at the ```0x60000``` mark and load it in Ghidra again. The code asks for us to enter a key, and then does a ROT13 on it and compares it to the encoded string at offset ```0x172```: ```4ZberYriryf2TbXrrcTbvat``` (this string decodes to ```4MoreLevels2GoKeepGoing```). If the user input matches, then the first 2 characters from this string (```4M```) are XORed with ```0x354c``` and returned. The result is ```0x7878``` and this becomes our offset for the code in the next stage.

We repeat the cutting of the file (this time at offset ```0x67878```) and analyze it in Ghidra. Here complexity increases, the code asks for the next key (4 bytes in length) and then uses its value to decode (via repeating ```SUB mod 0x100```) ```0x678``` bytes at offset ```0x137```. However the correctness check before a call to decoded block makes sure that the 4 bytes at that location match a constant value (```0xe5894855```):

```
        000000e4 41 8b 55 00     MOV        EDX,dword ptr [R13]=>SUB_00000137                = C7h
        000000e8 81 fa 55        CMP        EDX,0xe5894855
                 48 89 e5
        000000ee 75 1f           JNZ        LAB_0000010f
        000000f0 b8 04 00        MOV        EAX,0x2000004
                 00 02
        000000f5 bf 01 00        MOV        EDI,0x1
                 00 00
        000000fa 48 8d 35        LEA        RSI=>DAT_00000e37,[0xe37]                        = 4Bh    K
                 36 0d 00 00
        00000101 ba 0c 00        MOV        EDX,0xc
                 00 00
        00000106 0f 05           SYSCALL
        00000108 31 c0           XOR        EAX,EAX
        0000010a 41 ff d5        CALL       R13=>SUB_00000137
```

This tells us what the key is:  ```0xe5894855``` subtracted from ```0x59b978c7``` (bytes at the beginning of the encoded section) gives us ```r00t``` (reversed to account for little-endianness).

After decoding with the key let's repeat file cutting again and reverse code at offset ```0x137```. This gives us yet another iteration that asks for an 8-byte key and this time around XORs it with ```0x44a``` values at offset ```0x300```:

```c
  ...
  
  if ((int)((ulong)DAT_00000576 >> 0x20) == 0x4a514f75) {
    lVar2 = 0;
    while (lVar2 < 0x44a) {
      *(byte *)(lVar2 + 300) = *(byte *)(lVar2 + 300) ^ *(byte *)((long)((int)lVar2 % 8) + 0x576);
      lVar2 = lVar2 + 1;
    }
    if (_SUB_0000012c == -0x1a76b7ab) {
      syscall();
      uVar1 = (*(code *)&SUB_0000012c)(1,0x586,0xc);
      return uVar1;
    }
  }
  
  ...
```

Here we are helped again as 2 constants are now embedded in the code - ```0x4a514f75``` and ```-0x1a76b7ab```. The first one is the half of the key, the second one we can use to determine the other half of the key by XORing it with the appropriate bytes in the encoded section. After a few calculations we get the key ```LJcbuOQJ``` and use it to decode the code block.

Rinse and repeat - we cut the file again and disassemble the encoded section. The new code is a little more complex. It too asks for the key and calls two subroutines that use it to decode yet another encoded section and return 8 bytes from it:

```c
  ...
  
  if ((int)((ulong)DAT_00000254 >> 0x20) == 0x36395477) {
    FUN_00000113(0,0x254,8,0x254,0x264);
    FUN_00000194(uVar3,uVar2,0x30,0x224,0x224,0x264);
    syscall();
    return ZEXT816(DAT_00000224);
  }
  
  ...
```

However, this time around only half of the key is known - ```0x36395477``` (```wT96```), so we will have to bruteforce the remaining 4 characters. The subroutines create an encoding table based on the key and use it to decode the encoded ```0x30``` bytes (of which we will really only need the first 8 bytes).

But how do we determine which 4-character combination is correct? We need some kind of a test. Thankfully in the original dowloader we have 4 8-byte checksum values, which we can apply, and see if the all the resulting characters in the flag are in the sane character range (```[a-zA-Z0-9_]```).

Let's reimplement the decoding algorithm in Python, add flag correctness check, and bruteforce through all possible 4-character key values:

```python
from pwn import *

def tryKey(key):
	
	#build initial table
	tab = bytearray(256)
	for i in range(256):
	  tab[i] = i
	
	# create full key
	fullkey = key+"wT96"
	
	# update table based on key
	k = 0
	uv = 0
	for i in range(256):
		cv = tab[i]
		uv = (uv + cv + ord(fullkey[k])) & 0xff
		tab[i] = tab[uv]
		tab[uv] = cv
		k = (k + 1) % 8

	# data to decode - taken from the binary
	enc_data = bytearray(b'\xF6\x2C\x72\x1A\x03\x99\x0E\x78\xBD\x90\xE9\x68\xD0\x69\x37\x29\xF8\x12\xF4\xE5\xD0\xFB\xF3\x7E\x72\x61\x79\x19\xED\x44\x12\x52\xF5\xF9\xAA\x14\x36\x0D\x1F\xB2\x52\x6B\xF2\x6A\xDA\x9D\xEC\x3C')
	
	# decode base value for the flag
	uv3 = 0
	uv4 = 0
	for i in range(8):
		
		uv3 = (uv3 + 1) & 0xff
		cv1 = tab[uv3]
		uv4 = (cv1 + uv4) & 0xff
		cv2 = tab[uv4]
		tab[uv3] = cv2
		tab[uv4] = cv1
		enc_data[i] = tab[(cv1 + cv2) & 0xff] ^ enc_data[i]
	
	x0 = b'\x11\x5c\x28\xda\x83\x4f\xef\xfd'
	x1 = b'\x66\x5f\x33\x6b\x1a\x56\x6b\x19'
	x2 = b'\x39\x3b\x41\x5f\x5a\x59\x00\x44'
	x3 = b'\x00\x03\x25\x55\x57\x37\x6f\x68'
	
	flag = bytearray(8*4)
	
	# now decode 4 8-byte pieces of the flag
	for x in range(8):
		enc_data[x] ^= ord(x0[x])
		flag[x] = enc_data[x]
		enc_data[x] ^= ord(x1[x])
		flag[x+8] = enc_data[x]
		enc_data[x] ^= ord(x2[x])
		flag[x+16] = enc_data[x]
		enc_data[x] ^= ord(x3[x])
		flag[x+24] = enc_data[x]
	
	# reverse the string and only leave 28 bytes of it
	flag = flag[::-1][0:28]
	
	# are the string characters in [a-zA-Z0-9_]?
	for x in range(len(flag)):
		if chr(flag[x]) not in string.ascii_letters+string.digits+'_':
			return False
	
	print "\n\n\n\nFlag found! key: "+fullkey+"   flag: fb{"+flag+"}\n\n\n\n"	
	return True

s = iters.mbruteforce(lambda x: tryKey(x), string.ascii_letters + string.digits, 4, 'fixed') 
```

When we run the script we get the flag within several minutes:

```sh
$ python decode.py 
[-] MBruteforcing: Trying "XzDw","XHXF","XHXG","Xrjr","YnAm","Yjqj","XL7O","XYB1" -- 80.155%




Flag found! key: YrQmwT96   flag: fb{Y0_daWg_1_h34rd_u_1ik3_fl4gs}



[+] MBruteforcing: Found key: "YrQm"
```

The flag is ```fb{Y0_daWg_1_h34rd_u_1ik3_fl4gs}```.
