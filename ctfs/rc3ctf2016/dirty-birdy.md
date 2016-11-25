---
layout: page
title: RC3 CTF 2016 Writeup: Dirty Birdy
---

> 400 points
> 
> Description: We had an employee that was up to no good. Our SIEM caught him uploading files to a website from our file server but we canceled the transmission. We currently have an image of home directory that we store on our server. Take a look for yourself on what he stole.
> 
> Download Link: https://drive.google.com/file/d/0Bw7N3lAmY5PCUWExQUJVZGVySXc/view?usp=sharing

The file for this challenge is a CD-ROM image:

```
root@kali:~/rc3# file dtrump.img 
dtrump.img: ISO 9660 CD-ROM filesystem data 'CDROM'
```

Let's mount it:

```
root@kali:~/rc3# mkdir -p /mnt/dtrump
root@kali:~/rc3# mount -o loop dtrump.img /mnt/dtrump
mount: /dev/loop1 is write-protected, mounting read-only
root@kali:~/rc3# ls /mnt/dtrump
Desktop    Downloads         Music     Public    secretfiles  Videos
Documents  examples.desktop  Pictures  rr_moved  Templates
```

Folder ```secretfiles``` is obviously interesting:

```
root@kali:~/rc3# cat /mnt/dtrump/secretfiles/document.txt 
passowrd123
root@kali:~/rc3# cat /mnt/dtrump/secretfiles/README.md 
# supersecret
root@kali:~/rc3# file /mnt/dtrump/secretfiles/Workbook1.xlsx.gpg 
/mnt/dtrump/secretfiles/Workbook1.xlsx.gpg: PGP RSA encrypted session key - keyid: 1246B951 2DB12CE2 RSA (Encrypt or Sign) 1024b .
```

Based on ```file``` output and the extension, the Excel Workbook is encrypted with [gpg](https://www.gnupg.org/gph/de/manual/r1023.html). Luckily the folder with original encryption key is available on the image (```.gnupg```). Let's copy it to out home folder and decrypt the Excel file:

```
root@kali:~/rc3# gpg -o Workbook1.xlsx -d /mnt/dtrump/secretfiles/Workbook1.xlsx.gpg
gpg: encrypted with 1024-bit RSA key, ID 51B94612E22CB12D, created 2016-11-18
      "ThugG (lolz) <nope@gmail.com>"
```

When we open the decrypted file in Excel we are asked for the password. ```passowrd123``` from ```document.txt``` does not work, but ```password123``` does (this may be intentional, or accidental). When we examine the worksheets closely we see the flag ```RC3-2016-SNEAKY21``` in ```Sheet2```:

![Flag]({{ site.baseurl }}/ctfs/rc3ctf2016/dirty-birdy/flag.png)
