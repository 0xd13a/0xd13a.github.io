---
layout: page
title: "RCTF 2017 Writeup: message"
---

> message
> 
> 465 points
>
> Maybe this message is from aliens.
> 
> https://static2017.teamrois.cn/misc_745cfbae1f7e94f0a49e9f81a69d72e4/msg_bc5c27abe80477b13ec5ba03dcaf7cf0.zip
> 

We are given the following text file (split into several lines for clarity):

```
7fffffffffffffffffffffffffffffffbffff0e10419dff07fdc3ffdaeab
6deffdbfff6ffed7f7aef3febfffb7ff1bfbc675931e33c79fadfdebbae7
aeddedb7dafef7dc37df7ef6dbed777beedbedb77b6de24718f260e0e718
79fffffffffffffffffffffffffffffffffffffffffff07f87fc7f9fffff
fffdbfbbfdbfeffffffffebfdffdfff7ffffff871c33e6fe7bffffffd5ae
feed62dcffffffeadf9fb8bb0efffffff56df5db6dbf7ffffffaa0c21e19
e3bffffe07ffffffffff9fffffffffffffffffffffffffffffffffffffff
```

Converting it to binary file and doing a ```file``` and ```binwalk``` on it brings no useful results. However it does look like it has some repeating patterns in it. Let's convert it into a binary string representation with the following script:

```python
print bin(int(open("msg.txt","r").read(),16))[2:].replace("0",".").replace("1","#")
```

The script generates series of ```.```'s and ```#```'s. Let's copy the output into ```Notepad++```, reduce the font to a minimum (press ```Ctrl+-``` repeatedly), and resize the window to see if we can spot something.

The following pattern is revealed:

![flag]({{ site.baseurl }}/ctfs/rctf2017/message/flag.png)

The flag is ```RCTF{ArEciBo_mEsSaGe}```.
