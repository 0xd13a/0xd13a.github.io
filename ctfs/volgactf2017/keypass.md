---
layout: page
title: "VolgaCTF 2017 Quals Writeup: KeyPass"
---

> KeyPass
> 
> 100
>
> For reasons unknown an amature cryptographer wrote an application to generate "strong encryption keys". One of these keys was used to encrypt a tar archive with the flag. They used openssl command line util with -aes-128-cbc. Could you please get the flag? It shouldn't take much time...
>
> [flag.zip.enc]({{ site.baseurl }}/ctfs/volgactf2017/keypass/flag.zip.enc) 
> 
> [keypass]({{ site.baseurl }}/ctfs/volgactf2017/keypass/keypass)

When we run the application with a parameter we get a 17-byte encryption key back:

```
$ ./keypass 1
.EjN$4f4%+r5SrN$Z
```

Let's reverse it in [Snowman](https://derevenets.com/). The main work seems to happen in function ```fun_4004a0```. The algorithm first calculates the one-byte checksum of the string passed in as a parameter. It is done through a series of XORs:

```c
while (rdi11 != rcx12) {
    ++rdi11;
    rdx13 = rdx13 ^ reinterpret_cast<uint64_t>(static_cast<int64_t>(*rdi11));
}
```

After the checksum is calculated it is used in another algorithm that actually generates the key. The algorithm multiplies the checksum by ```0x40f7``` and adds it to ```0x7cc8b```. The result ```mod 82``` is then used as a pointer into an embedded character translation table (```2FuMlX%3kBJ:.N*epqA0Lh=En/diT1cwyaz$7SH,OoP;rUsWv4g\Z<tx(8mf>-#I?bDYC+RQ!K5jV69&)G```).

Once we have the key we can use it to decrypt the flag. Because the 1-byte checksum is used to calculate the key we can bruteforce through 256 combinations quickly. Let's put all this knowledge into a script:

```python
import os

def gen_checksum(s):
	sum = 0
	for x in s:
		sum ^= ord(x)
	return sum
	
def gen_key(checksum):
	x = checksum
	key = ""
	
	for i in range(17):
		x = 0x40f7 * (x % 0x7fffffff) + 0x7cc8b
		key += table[x % 0x7fffffff % 82]
		
	return key
		
table = open("keypass","rb").read()[0xa40:0xa40+82]
		
assert gen_key(gen_checksum("1")) == ".EjN$4f4%+r5SrN$Z"

ciphertext = open("flag.zip.enc","rb").read()

for x in range (0x100):
	
	if 0 == os.system("openssl aes-128-cbc -d -in flag.zip.enc -out flag.zip -pass 'pass:%s'" % gen_key(x)):
		os.system("unzip -c flag.zip")
		break
```

When we run it we get the flag ```VolgaCTF{L0ve_a11_trust_@_few_d0_not_reinvent_the_wh33l}```:

```sh
$ python solve.py 2>/dev/null
Archive:  flag.zip
 extracting: flag.txt                
VolgaCTF{L0ve_a11_trust_@_few_d0_not_reinvent_the_wh33l}
```