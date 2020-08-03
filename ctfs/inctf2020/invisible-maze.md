---
layout: page
title: "InCTF 2020 Writeup: Invisible Maze"
---

> Invisible Maze
> 
> 956
> 
> Move the flag in just the right ways or just hack the game!
> 
> Author: officialcjunior
> 
> Download: [output.gb]({{ site.baseurl }}/ctfs/inctf2020/invisible-maze/output.gb)

This is a fun little GameBoy reversing challenge. Absent of actual hardware let's download an emulator [BizHawk](http://tasvideos.org/Bizhawk.html).

The game requires you to move the flag across the field until you hit the spot where the flag is hidden:

![screenshot1]({{ site.baseurl }}/ctfs/inctf2020/invisible-maze/screenshot1.png)

![screenshot2]({{ site.baseurl }}/ctfs/inctf2020/invisible-maze/screenshot2.png)

Ghidra does not support GameBoy out of the box, but we can download and install a [GhidraBoy extension](https://github.com/Gekkio/GhidraBoy).

Opening the game file in Ghidra is not enough as not all code is easily recognized as such. We would need to go though entire file surface and manually convert to code anything that looks like code (press D in the Listing window).

Once all code is decompiled there is a particular function that seems to decode the actual flag:

```c
void FUN_0200(char param_5,byte param_6)
{
  byte i_00;
  short i;
  byte bStack0005;
  
  i = 0;
  while ((bStack0005 ^ 0x80) < 0x80 || (byte)((bStack0005 ^ 0x80) + 0x80) < (i_00 < 0x1d)) {
    DAT_c1a0 = (undefined)((ushort)("KRUHZ=\x03\t\a!R\x04!0\x05\n\x05RX!\x04:!>\x05PX\b?" + i)>> 8)
    ;
    DAT_c19f = "KRUHZ=\x03\t\a!R\x04!0\x05\n\x05RX!\x04:!>\x05PX\b?"[i] + param_5;
    (&DAT_c0d1)[i] = param_6 ^ DAT_c19f;
    i = i + 1;
  }
  (&DAT_c0d1)[i] = 0;
  FUN_1658("Way to go! \n flg if u not noob: %s\n",&DAT_c0d1);
  return;
}
```

Essentially it takes in 2 parameters (I guess they are dependent on the position of the flag icon in the field) and uses them to decrypt the flag data. The 2 parameters are bytes, so it's easy enough to bruteforce all possible combinations. Let's write this in Python:

```python
import string

enc = [0x4B, 0x52, 0x55, 0x48, 0x5A, 0x3D, 0x03, 0x09, 0x07, 0x21, 0x52, 0x04, 0x21, 0x30, 0x05, 0x0A, 0x05, 0x52, 0x58, 0x21, 0x04, 0x3A, 0x21, 0x3E, 0x05, 0x50, 0x58, 0x08, 0x3F]
for i in range(0x100):
	for j in range(0x100):
		out = ''
		for x in range(len(enc)):
			out += chr(((enc[x] + i) & 0xff) ^ j)
		if (out.startswith("inctf{") and all(c in string.printable for c in out)):
			print(out, i, j)
```

Running the script gives us the flag:

```
$ python solve.py 
('inctf{175_n0_L363nd_0F_z3ld4}', 37, 25)
('inctf{175_n0_L363nd_0F_z3ld4}', 165, 153)
```

The flag is ```inctf{175_n0_L363nd_0F_z3ld4}```.