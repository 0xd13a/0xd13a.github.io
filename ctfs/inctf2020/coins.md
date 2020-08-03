---
layout: page
title: "InCTF 2020 Writeup: Coins"
---

> Coins
> 
> 100
> 
> Toss a coin to your Witcher O' Valley of Plenty O' Valley of Plenty, oh
> 
> nc 34.74.30.191 1337
> 
> Author: III_Wolverine_III

Once we get past the PoW we are given a number of coins that a minting machine makes. Our task is to find the defective coin and to do that we can select ranges of coins whose weights will be xor'ed together and the result returned to us. We get a limited number of attempts so we have to be smart about it, and binary search should be the fastest method of narrowing it down.

xor'ing an even number of equal weights together results in 0, so that will be our indication that all the coins in that range are good. A non-zero number indicates that there is a defective coin there.

If we have 16 coins, we can start by checking coins 0 through 7. If the xor is 0, then the next range to check will be 8 through 11, and so on. A special case is when there are 2 coins in the range, i and i+1, and we cannot tell which one is the bad one. For that case we can make an additional check of i-1 and i (or i+1 and i+2).

Let's put this algorithm in a script:

```python
from pwn import *
import hashlib

r = remote('34.74.30.191', 1337)

# Solve the PoW challenge
challenge = r.recv().split()
hash = challenge[2]
suffix = challenge[0].split('+')[1][:-1]
print suffix, hash
s = iters.mbruteforce(lambda x: hashlib.sha256(x+suffix).hexdigest() == hash, string.letters+string.digits, 4, 'fixed')
r.send(s+"\n")

# Print out the header
print r.recvline().strip()
print r.recvline().strip()
print r.recvline().strip()

print "###"

number = 0

# Check if all coins in the set are of equal weight
def equal_weight(a, b):
	req = "{} {}".format(a, b)
	print req
	r.send(req+"\n")
	ans = r.recvline().strip()
	print ans
	return ans.endswith(" 0")

# Report the bad coin
def report_coin(c):
	req = "! {}".format(c)
	print req
	r.send(req+"\n")
	
# Do a recursive binary search
def binary_search(low, high): 
	print "Searching {} {}".format(low, high)

	if high < low:
		return

	# Only one coin is found - report it
	if low == high:
		report_coin(low)
		return

	# Check for corner cases
	if low+1 == high:
		if high < number:
			if equal_weight(high,high+1):
				report_coin(low)
				return
			else:
				report_coin(high)
				return
		else:
			if equal_weight(low-1,low):
				report_coin(high)
				return
			else:
				report_coin(low)
				return

	mid = (high + low) // 2
	if (mid - low + 1) % 2 != 0:
		mid += 1

	# Search recursively
	if not equal_weight(low,mid):
		binary_search(low, mid) 
	else:
		binary_search(mid+1,high) 

# Loop over all rounds
while True:

	number_line = r.recvline().strip()
	print number_line
	if not number_line.startswith("The number of coins in this batch"):
		break
	print r.recvline().strip()
	number = int(number_line.split()[8])

	print "###"

	binary_search(0, number)
			
	ans = r.recvline().strip()
	print ans
	if not ans.endswith("let's keep going!"):
		break

r.interactive()
```

Running the script gets us the flag:

```
$ python solve.py 
[+] Opening connection to 34.74.30.191 on port 1337: Done
1q6wygjzbL5bWfww 9b311d4d4f7ca66ffaeab307a1bac15f2d434a1c21e1f0810570bde025dd0e46
[+] MBruteforcing: Found key: "XaHy"
Give me XXXX:
There exists a coin minting machine at Bi0S which is known for its 
extermely fast minting process. However out of every batch (N coins) 
it produces one coin is faulty (have a different weight compared to 
the other N-1 coins). You can get information of the xor of the weight 
of the coins from index i to index j (both included) by communicating 
with the minting machine. Find the faulty coin (coin with different 
weight) with minimum number of queries, as the minting machine has 
better things to do than answer your questions. Multiple batches are 
produced by the minting machine and it is gaurenteed that in each 
batch there is only one defective coin. Your query should be in the 
format "i j" (without the quotes) where both i and j should lie in 
the range [0, N). You can report the correct position (Of course after 
solving it) in the format "! index" (without the quotes) where index 
lies in the range [0, N). If you correctly identify the faulty coin 
for a batch, you will continue to the next batch. If a query is given 
in the wrong format or give a wrong answer you will be rejected.

###
The number of coins in this batch are 12
Go ahead, ask some queries
###
Searching 0 12
0 7
The xor of coin weights from index l to r is 23
Searching 0 7
0 3
The xor of coin weights from index l to r is 0
Searching 4 7
4 5
The xor of coin weights from index l to r is 0
Searching 6 7
7 8
The xor of coin weights from index l to r is 23
! 7
Correctly identified the faulty coin in this batch, let's keep going!
The number of coins in this batch are 473847
Go ahead, ask some queries
###
...
The xor of coin weights from index l to r is 588436
Searching 2792 2793
2793 2794
The xor of coin weights from index l to r is 588436
! 2793
Correctly identified the faulty coin in this batch, let's keep going!
Ahh the winner!! you have earned your reward, the holy piece of text that will lead you to your destination
[*] Switching to interactive mode

b'inctf{1f_y0u_c4n_dr3am_y0u_c4n_s34rch_1n_logn}'
[*] Got EOF while reading in interactive
```


The flag is ```inctf{1f_y0u_c4n_dr3am_y0u_c4n_s34rch_1n_logn}```.