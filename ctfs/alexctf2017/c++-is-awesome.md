---
layout: page
title: "Alex CTF 2017 Writeup: C++ is awesome"
---

> RE2: C++ is awesome
>
> 100
> 
> They say C++ is complex, prove them wrong!
>
> re2

The application ```re2``` requires correct flag to be passed on command line:

```sh
$ ./re2 
Usage: ./re2 flag
```

Let's open the executable in [Radare2](https://github.com/radare/radare2) and take a look. The main routine seems to be doing all the work so let's analyze it: 

```
$ r2 re2
[0x00400a60]> aa
[x] Analyze all flags starting with sym. and entry0 (aa)
[0x00400a60]> pdf @ main
/ (fcn) main 301
|   main ();
|           ; var int local_70h @ rbp-0x70
|           ; var int local_64h @ rbp-0x64
|           ; var int local_60h @ rbp-0x60
|           ; var int local_50h @ rbp-0x50
|           ; var int local_21h @ rbp-0x21
|           ; var int local_20h @ rbp-0x20
|           ; var int local_14h @ rbp-0x14
|           ; DATA XREF from 0x00400a7d (entry0)
|           0x00400b89      55             push rbp
|           0x00400b8a      4889e5         mov rbp, rsp
|           0x00400b8d      53             push rbx
|           0x00400b8e      4883ec68       sub rsp, 0x68               ; 'h'
|           0x00400b92      897d9c         mov dword [rbp - local_64h], edi
|           0x00400b95      48897590       mov qword [rbp - local_70h], rsi
|           0x00400b99      837d9c02       cmp dword [rbp - local_64h], 2 ; [0x2:4]=0x102464c
|       ,=< 0x00400b9d      7438           je 0x400bd7
|       |   0x00400b9f      488b4590       mov rax, qword [rbp - local_70h]
|       |   0x00400ba3      488b18         mov rbx, qword [rax]
|       |   0x00400ba6      be090f4000     mov esi, str.Usage:         ; "Usage: " @ 0x400f09
|       |   0x00400bab      bf40216000     mov edi, obj.std::cout      ; " (GNU) 6.1.1 20160721 (Red Hat 6.1.1-4)" @ 0x602140
|       |   0x00400bb0      e81bfeffff     call sym.std::operator___std::char_traits_char__
|       |   0x00400bb5      4889de         mov rsi, rbx
|       |   0x00400bb8      4889c7         mov rdi, rax
|       |   0x00400bbb      e810feffff     call sym.std::operator___std::char_traits_char__
|       |   0x00400bc0      be110f4000     mov esi, str.flag_n         ; " flag." @ 0x400f11
|       |   0x00400bc5      4889c7         mov rdi, rax
|       |   0x00400bc8      e803feffff     call sym.std::operator___std::char_traits_char__
|       |   0x00400bcd      bf00000000     mov edi, 0
|       |   0x00400bd2      e8b9fdffff     call sym.imp.exit           ; sym.std::allocator_char_::allocator-0x90
|       `-> 0x00400bd7      488d45df       lea rax, qword [rbp - local_21h]
|           0x00400bdb      4889c7         mov rdi, rax
|           0x00400bde      e83dfeffff     call sym.std::allocator_char_::allocator
|           0x00400be3      488b4590       mov rax, qword [rbp - local_70h]
|           0x00400be7      4883c008       add rax, 8
|           0x00400beb      488b08         mov rcx, qword [rax]
|           0x00400bee      488d55df       lea rdx, qword [rbp - local_21h]
|           0x00400bf2      488d45b0       lea rax, qword [rbp - local_50h]
|           0x00400bf6      4889ce         mov rsi, rcx
|           0x00400bf9      4889c7         mov rdi, rax
|           0x00400bfc      e80ffeffff     call sym.std::__cxx11::basic_string_char_std::char_traits_char__std::allocator_char__::basic_string
|           0x00400c01      488d45df       lea rax, qword [rbp - local_21h]
|           0x00400c05      4889c7         mov rdi, rax
|           0x00400c08      e8f3fdffff     call sym.std::allocator_char_::_allocator
|           0x00400c0d      c745ec000000.  mov dword [rbp - local_14h], 0
|           0x00400c14      488d45b0       lea rax, qword [rbp - local_50h]
|           0x00400c18      4889c7         mov rdi, rax
|           0x00400c1b      e830feffff     call sym.std::__cxx11::basic_string_char_std::char_traits_char__std::allocator_char__::begin
|           0x00400c20      488945a0       mov qword [rbp - local_60h], rax
|           ; JMP XREF from 0x00400c93 (main)
|       .-> 0x00400c24      488d45b0       lea rax, qword [rbp - local_50h]
|       |   0x00400c28      4889c7         mov rdi, rax
|       |   0x00400c2b      e8c0fdffff     call sym.std::__cxx11::basic_string_char_std::char_traits_char__std::allocator_char__::end
|       |   0x00400c30      488945e0       mov qword [rbp - local_20h], rax
|       |   0x00400c34      488d55e0       lea rdx, qword [rbp - local_20h]
|       |   0x00400c38      488d45a0       lea rax, qword [rbp - local_60h]
|       |   0x00400c3c      4889d6         mov rsi, rdx
|       |   0x00400c3f      4889c7         mov rdi, rax
|       |   0x00400c42      e8f6000000     call fcn.00400d3d
|       |   0x00400c47      84c0           test al, al
|      ,==< 0x00400c49      744a           je 0x400c95
|      ||   0x00400c4b      488d45a0       lea rax, qword [rbp - local_60h]
|      ||   0x00400c4f      4889c7         mov rdi, rax
|      ||   0x00400c52      e843010000     call fcn.00400d9a
|      ||   0x00400c57      0fb610         movzx edx, byte [rax]
|      ||   0x00400c5a      488b0d3f1420.  mov rcx, qword [0x006020a0] ; [0x6020a0:8]=0x400e58 str.L3t_ME_T3ll_Y0u_S0m3th1ng_1mp0rtant_A__FL4G__W0nt_b3_3X4ctly_th4t_345y_t0_c4ptur3_H0wev3r_1T_w1ll_b3_C00l_1F_Y0u_g0t_1t ; "X.@"
|      ||   0x00400c61      8b45ec         mov eax, dword [rbp - local_14h]
|      ||   0x00400c64      4898           cdqe
|      ||   0x00400c66      8b0485c02060.  mov eax, dword [rax*4 + 0x6020c0] ; [0x6020c0:4]=36 ; "$"
|      ||   0x00400c6d      4898           cdqe
|      ||   0x00400c6f      4801c8         add rax, rcx                ; '&'
|      ||   0x00400c72      0fb600         movzx eax, byte [rax]
|      ||   0x00400c75      38c2           cmp dl, al
|      ||   0x00400c77      0f95c0         setne al
|      ||   0x00400c7a      84c0           test al, al
|     ,===< 0x00400c7c      7405           je 0x400c83
|     |||   0x00400c7e      e8d3feffff     call fcn.00400b56
|     `---> 0x00400c83      8345ec01       add dword [rbp - local_14h], 1
|      ||   0x00400c87      488d45a0       lea rax, qword [rbp - local_60h]
|      ||   0x00400c8b      4889c7         mov rdi, rax
|      ||   0x00400c8e      e8e7000000     call fcn.00400d7a
|      |`=< 0x00400c93      eb8f           jmp 0x400c24
|      `--> 0x00400c95      e8d9feffff     call fcn.00400b73
|           0x00400c9a      bb00000000     mov ebx, 0
|           0x00400c9f      488d45b0       lea rax, qword [rbp - local_50h]
|           0x00400ca3      4889c7         mov rdi, rax
|           0x00400ca6      e835fdffff     call sym.std::__cxx11::basic_string_char_std::char_traits_char__std::allocator_char__::_basic_string
|           0x00400cab      89d8           mov eax, ebx
\       ,=< 0x00400cad      eb34           jmp loc.00400ce3
|- loc.00400ce3 7
|   loc.00400ce3 ();
|           ; JMP XREF from 0x00400cad (main)
|           0x00400ce3      4883c468       add rsp, 0x68               ; 'h'
|           0x00400ce7      5b             pop rbx
|           0x00400ce8      5d             pop rbp
\           0x00400ce9      c3             ret
```

Application processes the command line parameter in a loop. For each character it goes to a lookup table (```0x6020c0```) and uses the value from it as an index into the long embedded string at ```0x006020a0```:

```
L3t_ME_T3ll_Y0u_S0m3th1ng_1mp0rtant_A_{FL4G}_W0nt_b3_3X4ctly_th4t_345y_t0_c4ptur3_H0wev3r_1T_w1ll_b3_C00l_1F_Y0u_g0t_1t
```

If the character in the parameter matches the character in the embedded string (compared at ```0x00400c75```) it continues. When the whole flag is matched, it is output.

To reverse the flag we can simply extract the embedded string and the lookup table from the binary, and repeat the lookup logic:

```python
from pwn import *

data = open('re2', 'rb').read()

str_data = data[0xe58:0xecf]
offset_data = data[0x20c0:0x213c]

offsets = unpack_many(offset_data, 32)

flag = ''
for x in offsets:
    flag += str_data[x]

print flag
```

The flag is ```ALEXCTF{W3_L0v3_C_W1th_CL45535}```.