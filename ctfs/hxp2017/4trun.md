---
layout: page
title: "HXP 2017 Writeup: 4trun"
---

> 4TRUN
> 
> We found this archaic thing in our lab. Could you please investigate its purpose?
>
> Download:
> 
> [4trun.zip]({{ site.baseurl }}/ctfs/hxp2017/4trun/4trun.zip)
> 
> 100 Basepoints + 100 Bonuspoints * min(1, 3/21 Solves) = 114 Points

When run, the executable asks for the flag, and checks it:

```
$ ./4TRUN 
Give flag: test
:(
```

We can open the application in HexRays or Snowman and study the flag checking logic. It consists of the following steps:

- Load the encoded flag data in a 6x6 _encoded flag matrix_
- Read the flag from the input and store it into a 6x6 _flag matrix_
- Update the _flag matrix_ by successively adding values in columns
- Fill _encoding factor matrix_ with constant values ```0``` through ```35```, and then update them with values from vector ```[1,3,3,3,3,7]```
- Multiply _flag matrix_ and _encoding factor matrix_
- Compare _encoded flag matrix_ and _flag matrix_, returning an indicator of whether the flag is valid or not

These steps can be reversed and the flag can be generated from _encoded flag matrix_ and _encoding factor matrix_. Let's put the reverse steps into a script:

```python
import struct, numpy

# extract encoded flag from executable 
enc_flag_data = bytearray(open("4TRUN","rb").read())[0x1180:0x1180+0xc0]

factor = [1,3,3,3,3,7]

enc_flag = numpy.tile(0,(6,6))
enc_factors = numpy.tile(0,(6,6))

# fill the flag matrix and the encoding factors
for i in range(6):
	for j in range(6):
		enc_flag[j,i] = struct.unpack("<I",enc_flag_data[0x20*j + i*4:0x20*j + i*4 + 4])[0]
		enc_factors[j,i] = 6 * j + i
	enc_factors[i,i] += factor[i]

print "Encoded flag:"
print enc_flag

print "Encoding factors:"
print enc_factors
	
# solve the matrix
res = numpy.linalg.solve(enc_factors, enc_flag)

print "Solved matrix:"
print res

# decode the resulting matrix by successively subrtacting values in columns
for i in range(6):
	for j in range(5,0,-1):
		for k in range(j-1,-1,-1):
			res[k,i] -= res[j,i]

print "Flag matrix:"
print res

flag = ""
for i in range(6):
	for j in range(6):
		flag += chr(int(round(res[j,i])))
print "Flag: " + flag
```

Running the script gets us the flag:

```
$ python solve.py 
Encoded flag:
[[ 3510  4337  3131  3500  4180  3476]
 [16140 18737 13815 15158 17061 14488]
 [27510 31814 23403 25718 29043 24670]
 [38904 44915 33165 36464 41034 34660]
 [50265 58013 42729 47003 52824 44809]
 [61890 71592 52558 58126 65153 55548]]
Encoding factors:
[[ 1  1  2  3  4  5]
 [ 6 10  8  9 10 11]
 [12 13 17 15 16 17]
 [18 19 20 24 22 23]
 [24 25 26 27 31 29]
 [30 31 32 33 34 42]]
Solved matrix:
[[ 606.  648.  467.  549.  550.  493.]
 [ 502.  540.  415.  435.  431.  389.]
 [ 382.  423.  309.  321.  379.  337.]
 [ 270.  314.  261.  269.  330.  221.]
 [ 147.  204.  147.  148.  214.  158.]
 [  48.  109.   52.   95.  119.  125.]]
Flag matrix:
[[ 104.  108.   52.  114.  119.  104.]
 [ 120.  117.  106.  114.   52.   52.]
 [ 112.  109.   48.   52.   49.  116.]
 [ 123.  110.  114.  121.  116.   63.]
 [  99.   95.   95.   53.   95.   33.]
 [  48.  109.   52.   95.  119.  125.]]
Flag: hxp{c0lumn_m4j0r_4rr4y5_w41t_wh4t?!}
```


