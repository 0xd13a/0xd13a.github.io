---
layout: page
title: "HackIT CTF 2017 Writeup: foren100"
---

> USB ducker
>
> foren100
>
> Description: This file was captured from one of the computers at the Internet cafe. We think that the hacker was using this computer at that time. Try to get his secret documents. ( flag format is flag{...} )
>
> Webpage: [task.pcap]({{ site.baseurl }}/ctfs/hackit2017/foren100/task.pcap)

The challenge has an associated PCAP file that we open in Wireshark. It turns out to be a USB communications capture for the Apple Keyboard:

![img1.png]({{ site.baseurl }}/ctfs/hackit2017/foren100/img1.png)

The file is pretty small and most of it are the USB_INTERRUPT events that encode key presses. The suspicion is that the flag is encoded in them.

Examination of the [USB specification](http://www.usb.org/developers/hidpage/Hut1_12v2.pdf), and especially the table on page ```53``` shows us how to decode the individual keys - we need to extract data from key press events and then parse it to extract the character that was entered and the state of the Shift key.

First let's massage the data, extracting just the chunks with the codes: 

```sh
$ tshark -r task.pcap -T fields -e usb.capdata | grep -E "^.{23}$" | grep -v 00:00:00:00:00:00:00:00 > data.txt
```

The data will now look like this:

```
...
02:00:00:00:00:00:00:00
00:00:52:00:00:00:00:00
02:00:00:00:00:00:00:00
02:00:2f:00:00:00:00:00
02:00:00:00:00:00:00:00
00:00:1a:00:00:00:00:00
02:00:00:00:00:00:00:00
02:00:21:00:00:00:00:00
...
```

The Shift key is encoded as the value of ```02``` at position ```0``` in the data, and the key code is the value at position ```2```. Also note that there are lots of keys with codes ```0x52```, ```0x51```, and ```0x28``` - which are Up Arrow, Down Arrrow, and Enter, respectively. It looks like someone was writing the text in a text editor, typing characters and moving about.

Let's put together a script that will parse the data, simulating editing of text in the editor (in a very primitive way):

```python

usb_codes = {
	0x04:"aA", 0x05:"bB", 0x06:"cC", 0x07:"dD", 0x08:"eE", 0x09:"fF",
	0x0A:"gG", 0x0B:"hH", 0x0C:"iI", 0x0D:"jJ", 0x0E:"kK", 0x0F:"lL",
	0x10:"mM", 0x11:"nN", 0x12:"oO", 0x13:"pP", 0x14:"qQ", 0x15:"rR",
	0x16:"sS", 0x17:"tT", 0x18:"uU", 0x19:"vV", 0x1A:"wW", 0x1B:"xX",
	0x1C:"yY", 0x1D:"zZ", 0x1E:"1!", 0x1F:"2@", 0x20:"3#", 0x21:"4$",
	0x22:"5%", 0x23:"6^", 0x24:"7&", 0x25:"8*", 0x26:"9(", 0x27:"0)",
	0x2C:"  ", 0x2D:"-_", 0x2E:"=+", 0x2F:"[{", 0x30:"]}",  0x32:"#~",
	0x33:";:", 0x34:"'\"",  0x36:",<",  0x37:".>"
	}

lines = ["","","","",""]
		
pos = 0

for x in open("data.txt","r").readlines():
	code = int(x[6:8],16)
	
	if code == 0:
		continue
	# newline or down arrow - move down
	if code == 0x51 or code == 0x28:
		pos += 1
		continue
	# up arrow - move up
	if code == 0x52:
		pos -= 1
		continue

	# select the character based on the Shift key
	if int(x[0:2],16) == 2:
		lines[pos] += usb_codes[code][1]
	else:
		lines[pos] += usb_codes[code][0]
		
	
for x in lines:
	print x
```

When we run the script we get the flag

```
$ python solve.py

w{w$ju},'pt]=j%;9+ps&#,i
k#>bn$:6pjim0{u'h;fks!s-
flag{k3yb0ard_sn4ke_2.0}
b[[e[fu~7d[=>*(0]'$1c$ce
3'ci.[%=%&k(lc*2y4!}%qz3
```

The flag is ```flag{k3yb0ard_sn4ke_2.0}```.