---
layout: page
title: "SECCON 2017 Online CTF Writeup: Qubic Rube"
---

> Qubic Rube
>
> 300 points
> 
> Please continue to solve Rubic's Cube and read QR code.
>
> http://qubicrube.pwn.seccon.jp:33654/

One of the reasons I love CTFs is because they force me to learn new technologies quickly. This challenge was awesome because I learned image manipulation and QR code scanning in Python - something I wanted to try for a while.

When we open the referenced site we are shown a spinning Rubics Cube with sides containing QR codes:

![image1.png]({{ site.baseurl }}/ctfs/seccon2017/qubic-rube/image1.png)

The images can be loaded directly and analyzed. As we [decode](https://zxing.org/w/decode.jspx) the codes we see that one of them contains a reference to the next page in sequence (50 of them total):

![image2.png]({{ site.baseurl }}/ctfs/seccon2017/qubic-rube/image2.png)

However, as we progress from page to page the images get more and more mangled:

![image3.png]({{ site.baseurl }}/ctfs/seccon2017/qubic-rube/image3.png)

It's clear that we have to reconstruct proper sides from separate pieces. Here is the sequence of steps that we have to follow:

- Load all images on a page
- Split each in 9 pieces (3x3)
- For each piece determine the color side it belongs to
- "Normalize" pieces by rotating corner pieces to be in the top-left position, and sides to be in the top-middle position
- Join pieces for each side in all possible combinations to see if we can extract the text
- If the text is a flag - show it
- If the text is the reference to the next page - follow it and start again

Let's put this algorithm into a script:

```python
import qrtools
import urllib2
from PIL import Image
import os

SIZE = 82
TEST_IMG = "test.png"

color_map = {}

color_map[(196, 30, 58)] = 0
color_map[(255, 88, 0)] = 1
color_map[(255, 255, 255)] = 2
color_map[(0, 81, 186)] = 3
color_map[(0, 158, 96)] = 4
color_map[(255, 213, 0)] = 5


permutations = [[1,2,3,4], [1,2,4,3], [1,3,2,4], [1,3,4,2], [1,4,2,3], [1,4,3,2],
	[2,1,3,4], [2,1,4,3], [2,3,1,4], [2,3,4,1], [2,4,1,3], [2,4,3,1],
	[3,1,2,4], [3,1,4,2], [3,2,1,4], [3,2,4,1], [3,4,1,2], [3,4,2,1], 
	[4,1,2,3], [4,1,3,2], [4,2,1,3], [4,2,3,1], [4,3,1,2], [4,3,2,1]
]

def process(file, center, corners, sides):
	orig_img = Image.open(file)
	
	# cut out 9 pieces
	for x in range(3):
		for y in range(3):
			img = orig_img.copy() 
			img = img.crop((x*SIZE, y*SIZE, x*SIZE+SIZE, y*SIZE+SIZE))
			colors = img.getcolors(256) #put a higher value if there are many colors in your image

			# determine piece color
			for c in colors:				
				if c[1] in color_map:
					colorid = color_map[c[1]]
					
			# normalize by rotation
			if x == 0 and y > 0: img = img.rotate(270)
			if x == 2 and y < 2: img = img.rotate(90)
			if x > 0 and y == 2: img = img.rotate(180)
			
			# store piece in proper bucket
			if x == 1 and y == 1: center[colorid] = img
			else:
				if x == 1 or y == 1: sides[colorid].append(img)
				else: corners[colorid].append(img)
			
def combine(center, corners, sides):
	global permutations

	# recombine pieces all possible ways
	for colorid in range(6):
		for corn in range(len(permutations)):
			for side in range(len(permutations)):
				img = Image.new("RGB",(SIZE*3,SIZE*3))
				
				img.paste(center[colorid],(SIZE,SIZE))
				
				# paste and rotate corners
				for x in range(4):
					pos = permutations[corn][x]
					if pos == 1: img.paste(corners[colorid][x],(0,0))
					if pos == 2: img.paste(corners[colorid][x].rotate(-90),(SIZE*2,0))
					if pos == 3: img.paste(corners[colorid][x].rotate(-180),(SIZE*2,SIZE*2))
					if pos == 4: img.paste(corners[colorid][x].rotate(-270),(0,SIZE*2))
					
				# paste and rotate sides
				for x in range(4):
					pos = permutations[side][x]
					if pos == 1: img.paste(sides[colorid][x],(SIZE,0))
					if pos == 2: img.paste(sides[colorid][x].rotate(-90),(SIZE*2,SIZE))
					if pos == 3: img.paste(sides[colorid][x].rotate(-180),(SIZE,SIZE*2))
					if pos == 4: img.paste(sides[colorid][x].rotate(-270),(0,SIZE))
				
				img.save(TEST_IMG)
				
				qr = qrtools.QR()
				qr.decode(TEST_IMG)
				
				os.remove(TEST_IMG)
				
				# see if we found the link to the next page
				if "seccon.jp" in qr.data:
					return qr.data[qr.data.rfind("/")+1:]
					
				# print the flag if found
				if "SECCON" in qr.data:
					print qr.data
	return None
			
# starting image prefix
pref = "01000000000000000000"

while True:
	print "---"
	
	corners = []
	sides = []
	center = []
	
	for x in range(6):
		corners.append([])
		sides.append([])
		center.append(None)
		
	# download all sides
	for x in "RLUDFB":
		file = "%s_%s.png" % (pref,x)
		
		open(file,"wb").write(urllib2.urlopen('http://qubicrube.pwn.seccon.jp:33654/images/' + file).read())
		process(file, center, corners, sides)
		
	# find the next link or the flag
	pref = combine(center, corners, sides)
	
	if pref == None:
		print "Not found"
		break
	else:
		print "Found " + pref
```


After running for a while the script gets us the answer:

```
...
Found 4882153b757d0af86d97
---
Found 49d06dfaeaefaa612e72
---
SECCON 2017 Online CTF                                   
SECCON 2017 Online CTF                                   
SECCON 2017 Online CTF                                   
SECCON 2017 Online CTF                                   
SECCON 2017 Online CTF                                   
Found 504ded069e4db4e3bef9
---
SECCON{Thanks to Denso Wave for inventing the QR code}   
SECCON{Thanks to Denso Wave for inventing the QR code}   
SECCON{Thanks to Denso Wave for inventing the QR code}   
SECCON{Thanks to Denso Wave for inventing the QR code}   
SECCON 2017 Online CTF                                   
SECCON 2017 Online CTF                                   
SECCON 2017 Online CTF                                   
SECCON 2017 Online CTF                                   
SECCON 2017 Online CTF                                   
Not found
```

The flag is ```SECCON{Thanks to Denso Wave for inventing the QR code}```.