---
layout: page
title: "RCTF 2017 Writeup: easyre"
---

> easyre
> 
> 153 points
> 
> Please submit the flag like RCTF{flag}.
> 
> [Download attachment]({{ site.baseurl }}/ctfs/rctf2017/easyre/easyre.zip)

We are given a Linux application, that asks for input and exits:

```sh
$ ./easy_re

OMG!!!! I forgot kid's id
Ready to exit     
AAAA
```

After reversing the application in ```Snowman``` we see the rough outline:

```c
...
pipe(esp1 + 6);
eax3 = fork(esp1 + 6);
esp4 = reinterpret_cast<int32_t*>(esp1 - 1 + 1 - 1 + 1);
if (!eax3) {
    puts("\nOMG!!!! I forgot kid's id", v5, v6);
    write(v7, "69800876143568214356928753", 29);
    puts("Ready to exit     ", "69800876143568214356928753", 29);
    exit(0, "69800876143568214356928753", 29);
    esp4 = esp4 - 1 + 1 - 1 + 1 - 1 + 1 - 1 + 1;
}
read(v8, reinterpret_cast<uint32_t>(esp4) + 46, 29);
esp9 = reinterpret_cast<void*>(esp4 - 1 + 1);
__isoc99_scanf("%d", reinterpret_cast<uint32_t>(esp9) + 36, 29);
esp10 = reinterpret_cast<void*>(reinterpret_cast<uint32_t>(esp9) - 4 + 4);
if (v11 == eax3) {
    eax12 = g80485f7;
    if ((eax12 & 0xff) == 0xcc) {
        puts(":D", reinterpret_cast<uint32_t>(esp9) + 36, 29);
        exit(1, reinterpret_cast<uint32_t>(esp9) + 36, 29);
        esp10 = reinterpret_cast<void*>(reinterpret_cast<uint32_t>(esp10) - 4 + 4 - 4 + 4);
    }
    printf("\nYou got the key\n ", reinterpret_cast<uint32_t>(esp9) + 36, 29);
    lol(reinterpret_cast<uint32_t>(esp10) - 4 + 4 + 46, reinterpret_cast<uint32_t>(esp9) + 36, 29);
}
...
```

The above code:

* Opens a pipe for communication
* Forks
* The parent application writes to pipe and exits
* The forked child reads from the pipe and reads a number from keyboard (it expects the entered value to match the PID of the child)
* Forked child then does some work in ```lol``` function:

```c
void lol(void* a1, void* a2, int32_t a3) {
    void* v4;
    int32_t v5;
    int32_t v6;

    if (1) {
        printf("flag_is_not_here", v4, v5);
    } else {
        printf("%s", reinterpret_cast<int32_t>(__zero_stack_offset()) - 4 - 19, v6);
    }
    return;
}
```

Notice that there is a dead branch in this method that seems to output something. Let's go to the disassembled code (```objdump -d -M intel ./easy_re```):

```
080485f4 <lol>:
 80485f4:	55                   	push   ebp
 80485f5:	89 e5                	mov    ebp,esp
 80485f7:	83 ec 28             	sub    esp,0x28
 80485fa:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]  # some interesting calculations happening here
 80485fd:	83 c0 01             	add    eax,0x1
 8048600:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 8048603:	89 c2                	mov    edx,eax
 8048605:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]
 8048608:	83 c0 01             	add    eax,0x1
 804860b:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 804860e:	8d 04 02             	lea    eax,[edx+eax*1]
 8048611:	88 45 ed             	mov    BYTE PTR [ebp-0x13],al
 8048614:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]
 8048617:	83 c0 04             	add    eax,0x4
 804861a:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 804861d:	89 c2                	mov    edx,eax
 804861f:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]
 8048622:	83 c0 05             	add    eax,0x5
 8048625:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 8048628:	8d 04 02             	lea    eax,[edx+eax*1]
 804862b:	88 45 ee             	mov    BYTE PTR [ebp-0x12],al
 804862e:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]
 8048631:	83 c0 08             	add    eax,0x8
 8048634:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 8048637:	89 c2                	mov    edx,eax
 8048639:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]
 804863c:	83 c0 09             	add    eax,0x9
 804863f:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 8048642:	8d 04 02             	lea    eax,[edx+eax*1]
 8048645:	88 45 ef             	mov    BYTE PTR [ebp-0x11],al
 8048648:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]
 804864b:	83 c0 0c             	add    eax,0xc
 804864e:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 8048651:	89 c2                	mov    edx,eax
 8048653:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]
 8048656:	83 c0 0c             	add    eax,0xc
 8048659:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 804865c:	8d 04 02             	lea    eax,[edx+eax*1]
 804865f:	88 45 f0             	mov    BYTE PTR [ebp-0x10],al
 8048662:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]
 8048665:	83 c0 12             	add    eax,0x12
 8048668:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 804866b:	89 c2                	mov    edx,eax
 804866d:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]
 8048670:	83 c0 11             	add    eax,0x11
 8048673:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 8048676:	8d 04 02             	lea    eax,[edx+eax*1]
 8048679:	88 45 f1             	mov    BYTE PTR [ebp-0xf],al
 804867c:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]
 804867f:	83 c0 0a             	add    eax,0xa
 8048682:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 8048685:	89 c2                	mov    edx,eax
 8048687:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]
 804868a:	83 c0 15             	add    eax,0x15
 804868d:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 8048690:	8d 04 02             	lea    eax,[edx+eax*1]
 8048693:	88 45 f2             	mov    BYTE PTR [ebp-0xe],al
 8048696:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]
 8048699:	83 c0 09             	add    eax,0x9
 804869c:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 804869f:	89 c2                	mov    edx,eax
 80486a1:	8b 45 08             	mov    eax,DWORD PTR [ebp+0x8]
 80486a4:	83 c0 19             	add    eax,0x19
 80486a7:	0f b6 00             	movzx  eax,BYTE PTR [eax]
 80486aa:	8d 04 02             	lea    eax,[edx+eax*1]
 80486ad:	88 45 f3             	mov    BYTE PTR [ebp-0xd],al
 80486b0:	c7 45 f4 00 00 00 00 	mov    DWORD PTR [ebp-0xc],0x0   # we set the value to 0
 80486b7:	83 7d f4 01          	cmp    DWORD PTR [ebp-0xc],0x1   # and then compare it to 1
 80486bb:	75 16                	jne    80486d3 <lol+0xdf>        
 80486bd:	b8 c0 88 04 08       	mov    eax,0x80488c0             # this branch will never be taken
 80486c2:	8d 55 ed             	lea    edx,[ebp-0x13]            # and yet it's the one that prints
 80486c5:	89 54 24 04          	mov    DWORD PTR [esp+0x4],edx   #  the value built above
 80486c9:	89 04 24             	mov    DWORD PTR [esp],eax
 80486cc:	e8 ff fd ff ff       	call   80484d0 <printf@plt>
 80486d1:	eb 0d                	jmp    80486e0 <lol+0xec>
 80486d3:	b8 c3 88 04 08       	mov    eax,0x80488c3
 80486d8:	89 04 24             	mov    DWORD PTR [esp],eax
 80486db:	e8 f0 fd ff ff       	call   80484d0 <printf@plt>
 80486e0:	c9                   	leave  
 80486e1:	c3                   	ret    
```

As it turns out the decompiled C code does not reveal everything, there is a bunch of calculations occurring in that function, and yet the calculated value is abandoned and ```flag_is_not_here``` is printed instead.

To reveal that value let's patch out the executable and replace ```cmp    DWORD PTR [ebp-0xc],0x1``` with ```cmp    DWORD PTR [ebp-0xc],0x0```. This can be done in any binary editor. Essentially we are replacing ```83 7d f4 01``` with ```83 7d f4 00```.

Once the binary is patched we can run it. To figure out the PID we can first run the application, and while it's waiting for input run ```ps``` in a separate shell:

```sh
$ ps -ef | grep easy
root      1695  1529  0 15:54 pts/0    00:00:00 ./easy_re_patched
root      1696  1695  0 15:54 pts/0    00:00:00 [easy_re_patched] <defunct>
root      1702  1536  0 15:55 pts/1    00:00:00 grep easy
```

When we enter the PID we get the flag:

```sh
 ./easy_re_patched 

OMG!!!! I forgot kid's id
Ready to exit     
1696

You got the key
 rhelheg
```

The flag is ```RCTF{rhelheg}```.