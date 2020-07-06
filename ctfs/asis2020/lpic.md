---
layout: page
title: "ASIS CTF Quals 2020 Writeup: LPIC-n"
---

> LPIC-n
>
> We did a terrible mistake, someone is preparing for the LPIC exam! We found this command and these files.
> 
> $ ./prog <flag.txt > flag && dd if=flag of=flag.txt conv=notrunc bs=1 skip=1
> 
> Download: [warm-up-re_1308581f1c3960c0a6853383a8aa17ff2e43405c.txz]({{ site.baseurl }}/ctfs/asis2020/lpic/warm-up-re_1308581f1c3960c0a6853383a8aa17ff2e43405c.txz)

We start with a encrypted flag file and a mangled program source. Let's format it a little bit:

```c
#include<stdio.h>

int main(){
	int a=0,b=a;
	long long c[178819],d=8,e=257,f,g,h,i=d-9;
	
	for(;a<178819;){
		c[a++]=d;
	}
	
	for(a*=53;a;a>>=8)
		putchar(a);
	
	if((f=getchar())<0)
		return 0;
	
	for(;(g=getchar())>=0;){

		h=i=g<<8^f;
		g+=f<<8;
		a=e<(512<<a%8|(a<7))||f>256?a:a>6?15:a+1;

		for(;c[i]>-1&&c[i]>>16!=g;)
			i+=i+h<69000?h+1:h-69000;

		h=c[i]<0;
		b|=h*f<<d;

		for(d+=h*(a%8+9);d>15;d-=8)
			putchar(b=b>>8);

		f=h?g-f*256:c[i]%65536L;

		if(a<8*h){
			c[i]=g*65536L|e++;
		}
	}

	b|=f<<d;
	
	for(d+=a%8;d>-1;d-=8)
		putchar(b>>=8);

	return!53;
}
```

Essentially the program reads the source file character-by-character with ```getchar``` and outputs the encoded representation with ```putchar```.

We could go full hog into analyzing the algorithm, but let's look at a preamble being output first:

```c
	for(a*=53;a;a>>=8)
		putchar(a);
```

This looks like a header of some sort that does not depend on the file contents. When we compile and run the program it turns out that the header is ```1F 9D 90```. This looks vaguely familiar and it should - it's a [compressed Tar](https://www.filesignatures.net/index.php?page=search&search=1F9D90&mode=SIG). Use of ```dd``` command strips the first byte of the header off, so when we add it manually we can output the contents:

```sh
$ uncompress -c flag_updated.txt
ASIS{Y3s_asis_L0ves_i0ccc_and_lov3s_obfuscati0ns_and_C_For3vere}
```

Bingo, the flag is ```ASIS{Y3s_asis_L0ves_i0ccc_and_lov3s_obfuscati0ns_and_C_For3vere}```.