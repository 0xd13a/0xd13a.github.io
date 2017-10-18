---
layout: page
title: "FLARE-ON 2017 Writeup: IgniteMe.exe"
---

This is another simple challenge. When run, it prompts for a password and verifies it:

```
C:\work\flareon17>IgniteMe.exe
G1v3 m3 t3h fl4g:  test
N0t t00 h0t R we? 7ry 4ga1nz plzzz!
```

Quick analysis in IDA Pro shows that the "meat" of password decoding logic is in ```sub_401050```:

```
.text:00401050 sub_401050      proc near               ; CODE XREF: start+44
.text:00401050
.text:00401050 varFlagSize     = dword ptr -0Ch
.text:00401050 varLoopCounter  = dword ptr -8
.text:00401050 varEncodingFactor= byte ptr -1
.text:00401050
.text:00401050                 push    ebp
.text:00401051                 mov     ebp, esp
.text:00401053                 sub     esp, 0Ch
.text:00401056                 push    offset byte_403078
.text:0040105B                 call    sub_401020      ; determine encoded flag size and put it in varFlagSize
.text:00401060                 add     esp, 4
.text:00401063                 mov     [ebp+varFlagSize], eax
.text:00401066                 call    sub_401000      ; calculate initial encoding factor
.text:0040106B                 mov     [ebp+varEncodingFactor], al
.text:0040106E                 mov     eax, [ebp+varFlagSize]
.text:00401071                 sub     eax, 1
.text:00401074                 mov     [ebp+varLoopCounter], eax
.text:00401077                 jmp     short loc_401082
.text:00401079 ; ---------------------------------------------------------------------------
.text:00401079
.text:00401079 loc_401079:                             ; CODE XREF: sub_401050+5D
.text:00401079                 mov     ecx, [ebp+varLoopCounter]
.text:0040107C                 sub     ecx, 1
.text:0040107F                 mov     [ebp+varLoopCounter], ecx
.text:00401082
.text:00401082 loc_401082:                             ; CODE XREF: sub_401050+27
.text:00401082                 cmp     [ebp+varLoopCounter], 0 ; run through input string
.text:00401086                 jl      short loc_4010AF
.text:00401088                 mov     edx, [ebp+varLoopCounter]
.text:0040108B                 movsx   eax, byte_403078[edx]
.text:00401092                 movzx   ecx, [ebp+varEncodingFactor]
.text:00401096                 xor     eax, ecx        ; encode one character by XORing it with factor
.text:00401098                 mov     edx, [ebp+varLoopCounter]
.text:0040109B                 mov     byte_403180[edx], al
.text:004010A1                 mov     eax, [ebp+varLoopCounter]
.text:004010A4                 mov     cl, byte_403078[eax]
.text:004010AA                 mov     [ebp+varEncodingFactor], cl ; set the factor to the encoded character value
.text:004010AD                 jmp     short loc_401079
.text:004010AF ; ---------------------------------------------------------------------------
.text:004010AF
.text:004010AF loc_4010AF:                             ; CODE XREF: sub_401050+36
.text:004010AF                 mov     [ebp+varLoopCounter], 0
.text:004010B6                 jmp     short loc_4010C1
.text:004010B8 ; ---------------------------------------------------------------------------
.text:004010B8
.text:004010B8 loc_4010B8:                             ; CODE XREF: sub_401050:loc_4010E3
.text:004010B8                 mov     edx, [ebp+varLoopCounter]
.text:004010BB                 add     edx, 1
.text:004010BE                 mov     [ebp+varLoopCounter], edx
.text:004010C1
.text:004010C1 loc_4010C1:                             ; CODE XREF: sub_401050+66
.text:004010C1                 cmp     [ebp+varLoopCounter], 27h
.text:004010C5                 jnb     short loc_4010E5
.text:004010C7                 mov     eax, [ebp+varLoopCounter]
.text:004010CA                 movsx   ecx, byte_403180[eax]
.text:004010D1                 mov     edx, [ebp+varLoopCounter]
.text:004010D4                 movzx   eax, byte_403000[edx]
.text:004010DB                 cmp     ecx, eax        ; compare each string character with corresponding character in encoded flag
.text:004010DD                 jz      short loc_4010E3
.text:004010DF                 xor     eax, eax        ; if character does not match exit with error
.text:004010E1                 jmp     short loc_4010EA
.text:004010E3 ; ---------------------------------------------------------------------------
.text:004010E3
.text:004010E3 loc_4010E3:                             ; CODE XREF: sub_401050+8D
.text:004010E3                 jmp     short loc_4010B8
.text:004010E5 ; ---------------------------------------------------------------------------
.text:004010E5
.text:004010E5 loc_4010E5:                             ; CODE XREF: sub_401050+75
.text:004010E5                 mov     eax, 1
.text:004010EA
.text:004010EA loc_4010EA:                             ; CODE XREF: sub_401050+91
.text:004010EA                 mov     esp, ebp
.text:004010EC                 pop     ebp
.text:004010ED                 retn
.text:004010ED sub_401050      endp
```

Let's replicate the algorithm in Python:

```python
data = bytearray(open("IgniteMe.exe","rb").read())[0xa00:0xa00+0x27]

out = ""

factor = 4 # (rol(0x80070057 & 0xffff0000,4) >> 1) & 0xFF

for x in range(len(data)):
	factor = data[len(data)-x-1] ^ factor
	out += chr(factor)
	
print out[::-1]
```

Running the script gets us the flag:

```
C:\work\flareon17>\Python27\python.exe ignite_solve.py
R_y0u_H0t_3n0ugH_t0_1gn1t3@flare-on.com
```