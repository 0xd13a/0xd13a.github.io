---
layout: page
title: "Kaspersky Industrial CTF 2018 Writeup: glardomos"
---

> glardomos
>
> 587
>
> Find the flag inside the binary
> 
> [Glardomos.exe]({{ site.baseurl }}/ctfs/kasp2018/glardomos/Glardomos.zip)

Glardomos is a .Net executable:

```
$ file Glardomos.exe 
Glardomos.exe: PE32 executable (console) Intel 80386 Mono/.Net assembly, for MS Windows
```

It requres flag as parameter and verifies it:

```
C:\work\kasp18>Glardomos.exe
Glardomos <flag>

C:\work\kasp18>Glardomos.exe AAAAAAA
Failed!
```

Upon closer look the executable seems to be obfuscated with the [Confuser](https://github.com/yck1509/ConfuserEx) tool (as indicated by presence of attribute ```ConfusedByAttribute```), which may have been further modified.

This challenge would be very tough to solve without a great debugger [dnspy](https://github.com/0xd4d/dnSpy) and deobfuscator [de4dot](https://github.com/0xd4d/de4dot). Even with them it is a lot of trial and error as neither of them seem to support removal of Confuser code out of the box.

The following sequence of steps worked:

- Load ```Glardomos.exe``` in dnspy and set up a breakpoint in constructor of the main module:

![]({{ site.baseurl }}/ctfs/kasp2018/glardomos/shot1.png)

- Step through all code in that method. A new Glardomos module will appear in the tree. Save it to a new file (using menu command ```File/Save Module...```), and select options to preserve most information and tokens in the Save dialog.

- Run de4dot tool on the saved module to make the symbolic names more sane:

```
C:\work\kasp18>..\..\util\de4dot\de4dot.exe Glardomos_2.exe

de4dot v3.1.41592.3405 Copyright (C) 2011-2015 de4dot@gmail.com
Latest version and source code: https://github.com/0xd4d/de4dot

Detected Unknown Obfuscator (C:\work\kasp18\Glardomos_2.exe)
Cleaning C:\work\kasp18\Glardomos_2.exe
Renaming all obfuscated symbols
Saving C:\work\kasp18\Glardomos_2-cleaned.exe
``` 

- Load cleaned module in dnspy again. The code still looks very cryptic, however some of the methods being called become more clear. Note that the application creates and calls a PowerShell script:

```
static PowerShell smethod_8(PowerShell powerShell_0, string string_0)
{
	return powerShell_0.AddScript(string_0);
}
```

- Set up a breakpoint inside ```System.Management.Automation.Powershell``` and run the module with some random flag value as a parameter

- When the breakpoint is hit the following code is added to the script:

```powershell
$flag="AAAAAAAAA";
. ((varIAbLe '*MDR*').NAME[3,11,2]-jOiN'') ( ...VERY LARGE PAYLOAD REMOVED...
```

- When saved to a file and executed the script generates the same error as the executable, which probably means that it is the part that actually parses the flag:

```
C:\work\kasp18>powershell -executionpolicy bypass -file ps.ps1
Failed!
```

- Looking at the script we can see that there is a large blob of encoded script code inside that we need to analyze. By copying it into a separate file and executing it we unwrap multiple levels of encoding one by one, finally arriving at the following code:

```powershell
...
$rv=$FALSE;

if ($flag.length -ne 39){}
elseif ($flag[0] -ne 'K'){}
elseif ($flag[1] -ne 'L'){}
elseif ($flag[2] -ne 'C'){}
elseif ($flag[3] -ne 'T'){}
elseif ($flag[4] -ne 'F'){}
elseif ($flag[5] -ne '{'){}
elseif ($flag[6] -ne '3'){}
elseif ($flag[7] -ne '4'){}
elseif ($flag[8] -ne 'O'){}
elseif ($flag[9] -ne 'K'){}
elseif ($flag[10] -ne '3'){}
elseif ($flag[11] -ne 'B'){}
elseif ($flag[12] -ne 'P'){}
elseif ($flag[13] -ne 'K'){}
elseif ($flag[14] -ne '3'){}
elseif ($flag[15] -ne '3'){}
elseif ($flag[16] -ne 'H'){}
elseif ($flag[17] -ne '0'){}
elseif ($flag[18] -ne 'S'){}
elseif ($flag[19] -ne 'Z'){}
elseif ($flag[20] -ne 'X'){}
elseif ($flag[21] -ne '3'){}
elseif ($flag[22] -ne 'Y'){}
elseif ($flag[23] -ne 'Z'){}
elseif ($flag[24] -ne 'X'){}
elseif ($flag[25] -ne 'N'){}
elseif ($flag[26] -ne '2'){}
elseif ($flag[27] -ne 'V'){}
elseif ($flag[28] -ne 'C'){}
elseif ($flag[29] -ne 'J'){}
elseif ($flag[30] -ne 'V'){}
elseif ($flag[31] -ne '2'){}
elseif ($flag[32] -ne '4'){}
elseif ($flag[33] -ne 'C'){}
elseif ($flag[34] -ne 'P'){}
elseif ($flag[35] -ne '6'){}
elseif ($flag[36] -ne 'Y'){}
elseif ($flag[37] -ne 'H'){}
elseif ($flag[38] -ne '}'){}
else
{
  $rv=$TRUE;
}

if($rv)
{
  Write-Output "Success!"
}
else
{
  Write-Output "Failed!"
}
```

Bingo! The flag is ```KLCTF{34OK3BPK33H0SZX3YZXN2VCJV24CP6YH}```.