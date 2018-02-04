---
layout: page
title: "Sharif CTF 2018 Writeup: Client01"
---

> 75
>
> Client01
> 
> Attached file is the homepage of the client01. He knows the flag.
>
> Download

Search through the client files brings up the following reference in ```client01/.thunderbird/5bd7jhog.default/ImapMail/imap.gmail.com/INBOX```:

```
<div dir="ltr"><a style="color:rgb(0,187,0);text-decoration:none" 
href="http://www.filehosting.org/file/details/720884/file" target="_blank">
http://www.filehosting.org/<wbr>file/details/720884/file</a></div>
```

The downloaded file is a damaged PNG image (letter ```P``` is missing from the ```PNG``` header). We correct it and enough of the flag is revealed to finish the challenge:

![flag]({{ site.baseurl }}/ctfs/sharif2018/client01/flag.png)

The flag is ```SharifCTF{43215f0c5e005d4e557ddfe3f2e57df0}```.