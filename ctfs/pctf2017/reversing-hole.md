---
layout: page
title: "PlaidCTF 2017 Writeup: Down the Reversing Hole"
---

> Down the Reversing Hole
> 
> Misc (50 pts)
>
> Don't forget. This is a MISC challenge. 
>
> [Download]({{ site.baseurl }}/ctfs/pctf2017/reversing-hole/reversing-hole.exe) 

Of course I disregarded the warning about the "MISC challenge" and went whole hog into reversing the algorithm... only to discover that, based on the code I saw, bruteforcing the flag would likely take longer than the length of the CTF. :smile:

One of the CTF IRC channel OPs reiterated that this was **not** a reversing challenge. So, back to the drawing board I went... Played with different input values, looked for interesting artifacts inside the binary - nothing seemed to hint at a flag.

Finally, the MS-DOS stub caught my eye, it had a non-standard message - ```This program cannot be run in WIN mode```. The regular message is ```This program cannot be run in DOS mode```. The stub also looked larger than usual.

Running the program in [DOSBox](https://www.dosbox.com/) revealed the reason why:

![screen]({{ site.baseurl }}/ctfs/pctf2017/reversing-hole/screen.png)

The flag is ```PCTF{at_l3a5t_th3r3s_d00m_p0rts_0n_d0s}```.