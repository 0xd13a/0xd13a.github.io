---
layout: page
title: "ASIS CTF 2017 Quals Writeup: Wandere bits"
---

> Wandere bits
> 
> 109
>
> I lost my flag's bit under a cherry tree... Can you find it?
>
> [wandere]({{ site.baseurl }}/ctfs/asis2017/wandere-bits/wandere)

The executable ```wandere``` expects a flag passed on command line:

```sh
$ ./wandere
usage : binary.bin flag
```

Let's reverse it in ```Snowman```. After a bunch of permutations on the flag passed on command line, execution comes to this interesting part:

```c
...
        eax23 = fun_400f10(rsp22, 0x4069f8, rsp22, 0x4069f8);
        rsp24 = reinterpret_cast<void**>(reinterpret_cast<uint64_t>(rsp22 - 8) + 8);
        if (v25 != rsp24 + 2) {
            fun_400e40();
            rsp24 = rsp24 - 1 + 1;
        }
        _ZN9BigNumberD1Ev(rsp24 + 10, 0x4069f8, rdx16);
        rsp26 = reinterpret_cast<void*>(rsp24 - 1 + 1);
        if (!eax23) {
            *reinterpret_cast<int32_t*>(&rsi27) = reinterpret_cast<int32_t>("gj, you got the flag :)");
            *reinterpret_cast<int32_t*>(&rsi27 + 4) = 0;
            fun_400ee0(0x608100, 0x406a5d, 23);
            rsp28 = reinterpret_cast<void*>(reinterpret_cast<uint64_t>(rsp26) - 8 + 8);
        } else {
            *reinterpret_cast<int32_t*>(&rsi27) = reinterpret_cast<int32_t>("0ops, try harder plz :(");
            *reinterpret_cast<int32_t*>(&rsi27 + 4) = 0;
            fun_400ee0(0x608100, 0x406a75, 23);
            rsp28 = reinterpret_cast<void*>(reinterpret_cast<uint64_t>(rsp26) - 8 + 8);
        }
...
```

Function ```fun_400f10``` compares the encoded flag to hardcoded value at address ```0x4069f8```:

![encoded-bytes.png]({{ site.baseurl }}/ctfs/asis2017/wandere-bits/encoded-bytes.png)

If the flag matches the success message is output.

Rather than reversing the algorithm let's cheat. The following code does the comparison and outputs the success message:

![code.png]({{ site.baseurl }}/ctfs/asis2017/wandere-bits/code.png)

Let's patch it to simply print out the encoded representation. That way we will be able to bruteforce the flag and check it against encoded representation output by the program. 

The first instruction in the patch will initialize RSI with the address of encoded flag, and the second instruction will set the string output length to ```0x4c``` bytes:

![patched-code.png]({{ site.baseurl }}/ctfs/asis2017/wandere-bits/patched-code.png)

Now we can go through all possible combinations of characters:

```python
import subprocess

goal = "82a386a3b7983198313b363293399232349892369a98323692989a313493913036929a303abf"

pref = "ASIS{"
suff = "}"

flag = pref
for x in range(len(goal)/2 - len(pref) - len(suff)):
	for c in range(20, 128):
		result = subprocess.check_output(['./wandere', flag + chr(c) + "a" * (len(goal)/2 - len(flag) - 2) + suff])[:(len(flag) + 1) * 2]
		if goal.startswith(result):
			flag += chr(c)
			break
			
print flag + suff
```

Running the script gives us the flag - ```ASIS{d2d2791c6a18da9ed19ade28cb09ae05}```:

```sh
$ python solve.py 
ASIS{d2d2791c6a18da9ed19ade28cb09ae05}
```
