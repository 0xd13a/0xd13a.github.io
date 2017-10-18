---
layout: page
title: "FLARE-ON 2017 Writeup: remorse.ino.hex"
---

Tackling an Arduino challenge was intimidating, I have never dealt with that processor before. However things turned out to be not that complicated. 

The first step was to familiarize myself with the [instruction set](http://www.atmel.com/images/Atmel-0856-AVR-Instruction-Set-Manual.pdf).

Luckily IDA Pro knows how to open HEX files (you just have to select the right processor). Disassembly of HEX files can also be done in [www.onlinedisassembler.com](https://www.onlinedisassembler.com/static/home/).

A big help was an annotated decompile of a [simple Arduino application]({{ site.baseurl }}/ctfs/flareon2017/remorse/serial_print_example.txt) originally posted at [https://pastebin.com/kH5jpdpX](https://pastebin.com/kH5jpdpX).

I quickly saw similarities between that and the decompile of the challenge binary, spotting standard functions that do initialization and printing (and therefore can be separated from the actual application logic). Namely functions at addresses ```0x39b``` and ```0x31d``` can be called ```write``` functions, and function at address ```0x332``` can be called ```writeWithNewline```. 

Let's assume that the write routine will eventually be used to write out the flag and cross reference all uses of these functions in the code. It turns out that there is a particular function (```sub_536```) that uses writes and does other interesting behavior that looks like decoding of a flag.

In the function there are several sections:

- 0x548-0x572 - encrypted flag bytes are stored in memory

- 0x575-0x57c - flag is decrypted

- 0x57d-0x580 - flag character at position 10 is checked to be '@'

- 0x58c-0x590 - flag is printed out

```
ROM:0536          sub_536:                                ; CODE XREF: sub_5A0+1E
ROM:0536 93CF          push    r28
ROM:0537 93DF          push    r29
ROM:0538 B7CD          in      r28, SPL  
ROM:0539 B7DE          in      r29, SPH 
ROM:053A 95DA          dec     r29
ROM:053B B60F          in      r0, SREG 
ROM:053C 94F8          cli
ROM:053D BFDE          out     SPH, r29 
ROM:053E BE0F          out     SREG, r0 
ROM:053F BFCD          out     SPL, r28 
ROM:0540 01FE          movw    r30, r28
ROM:0541 9631          adiw    r30, 1
ROM:0542 01DF          movw    r26, r30
ROM:0543 EF9F          ser     r25
ROM:0544 0F9E          add     r25, r30
ROM:0545
ROM:0545          loc_545:                                ; CODE XREF: sub_536+11
ROM:0545 921D          st      X+, r1
ROM:0546 139A          cpse    r25, r26
ROM:0547 CFFD          rjmp    loc_545
ROM:0548 EB95          ldi     r25, 0xB5 ;
ROM:0549 8399          std     Y+1, r25  ;  store byte 0xb5
ROM:054A 839A          std     Y+2, r25  ;  store byte 0xb5
ROM:054B E896          ldi     r25, 0x86 ;
ROM:054C 839B          std     Y+3, r25  ;  store byte 0x86
ROM:054D EB94          ldi     r25, 0xB4 ;
ROM:054E 839C          std     Y+4, r25  ;  store byte 0xb4
ROM:054F EF94          ldi     r25, 0xF4 ;
ROM:0550 839D          std     Y+5, r25  ;  store byte 0xf4
ROM:0551 EB93          ldi     r25, 0xB3 ;
ROM:0552 839E          std     Y+6, r25  ;  store byte 0xb3
ROM:0553 EF91          ldi     r25, 0xF1 ;
ROM:0554 839F          std     Y+7, r25  ;  store byte 0xf1
ROM:0555 EB20          ldi     r18, 0xB0 ;
ROM:0556 8728          std     Y+8, r18  ;  store byte 0xb0
ROM:0557 8729          std     Y+9, r18  ;  store byte 0xb0
ROM:0558 879A          std     Y+0xA, r25;  store byte 0xf1
ROM:0559 EE9D          ldi     r25, 0xED ;
ROM:055A 879B          std     Y+0xB, r25;  store byte 0xed
ROM:055B E890          ldi     r25, 0x80 ;
ROM:055C 879C          std     Y+0xC, r25;  store byte 0x80
ROM:055D EB9B          ldi     r25, 0xBB ;
ROM:055E 879D          std     Y+0xD, r25;  store byte 0xbb
ROM:055F E89F          ldi     r25, 0x8F ;
ROM:0560 879E          std     Y+0xE, r25;  store byte 0x8f
ROM:0561 EB9F          ldi     r25, 0xBF ;
ROM:0562 879F          std     Y+0xF, r25;  store byte 0xbf
ROM:0563 E89D          ldi     r25, 0x8D ;
ROM:0564 8B98          std     Y+0x10, r25; store byte 0x8d
ROM:0565 EC96          ldi     r25, 0xC6 ;
ROM:0566 8B99          std     Y+0x11, r25; store byte 0xc6
ROM:0567 E895          ldi     r25, 0x85 ;
ROM:0568 8B9A          std     Y+0x12, r25; store byte 0x85
ROM:0569 E897          ldi     r25, 0x87 ; 
ROM:056A 8B9B          std     Y+0x13, r25; store byte 0x87
ROM:056B EC90          ldi     r25, 0xC0 ; 
ROM:056C 8B9C          std     Y+0x14, r25; store byte 0xc0
ROM:056D E994          ldi     r25, 0x94 ;
ROM:056E 8B9D          std     Y+0x15, r25; store byte 0x94
ROM:056F E891          ldi     r25, 0x81 ;
ROM:0570 8B9E          std     Y+0x16, r25; store byte 0x81
ROM:0571 E89C          ldi     r25, 0x8C ; 
ROM:0572 8B9F          std     Y+0x17, r25; store byte 0x8c
ROM:0573 E6AC          ldi     r26, 0x6C ;
ROM:0574 E0B5          ldi     r27, 5
ROM:0575 E020          ldi     r18, 0    ;  set loop counter to 0
ROM:0576
ROM:0576          loc_576:                                ; CODE XREF: sub_536+46
ROM:0576 9191          ld      r25, Z+   ;  load encoded value to r25
ROM:0577 2798          eor     r25, r24  ;  XOR with r24
ROM:0578 0F92          add     r25, r18  ;  add loop counter
ROM:0579 939D          st      X+, r25   ;  store decoded character
ROM:057A 5F2F          subi    r18, -1   ;  increment loop counter
ROM:057B 3127          cpi     r18, 0x17 ;  quit once we have processed 0x17 characters
ROM:057C F7C9          brne    loc_576
ROM:057D 9180 0576     lds     r24, 0x576;  load character from position 10 in the string (0x576-0x56c)
ROM:057F 3480          cpi     r24, 0x40 ;  make sure it is '@'
ROM:0580 F4A1          brne    loc_595
ROM:0581 E26B          ldi     r22, 0x2B ; 
ROM:0582 E075          ldi     r23, 5
ROM:0583 E88F          ldi     r24, 0x8F ;
ROM:0584 E095          ldi     r25, 5
ROM:0585 940E 0332     call    subWriteWithNewline_332 ; write Correct Pin State message
ROM:0587 E167          ldi     r22, 0x17
ROM:0588 E68C          ldi     r24, 0x6C ;  load flag address (0x56c)
ROM:0589 E095          ldi     r25, 5
ROM:058A 940E 04F0     call    sub_4F0
ROM:058C E66C          ldi     r22, 0x6C ; 
ROM:058D E075          ldi     r23, 5          
ROM:058E E88F          ldi     r24, 0x8F ;
ROM:058F E095          ldi     r25, 5
ROM:0590 940E 0332     call    subWriteWithNewline_332 ; write flag
ROM:0592 E081          ldi     r24, 1
ROM:0593 E090          ldi     r25, 0
ROM:0594 C002          rjmp    loc_597
ROM:0595          ; ---------------------------------------------------------------------------
ROM:0595
ROM:0595          loc_595:                                ; CODE XREF: sub_536+4A
ROM:0595 E080          ldi     r24, 0
ROM:0596 E090          ldi     r25, 0
ROM:0597
ROM:0597          loc_597:                                ; CODE XREF: sub_536+5E
ROM:0597 95D3          inc     r29
ROM:0598 B60F          in      r0, SREG
ROM:0599 94F8          cli
ROM:059A BFDE          out     SPH, r29  
ROM:059B BE0F          out     SREG, r0 
ROM:059C BFCD          out     SPL, r28 
ROM:059D 91DF          pop     r29
ROM:059E 91CF          pop     r28
ROM:059F 9508          ret
ROM:059F          ; End of function sub_536
``` 

In this algorithm the only unknown factor is the value of ```r24```, but to save time we can bruteforce it.

Let us rewrite the algorithm in Python:

```python
flag = [0xB5, 0xB5, 0x86, 0xB4, 0xF4, 0xB3, 0xF1, 0xB0, 0xB0, 0xF1, 0xED, 0x80, 0xBB, 0x8F, 0xBF, 0x8D, 0xC6, 0x85, 0x87, 0xC0, 0x94, 0x81, 0x8C]

for z in range(0x100):
	out = ""

	for x in range(len(flag)):
		out += chr(((flag[x] ^ z) + x) & 0xFF)
		
	if out[10] == '@':	
		print out
		break
```

Running the script gets us the flag:

```
C:\work\flareon17\remorse>\Python27\python.exe remorse_solve.py
no_r3m0rs3@flare-on.com
```