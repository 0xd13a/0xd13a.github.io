---
layout: page
title: "InCTF 2020 Writeup: deadsimplecrackme"
---

> deadsimplecrackme
> 
> 100
> 
> This one is an easy one.
> 
> Author silverf3lix
> 
> Download: [deadsimplecrackme]({{ site.baseurl }}/ctfs/inctf2020/deadsimplecrackme/EBOOT.PBP)

This is a PSP reversing challenge, so let's set up an [emulator](https://www.ppsspp.org/).

We are asked to press a bunch of buttons on the controller to reveal the key:

![screenshot1]({{ site.baseurl }}/ctfs/inctf2020/deadsimplecrackme/screenshot1.png)

We can extract the ELF executable out of the PBP file and open in in Ghidra. The following is the function of interest:

```c
void _gp_6(int *param_1,int param_2)
{
  int iVar1;
  int iVar2;
  int iVar3;
  int iVar4;
  int iVar5;
  int iVar6;
  int iVar7;
  int iVar8;
  int iVar9;
  int iVar10;
  int iVar11;
  uint *local_50;
  code *local_4c;
  uint local_44;
  undefined **local_40;
  undefined4 local_3c;
  undefined4 local_38;
  undefined **local_30;
  undefined4 local_2c;
  
  iVar1 = *param_1;
  iVar2 = param_1[1];
  iVar8 = param_1[8];
  iVar10 = param_1[7];
  iVar11 = 0;
  iVar3 = param_1[2];
  iVar4 = param_1[3];
  iVar5 = param_1[4];
  iVar6 = param_1[5];
  iVar7 = param_1[9];
  iVar9 = param_1[6];
  do {
    local_2c = 1;
    local_3c = 1;
    local_4c = h8dc2c151a61fc5c7;
    local_40 = &PTR_LOOP_0008b68c;
    local_38 = 0;
    local_44 = (uint)(byte)hd21e1c288d6314bc[iVar11] ^
               iVar7 + iVar8 + iVar10 + iVar9 + iVar6 + iVar5 + iVar4 + iVar3 + iVar2 + iVar1 +
               param_2 & 0xffU;
    local_50 = &local_44;
    local_30 = (undefined **)&local_50;
    h3ee82e4c30d7dcbc(&local_40);
    iVar11 = iVar11 + 4;
  } while (iVar11 != 0x120);
  local_3c = 1;
  local_30 = &PTR_LOOP_0008b68c;
  local_40 = &PTR_DAT_0008b684;
  local_2c = 0;
  local_38 = 0;
  h3ee82e4c30d7dcbc(&local_40);
  return;
}
```

Here data at location ```hd21e1c288d6314bc``` is XORed with codes from button presses. Instead of digging further let's simply bruteforce the code in CyberChef:

![screenshot2]({{ site.baseurl }}/ctfs/inctf2020/deadsimplecrackme/screenshot2.png)

Bingo!  The flag is ```inctf{s3xy_lil_machine_running_at_333mhz}```.
