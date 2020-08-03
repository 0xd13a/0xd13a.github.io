---
layout: page
title: "InCTF 2020 Writeup: ArchRide"
---

> ArchRide
> 
> 573
> 
> I have a long one planned. Buckle up! Limit your character check to the printable range please :)
> 
> Authors: 4lex1, Mr_UnKnOwN
>
> Download: [surprise]({{ site.baseurl }}/ctfs/inctf2020/archride/surprise)

This challenge has more than meets the eye. 

We start with a bzip2 archive that contains an executable. When run, it asks for a key; if they is wrong you get an error message, if it's right it uses it to decode and drop another archive. Then we start all over again. 

There are two algorithms inside the executable that check validity of the key. Here are Ghidra decompiles:

```c
ulong FUN_00100949(byte *param_1)

{
  ulong uVar1;
  
  if ((int)(char)(param_1[2] ^ *param_1 ^ param_1[4]) == DAT_00302020) {
    uVar1 = (ulong)DAT_00302024;
    if ((((((int)(char)(param_1[6] ^ param_1[2] ^ param_1[4]) == DAT_00302024) &&
          (uVar1 = (ulong)DAT_00302028,
          (int)(char)(param_1[8] ^ param_1[4] ^ param_1[6]) == DAT_00302028)) &&
         (uVar1 = (ulong)DAT_0030202c,
         (int)(char)(param_1[10] ^ param_1[6] ^ param_1[8]) == DAT_0030202c)) &&
        (((uVar1 = (ulong)DAT_00302030,
          (int)(char)(param_1[0xc] ^ param_1[8] ^ param_1[10]) == DAT_00302030 &&
          (uVar1 = (ulong)DAT_00302034,
          (int)(char)(param_1[1] ^ param_1[10] ^ param_1[0xc]) == DAT_00302034)) &&
         ((uVar1 = (ulong)DAT_00302038,
          (int)(char)(param_1[3] ^ param_1[0xc] ^ param_1[1]) == DAT_00302038 &&
          ((uVar1 = (ulong)DAT_0030203c,
           (int)(char)(param_1[5] ^ param_1[1] ^ param_1[3]) == DAT_0030203c &&
           (uVar1 = (ulong)DAT_00302040,
           (int)(char)(param_1[7] ^ param_1[3] ^ param_1[5]) == DAT_00302040)))))))) &&
       ((uVar1 = (ulong)DAT_00302044,
        (int)(char)(param_1[9] ^ param_1[5] ^ param_1[7]) == DAT_00302044 &&
        ((((uVar1 = (ulong)DAT_00302048,
           (int)(char)(param_1[0xb] ^ param_1[7] ^ param_1[9]) == DAT_00302048 &&
           (uVar1 = (ulong)DAT_0030204c,
           (int)(char)(param_1[0xd] ^ param_1[9] ^ param_1[0xb]) == DAT_0030204c)) &&
          (uVar1 = (ulong)DAT_00302050,
          (int)(char)(*param_1 ^ param_1[0xb] ^ param_1[0xd]) == DAT_00302050)) &&
         (uVar1 = (ulong)DAT_00302054,
         (int)(char)(param_1[2] ^ param_1[0xd] ^ *param_1) == DAT_00302054)))))) {
      uVar1 = 1;
    }
  }
  else {
    uVar1 = 0;
  }
  return uVar1;
}

...

ulong FUN_00100c3d(byte *param_1)

{
  ulong uVar1;
  
  if ((int)(char)(param_1[2] ^ *param_1 ^ param_1[1]) == DAT_00302060) {
    uVar1 = (ulong)DAT_00302064;
    if ((((((int)(char)(param_1[3] ^ param_1[1] ^ param_1[2]) == DAT_00302064) &&
          (uVar1 = (ulong)DAT_00302068,
          (int)(char)(param_1[4] ^ param_1[2] ^ param_1[3]) == DAT_00302068)) &&
         (uVar1 = (ulong)DAT_0030206c,
         (int)(char)(param_1[5] ^ param_1[3] ^ param_1[4]) == DAT_0030206c)) &&
        (((uVar1 = (ulong)DAT_00302070,
          (int)(char)(param_1[6] ^ param_1[4] ^ param_1[5]) == DAT_00302070 &&
          (uVar1 = (ulong)DAT_00302074,
          (int)(char)(param_1[7] ^ param_1[5] ^ param_1[6]) == DAT_00302074)) &&
         ((uVar1 = (ulong)DAT_00302078,
          (int)(char)(param_1[8] ^ param_1[6] ^ param_1[7]) == DAT_00302078 &&
          ((uVar1 = (ulong)DAT_0030207c,
           (int)(char)(param_1[9] ^ param_1[7] ^ param_1[8]) == DAT_0030207c &&
           (uVar1 = (ulong)DAT_00302080,
           (int)(char)(param_1[10] ^ param_1[8] ^ param_1[9]) == DAT_00302080)))))))) &&
       ((uVar1 = (ulong)DAT_00302084,
        (int)(char)(param_1[0xb] ^ param_1[9] ^ param_1[10]) == DAT_00302084 &&
        ((((uVar1 = (ulong)DAT_00302088,
           (int)(char)(param_1[0xc] ^ param_1[10] ^ param_1[0xb]) == DAT_00302088 &&
           (uVar1 = (ulong)DAT_0030208c,
           (int)(char)(param_1[0xd] ^ param_1[0xb] ^ param_1[0xc]) == DAT_0030208c)) &&
          (uVar1 = (ulong)DAT_00302090,
          (int)(char)(*param_1 ^ param_1[0xc] ^ param_1[0xd]) == DAT_00302090)) &&
         (uVar1 = (ulong)DAT_00302094,
         (int)(char)(param_1[1] ^ param_1[0xd] ^ *param_1) == DAT_00302094)))))) {
      uVar1 = 1;
    }
  }
  else {
    uVar1 = 0;
  }
  return uVar1;
}
```

Once the validity is confirmed a simple XOR with the key over the data area in the executable produce the new bzip2 archive.

There are good news and bad news. The good news is that the algorithms to check the key and decode the archive are the same for every nested file, we can automate that. The bad news is that the offsets of data to XOR with and decode vary from executable to executable. What's worse, the executables are compiled for different platforms and we cannot simply execute them all to decode data, we would have to replicate the decoding algorithm.

To solve these issues we will:

* Use Z3 to find the right key characters that match all restrictions
* Try several different offsets in the executables to find the right ones
* Decode and drop each archive ourselves, recursively processing all the nested levels. We don't know where the archive data ends so we will simply take all data until the end of the executable (bzip2 is good at ignoring junk at the end)

Here's the algorithm:

```python
from z3 import *
import os

# Decode the embedded archive using values at specified offsets
def decode(const_start_1, const_start_2, data_start):
	const1 = [0] * 14

	for x in range(14):
		const1[x] = file[const_start_1 + x * 4]

	const2 = [0] * 14

	for x in range(14):
		const2[x] = file[const_start_2 + x * 4]

	solver = Solver()

	# Make sure all characters in the key are in range [0-9a-zA-Z/+]
	for i in range(14):
		globals()['c%i' % i] = BitVec('c%i' % i, 32)
		solver.add(
			Or(
				And(globals()['c%i' % i] >= ord('/'), globals()['c%i' % i] <= ord('9')), 
				And(globals()['c%i' % i] >= ord('A'), globals()['c%i' % i] <= ord('Z')), 
				And(globals()['c%i' % i] >= ord('a'), globals()['c%i' % i] <= ord('z')), 
				globals()['c%i' % i] == ord('+')))
		
	# Add restrictions on characters in the key based on the algorithm found in the executables
	solver.add(globals()['c2'] ^ globals()['c0'] ^ globals()['c4'] == const1[0])
	solver.add(globals()['c6'] ^ globals()['c2'] ^ globals()['c4'] == const1[1])
	solver.add(globals()['c8'] ^ globals()['c4'] ^ globals()['c6'] == const1[2])
	solver.add(globals()['c10'] ^ globals()['c6'] ^ globals()['c8'] == const1[3])
	solver.add(globals()['c12'] ^ globals()['c8'] ^ globals()['c10'] == const1[4])
	solver.add(globals()['c1'] ^ globals()['c10'] ^ globals()['c12'] == const1[5])
	solver.add(globals()['c3'] ^ globals()['c12'] ^ globals()['c1'] == const1[6])
	solver.add(globals()['c5'] ^ globals()['c1'] ^ globals()['c3'] == const1[7])
	solver.add(globals()['c7'] ^ globals()['c3'] ^ globals()['c5'] == const1[8])
	solver.add(globals()['c9'] ^ globals()['c5'] ^ globals()['c7'] == const1[9])
	solver.add(globals()['c11'] ^ globals()['c7'] ^ globals()['c9'] == const1[10])
	solver.add(globals()['c13'] ^ globals()['c9'] ^ globals()['c11'] == const1[11])
	solver.add(globals()['c0'] ^ globals()['c11'] ^ globals()['c13'] == const1[12])
	solver.add(globals()['c2'] ^ globals()['c13'] ^ globals()['c0'] == const1[13])

	solver.add(globals()['c2'] ^ globals()['c0'] ^ globals()['c1'] == const2[0])
	solver.add(globals()['c3'] ^ globals()['c1'] ^ globals()['c2'] == const2[1])
	solver.add(globals()['c4'] ^ globals()['c2'] ^ globals()['c3'] == const2[2])
	solver.add(globals()['c5'] ^ globals()['c3'] ^ globals()['c4'] == const2[3])
	solver.add(globals()['c6'] ^ globals()['c4'] ^ globals()['c5'] == const2[4])
	solver.add(globals()['c7'] ^ globals()['c5'] ^ globals()['c6'] == const2[5])
	solver.add(globals()['c8'] ^ globals()['c6'] ^ globals()['c7'] == const2[6])
	solver.add(globals()['c9'] ^ globals()['c7'] ^ globals()['c8'] == const2[7])
	solver.add(globals()['c10'] ^ globals()['c8'] ^ globals()['c9'] == const2[8])
	solver.add(globals()['c11'] ^ globals()['c9'] ^ globals()['c10'] == const2[9])
	solver.add(globals()['c12'] ^ globals()['c10'] ^ globals()['c11'] == const2[10])
	solver.add(globals()['c13'] ^ globals()['c11'] ^ globals()['c12'] == const2[11])
	solver.add(globals()['c0'] ^ globals()['c12'] ^ globals()['c13'] == const2[12])
	solver.add(globals()['c1'] ^ globals()['c13'] ^ globals()['c0'] == const2[13])

	# Find the solution
	result = str(solver.check())
	if result != "sat":
		return False

	modl = solver.model()

	# Collect the key characters from the solution
	res = ""
	for i in range(14):
		obj = globals()['c%i' % i]
		res += chr(modl[obj].as_long())
	print(res)

	# Decrypt the embedded archive and write it out
	x = data_start
	
	outfile = bytearray((len(file) - x) / 8 + 1)
	
	i = 0
	while x < len(file):
		outfile[i] = (file[x] ^ ord(res[i % 13])) & 0xff
		x += 8
		i += 1
	open("surprise","wb").write(outfile[0:i])
	return True
	
# Loop over all levels
while True:

	# Decompress the archive
	file = bytearray(open("surprise","rb").read())
	if file[0:3] != "BZh":
		print "No archive found"
		break
	
	os.system("bzip2 -dc surprise > surprise.decompressed")	
	
	# Make sure it's an executable
	file = bytearray(open("surprise.decompressed","rb").read())
	if file[1:4] != "ELF":
		print "No executable extracted"
		break

	os.system("mv surprise.decompressed surprise.decoded")	

	# Load the executable
	file = bytearray(open("surprise.decoded","rb").read())

	# Because offsets differ try 4 different ones
	if not decode(0x2010, 0x2048, 0x2080):
		if not decode(0x2020, 0x2060, 0x20A0):
			if not decode(0x1008, 0x1040, 0x1078):
				if not decode(0x10133, 0x1016b, 0x101a7):
					print "Unknown encoding"
					break

# Execute the final version of the executable
os.system("./surprise")	
```

The script runs for a while and produces the flag:

```
$ python solve.py 
JCE1aWJApiDO5K

bzip2: surprise: trailing garbage after EOF ignored
25ajMKWS7KW1f9

bzip2: surprise: trailing garbage after EOF ignored
LA9k9yfJGi9u3C

bzip2: surprise: trailing garbage after EOF ignored

...

aD5quOpbgG1Bua
No archive found
inctf{x32_x64_ARM_MAC_powerPC_4rch_maz3_6745}
```

The flag is ```inctf{x32_x64_ARM_MAC_powerPC_4rch_maz3_6745}```.