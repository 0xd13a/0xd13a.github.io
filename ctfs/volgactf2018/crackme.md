---
layout: page
title: "VolgaCTF 2018 Quals Writeup: CrackMe"
---

> Just do it.
>
> [download]({{ site.baseurl }}/ctfs/volgactf2018/crackme/crackme.zip)

We are given an executable and an enecrypted text file, and must find a way to decrypt it. The executable is a .Net assembly:

```
$ file CrackMe.exe
CrackMe.exe: PE32 executable (console) Intel 80386 Mono/.Net assembly, for MS Windows
```

Let's open it in ```ILSpy```, which produces a handy decompilation to the original source. Based on the source, the application essentially encrypts and decrypts files and the encryption key must be provided on command line.

When the key is read by the application an MD5 is calculated for it and then it is combined with a constant (```appSettings.DefaultKey```) and mangled:

```csharp
...

public string UserPassword
{
	set
	{
		this.KeyLength = 16;
		byte[] userKey = MD5.Create().ComputeHash(Encoding.UTF8.GetBytes(value));
		this.UserKey = this.CombineKeys(userKey);
	}
}

...

private byte[] CombineKeys(byte[] UserKey)
{
	AppSettings appSettings = new AppSettings();
	byte[] expr_16 = Encoding.UTF8.GetBytes(appSettings.DefaultKey);
	long num = BitConverter.ToInt64(expr_16, 0);
	long num2 = BitConverter.ToInt64(expr_16, 8);
	long num3 = BitConverter.ToInt64(UserKey, 0);
	long num4 = BitConverter.ToInt64(UserKey, 8);
	long num5 = num ^ num3;
	long num6 = num2 ^ num4;
	long num7 = (~num & num3) | (~num3 & num);
	long num8 = (~num2 & num4) | (~num4 & num2);
	int num9 = BitConverter.ToInt32(BitConverter.GetBytes(num5), 0);
	int num10 = BitConverter.ToInt32(BitConverter.GetBytes(num5), 4);
	int num11 = BitConverter.ToInt32(BitConverter.GetBytes(num6), 0);
	int num12 = BitConverter.ToInt32(BitConverter.GetBytes(num6), 4);
	num9 >>= 2;
	num10 >>= 2;
	num9 <<= 1;
	num10 <<= 1;
	num12 = num9 << 1;
	num11 >>= 2;
	num11 = num9 << 1;
	num12 >>= 2;
	if (~(num9 & num12) == (~num9 | ~num12))
	{
		num11 = num10;
		if (~(~num7) != num5 && ~(~num8) != num6)
		{
			num10 = num12;
		}
		else
		{
			num12 = num10;
		}
		num9 = ~num12;
	}
	else
	{
		num11 = num9;
		if (~(~num7) == num5 && ~(~num8) == num6)
		{
			num10 = num12;
		}
		else
		{
			num12 = num10;
		}
		num9 = ~num10;
	}
	num9 = ~num9;
	byte[] bytes = BitConverter.GetBytes(num9);
	byte[] bytes2 = BitConverter.GetBytes(num10);
	byte[] bytes3 = BitConverter.GetBytes(num11);
	byte[] bytes4 = BitConverter.GetBytes(num12);
	byte[] array = new byte[16];
	for (int i = 0; i < 4; i++)
	{
		array[i] = bytes[i];
		array[i + 4] = bytes2[i];
		array[i + 8] = bytes3[i];
		array[i + 12] = bytes4[i];
	}
	return array;
}

...
``` 

Bruteforcing of a 16-byte key will take forever, but let's take a closer look at the ```CombineKeys``` algorithm. A few things stand out:

* The branch on line ```39``` will always be taken because by De Morgan's theorem ```(AA)' == A' + A'```

* ```num7``` essentially equals to ```num5``` because by definition of the XOR operator ```A'B + AB' == A ^ B```. Same goes for ```num8``` and ```num6```. This means that branch on line ```46``` will always be taken.

Because of these considerations lines ```39 - 65``` are essentially reduced to the following:

```		
num11 = num10;
num12 = num10;
num9 = num10;
```

This means that the key is essentially a repetition of the same 4-byte sequence and we can bruteforce that pretty quickly with the following script:

```py
from Crypto.Cipher import AES

data = open("CrackMe.txt","rb").read()
iv = data[0:16]
data = data[16:]

x = 0
while x < 0x100000000:

	key = struct.pack(">I", x)
	aes = AES.new(key*4, AES.MODE_CBC, iv)
	dec = aes.decrypt(data)

	if "Volga" in dec:
		print dec
		break

	x += 1
```

The flag is ```VolgaCTF{my_little_cat_solved_this_much_faster}```.