---
layout: page
title: "HXP CTF 2018 Writeup: cheatquest of hxpschr 2"
---

> Paintings
> 
> Don't you want to admire the beautiful pictures I created while playing your favourite game for Gameboy Advance? I tried to take take a photo using my camera but it is freaking impossible to see the contents of the screen.
>
> For your convenience I therefore developed my own screen capturing software. Now decode those pictures!
>
> Note: Please convert the flag to lowercase before submitting it.
>
> Download: cheatquest of hxpschr 2-0e854089ec64f6d8.tar.xz
>
> 200 Basepoints + 100 Bonuspoints * min(1, 3/9 Solves) = 233 Points


Based on the pictures and the provided pcap we are dealing with a USB communication between a client application and Game Boy Advance. There are screen captures of the device screen somehow encoded in the pcap, and supposedly the flag is in those images.

Pcap analysis in Wireshark shows a number of USB packet types, but the ones that realistically contain the data that we need are likely the URB_INTERRUPT packets:

![]({{ site.baseurl }}/ctfs/hxp2018/cheatquest2/urb.png)

According to Game Boy Advance [Wikipedia page](https://en.wikipedia.org/wiki/Game_Boy_Advance) the screen resolution is ```240x160```, which is ```38400``` pixels, so the amount of data for the single image should be sizeable. Different packet sets in the capture are marked by different  "tags" in the data sections - ```43425720```, ```4342571c```, and ```4342571d```; the former looks like the one marking the large data volumes that we need. The client application requests different memory area dumps - ```4``` through ```7```, and the one tagged with ```6``` looks like it contains the data that we are looking for:

![]({{ site.baseurl }}/ctfs/hxp2018/cheatquest2/type6.png)


There are several groups of ```384``` blocks of type ```6``` (each group likely encoding one image), which with ```256``` data bytes in each block gives us ```98304``` bytes. Game Boy encodes some of the images with 2 bytes per pixel, which corresponds to ```48k``` pixels - close enough to ```38k``` that we need (because it's a memory dump it does not have to be an exact amount).

We can now extract the right data portions and build images. Because the Python library that we are going to use for analysis does not work with .pcapng files let's first convert the file to .pcap format in Wireshark. Once that is done we can use the following script:

```python
import binascii
import dpkt
from PIL import Image, ImageDraw

# parse pcap file
pcap = dpkt.pcap.Reader(open('paintings.pcap', 'rb'))

data = []
print "walking pcap"
for ts, buf in pcap:
	
	# only keep URB_INTERRUPT packets (0x01) of specific size, discard the rest
	if ord(buf[9]) != 1 or len(buf) != 72:
		continue
		
	data.append(buf[0x40:])

print "done reading pcap"

in_image = False
image_no = 0
img_data = ''
while len(data) > 0:
	rectype = binascii.hexlify(data.pop(0))
	# discard this packet and another one
	if rectype == '4342572000000000':
		data.pop(0)
		continue
	# discard this packet and another three
	if rectype == '4342571c00000000':
		data.pop(0)
		data.pop(0)
		data.pop(0)
		continue
	if rectype == '4342571d00000000':
		data.pop(0) # discard
		
		type = data.pop(0) # get data block type
		data.pop(0) # discard
		s = ''
		for x in range(32):
			data.pop(0) # discard
			s += binascii.hexlify(data.pop(0))
			
		# this is the type that we need
		if ord(type[3]) == 6:
			if in_image:
				# collect data
				img_data += s
			else:
				
				in_image = True
				print "image start" 			
				img_data = s
		else:
			if in_image:
				
				# done with image data
				in_image = False
				
				i_data = binascii.unhexlify(img_data)
				
				img = Image.new('RGB', (240, 160))
				pixels = img.load()
				
				# put image data into the pixels
				for i1 in range(7):
					for j in range(160):
						for i2 in range(32):
							pos = i1*32*160 + j*32 + i2
							
							# take 16-bit value
							p = ord(i_data[pos*2]) * 0x100 + ord(i_data[pos*2+1])
							
							# decode 32-bit value into RGB (https://stackoverflow.com/a/38557870)
							pixels[i1*32+i2,j] = ((p & 0xF800) >> 11,(p & 0x07E0) >> 5,p & 0x001F)
				
				img.save('%d.png' % (image_no))
				print "image end"
				image_no += 1
		continue
		
```

Once the images were decoded they looked rather cryptic, and it took a long while to try different combinations of decoding order - line first, column first, chunks of 16 pixels column-wise, etc... In the end encoding data in chunks of 32 pixels column by column brought up an interesting artifact in image ```3```:

![]({{ site.baseurl }}/ctfs/hxp2018/cheatquest2/background.png)

These look like letters ```H``` and ```X```! Analyzing other images revealed the rest:

![]({{ site.baseurl }}/ctfs/hxp2018/cheatquest2/flag.png)

Success! The flag is ```hxp{ashesforash}```.

