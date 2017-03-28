---
layout: page
title: "VolgaCTF 2017 Quals Writeup: Angry Guessing Game"
---

> Angry Guessing Game
> 
> 200
> 
> This game is currently in a trial mode. It asks for a license key to continue. Can you find this key?
>
> [guessing_game]({{ site.baseurl }}/ctfs/volgactf2017/angry-guessing-game/guessing_game)

The game begins by asking us to guess the random number. This happens a couple of times and then it asks for a license key. When the wrong key is entered the application exits. 

The code contains the license key somewhere inside, either as a constant or as an algorithm. Let's reverse it in [Snowman](https://derevenets.com/).

There is a lot of code to go through, so first let's look for simple things, like the first character from the flag ('V', ASCII 86, 0x56 ). We get lucky and stumble upon the following function, which looks like the verification method for the flag:

```c
int32_t fun_67d0(struct s1* rdi, void** rsi) {
    struct s2* rax3;

    if (rdi->f16 > 41) {
        rax3 = rdi->f0;
        *reinterpret_cast<unsigned char*>(&rax3) = 
		static_cast<unsigned char>(reinterpret_cast<uint1_t>(!!(reinterpret_cast<unsigned char>(reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f40 == 54)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f39 == 99)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f38 == 98)) 
		& static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f37 == 55))))) 
		& reinterpret_cast<unsigned char>(reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f36 == 48)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f35 == 52)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f34 == 57)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f33 == 48)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f32 == 55)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f31 == 52)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f30 == 54)) 
		& static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f29 == 0x65))))))))) 
		& reinterpret_cast<unsigned char>(reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f28 == 49)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f27 == 99)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f26 == 53)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f25 == 57)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f24 == 48)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f23 == 97)) 
		& static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f22 == 53)))))))) 
		& reinterpret_cast<unsigned char>(reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f21 == 57)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f20 == 48)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f19 == 98)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f18 == 0x65)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f17 == 57)) 
		& static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f16 == 55))))))) 
		& reinterpret_cast<unsigned char>(reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f15 == 98)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f14 == 0x65)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f13 == 53)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f12 == 55)) 
		& static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f11 == 54)))))) 
		& reinterpret_cast<unsigned char>(reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f10 == 98)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f9 == 0x65)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f8 == 0x7b)) 
		& static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f7 == 70))))) 
		& reinterpret_cast<unsigned char>(reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f6 == 84)) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f5 == 67)) 
		& static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f4 == 97)))) 
		& reinterpret_cast<unsigned char>(reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f3 == 0x67)) 
		& static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f2 == 0x6c))) 
		& reinterpret_cast<unsigned char>(static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f1 == 0x6f)) 
		& static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f0 == 86))))))))))) 
		& static_cast<unsigned char>(reinterpret_cast<uint1_t>(rax3->f41 == 0x7d)))));
        return *reinterpret_cast<int32_t*>(&rax3);
    } else {
        return 0;
    }
}
```

By carefully putting together different characters in the right order we get the flag ```VolgaCTF{eb675eb79eb095a095c1e64709407bc6}```.