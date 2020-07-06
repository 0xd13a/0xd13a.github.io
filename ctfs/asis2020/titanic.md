---
layout: page
title: "ASIS CTF Quals 2020 Writeup: Titanic"
---

> Titanic
> 
> "... Our ship got caught in a storm and sank. I was one of the few who survived. I was treading water, shocked and disappointed. After a while, I tried swimming toward a shadow which seemed to be the nearest island. ..."
> 
> nc 76.74.178.201 8002

This is a PPC challenge protected by a PoW. Once we pass that we are greeted with a task description:

```
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+ welcome to JPS challenge, its about just printable strings! the number  +
+ n = 114800724110444 gets converted to the printable `hi all', in each   +
+ round find the suitable integer with given property caring about the    +
+ timeout of the submit of solution! all printable = string.printable  :) +
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
| whats the nearest number to 1537367419571623870752 that gets converted to the printable string?
```

So when the number is converted to hex, and then to a string, all characters that are not printable have to be replaced with printable in such a way that the resulting number is the closest possible to the original.

In Python ```string.printable``` contains characters from the following ranges: ```[0x09,0x0d]``` and ```[0x20,0x7e]```.

Analyzing the problem took me on a bunch of detours but essentially it comes down to correctly replacing the ***1st nonprintable character on the left***.

Suppose the number we have to fix is ```0x30104577```. Character ```0x10``` is nonprintable, and the closest printable one is ```0x0d```. Since are going to a smaller number the rest of the characters would have to have largest possible printable values, so the number becomes ```0x300d7e7e```. 

Let's do another example - ```0x501f3132```. Character ```0x1f``` is nonprintable, and the closest printable one is ```0x20```. Since are going to a larger number the rest of the characters would have to have smallest possible printable values, so the number becomes ```0x50200909```. 

Special care will have to be taken when values cross the ```0``` threshold. To adjust for that we would have to either increase or decrease by ```1``` the number before the one we are fixing.

Let's put this logic into a Python script. It can be a bit better optimized, but this will do:

```python
from pwn import *
import hashlib
import binascii
import string

r = remote('76.74.178.201', 8002)

# Receive and solve the PoW challenge
challenge = r.recv().split()
algo = challenge[8].split("(")[0]
suff = challenge[10]
size = int(challenge[14])
print algo, suff, size
s = iters.mbruteforce(lambda x: getattr(hashlib, algo)(x).hexdigest().endswith(suff), string.letters+string.digits, size, 'fixed')
r.send(s+"\n")

# Print the assignment
print r.recvline().strip()
print r.recvline().strip()
print r.recvline().strip()
print r.recvline().strip()
print r.recvline().strip()
print r.recvline().strip()

while True:

	msg = r.recvline().strip()
	print msg
	
	# Parse the number
	msg_parts = msg.split()
	if msg_parts[1] == "whats":
		num = int(msg_parts[6])
	else:
		break
	
	# Convert to hex
	h = hex(num)[2:]
	if h.endswith("L"):
		h = h[:-1]
	if len(h) % 2 == 1:
		h = '0'+h

	b = bytearray.fromhex(h)
	b_low = bytearray()
	b_high = bytearray()
	
	nearest_num = num

	# Go through the string
	for x in range(len(b)):
		c = b[x]
		
		# Find the first nonprintable character
		if not (chr(c) in string.printable): 
			
			# Find closest lower and higher printable characters
			low = c
			low_threshold_crossed = False
			while not (chr(low) in string.printable):
				low = (low-1) & 0xFF
				if low == 0:
					low_threshold_crossed = True
				
			high = c
			high_threshold_crossed = False
			while not (chr(high) in string.printable):
				high = (high+1) & 0xFF
				if high == 0:
					high_threshold_crossed = True

			b_low[:] = b[:]
			b_high[:] = b[:]

			# Build lower and higher numbers 
			b_low[x] = low
			if low_threshold_crossed and x != 0:
				b_low[x-1] -= 1
			for i in range(x+1,len(b_low)):
				b_low[i] = 0x7e
				
			low_num = int(binascii.hexlify(b_low),16)

			b_high[x] = high
			if high_threshold_crossed and x != 0:
				b_high[x-1] += 1
			for i in range(x+1,len(b_high)):
				b_high[i] = 0x9
					
			high_num = int(binascii.hexlify(b_high),16)
			
			# Decide which number is closer to the original
			if abs(num - low_num) > abs(num - high_num):
				nearest_num = high_num
			else:
				nearest_num = low_num
			print nearest_num
			break
	
	# Send the answer
	r.send(str(nearest_num)+"\n")
	
	msg = r.recvline().strip()
	print msg
	
	
	# Exit if we see the flag
	if "flag" in msg:
		break
	
r.interactive()
```

Running the script gets us the flag:

```
$ python solve_final.py 
[+] Opening connection to 76.74.178.201 on port 8002: Done
md5 3d6f5e 27
[+] MBruteforcing: Found key: "aaaaaaaaaaaaaaaaaaaaaaaxQqB"
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+ welcome to JPS challenge, its about just printable strings! the number  +
+ n = 114800724110444 gets converted to the printable `hi all', in each   +
+ round find the suitable integer with given property caring about the    +
+ timeout of the submit of solution! all printable = string.printable  :) +
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
| whats the nearest number to 61895207845396123731595648074623445905448898599139552975792268813653270992177034837318006329338741700 that gets converted to the printable string?
61895207845396123731595648074623445905448898599139552975792268813653270992177034837318006329410390281
| Correct, pass the next level :)
| whats the nearest number to 423703516379457305862443932870018303927592410127 that gets converted to the printable string?
423702877218628465373942340875780940618983702142
| Correct, pass the next level :)
...
| whats the nearest number to 374735388155597834881026 that gets converted to the printable string?
374735388155597736017534
+ Congratz! You got the flag: ASIS{jus7_simpl3_and_w4rmuP__PPC__ch41LEn93}
[*] Switching to interactive mode
[*] Got EOF while reading in interactive
$  
```

The flag is ```ASIS{jus7_simpl3_and_w4rmuP__PPC__ch41LEn93}```.