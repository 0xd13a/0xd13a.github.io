---
layout: page
title: "SECCON 2016 Online CTF Writeup: Vigenere"
---

> 100 points
> 
> ```k: ????????????```
>
> ```p: SECCON{???????????????????????????????????}```
>
> ```c: LMIG}RPEDOEEWKJIQIWKJWMNDTSR}TFVUFWYOCBAJBQ```
>
> ```k=key, p=plain, c=cipher, md5(p)=f528a6ab914c1ecf856a1d93103948fe```
> 
> ```-|ABCDEFGHIJKLMNOPQRSTUVWXYZ{}```
> ```-+----------------------------```
> ```A|ABCDEFGHIJKLMNOPQRSTUVWXYZ{}```
> ```B|BCDEFGHIJKLMNOPQRSTUVWXYZ{}A```
> ```C|CDEFGHIJKLMNOPQRSTUVWXYZ{}AB```
> ```D|DEFGHIJKLMNOPQRSTUVWXYZ{}ABC```
> ```E|EFGHIJKLMNOPQRSTUVWXYZ{}ABCD```
> ```F|FGHIJKLMNOPQRSTUVWXYZ{}ABCDE```
> ```G|GHIJKLMNOPQRSTUVWXYZ{}ABCDEF```
> ```H|HIJKLMNOPQRSTUVWXYZ{}ABCDEFG```
> ```I|IJKLMNOPQRSTUVWXYZ{}ABCDEFGH```
> ```J|JKLMNOPQRSTUVWXYZ{}ABCDEFGHI```
> ```K|KLMNOPQRSTUVWXYZ{}ABCDEFGHIJ```
> ```L|LMNOPQRSTUVWXYZ{}ABCDEFGHIJK```
> ```M|MNOPQRSTUVWXYZ{}ABCDEFGHIJKL```
> ```N|NOPQRSTUVWXYZ{}ABCDEFGHIJKLM```
> ```O|OPQRSTUVWXYZ{}ABCDEFGHIJKLMN```
> ```P|PQRSTUVWXYZ{}ABCDEFGHIJKLMNO```
> ```Q|QRSTUVWXYZ{}ABCDEFGHIJKLMNOP```
> ```R|RSTUVWXYZ{}ABCDEFGHIJKLMNOPQ```
> ```S|STUVWXYZ{}ABCDEFGHIJKLMNOPQR```
> ```T|TUVWXYZ{}ABCDEFGHIJKLMNOPQRS```
> ```U|UVWXYZ{}ABCDEFGHIJKLMNOPQRST```
> ```V|VWXYZ{}ABCDEFGHIJKLMNOPQRSTU```
> ```W|WXYZ{}ABCDEFGHIJKLMNOPQRSTUV```
> ```X|XYZ{}ABCDEFGHIJKLMNOPQRSTUVW```
> ```Y|YZ{}ABCDEFGHIJKLMNOPQRSTUVWX```
> ```Z|Z{}ABCDEFGHIJKLMNOPQRSTUVWXY```
> ```{|{}ABCDEFGHIJKLMNOPQRSTUVWXYZ```
> ```}|}ABCDEFGHIJKLMNOPQRSTUVWXYZ{```
> 
> Vigenere cipher
> [https://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher](https://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher)

This challenge seems simple enough, so doing it by hand is a viable option.

In a Vigenere cipher the key is repeated in sequence during encryption (e.g. ```KEYKEYKEYKEY```). Because each question mark in the given plain- and ciphertext, and the key is likely a placeholder for the missing letter we can conclude that the key is 12 characters long. This gives use the following data to start with:

```
Plaintext:  SECCON{???????????????????????????????????}
Key:        XxxxxxxxxxxxXxxxxxxxxxxxXxxxxxxxxxxxXxxxxxx
Ciphertext: LMIG}RPEDOEEWKJIQIWKJWMNDTSR}TFVUFWYOCBAJBQ
```

Luckily in some positions the plaintext and the ciphertext letters are both available. This allows us to recover some key letters. To do that we take a plaintext letter and find a row in the table that starts with it. Then we trace through the row to find the corresponding ciphertext letter. Once we find it we trace up the column to find the key letter and record it. For example, the first plaintext letter ```S``` corresponds to ciphertext letter ```L``` which in turn corresponds to key letter ```V```. Let's do this for all known plaintext letters:

```
Plaintext:  SECCON{???????????????????????????????????}
Key:        VIGENERxxxxxXxxxxxxxxxxxXxxxxxxxxxxxXxxxxxR
Ciphertext: LMIG}RPEDOEEWKJIQIWKJWMNDTSR}TFVUFWYOCBAJBQ
```

Knowing that the key repeats itself we can now fill out some of the missing parts of key string...:

```
Plaintext:  SECCON{???????????????????????????????????}
Key:        VIGENERxxxxxVIGENERxxxxxVIGENERxxxxxVIGENER
Ciphertext: LMIG}RPEDOEEWKJIQIWKJWMNDTSR}TFVUFWYOCBAJBQ
```

...and then recover corresponding plaintext:

```
Plaintext:  SECCON{?????BCDEDEF?????KLMNOPQ?????VWXYYZ}
Key:        VIGENERxxxxxVIGENERxxxxxVIGENERxxxxxVIGENER
Ciphertext: LMIG}RPEDOEEWKJIQIWKJWMNDTSR}TFVUFWYOCBAJBQ
```

Now it's time to do a bit of guessing. First, the next letter in the key is likely ```E```. Substituting it gives us the letter ```A``` in the plaintext. We can see a pattern developing - an alphabet sequence with some letters sequences repeating.

Guessing the first unknown sequence of letters in the plaintext to be some combination of ```A```'s and ```B```'s gives us the likely missing key sequence as ```CODE```:

```
Plaintext:  SECCON{ABABABCDEDEF?????KLMNOPQ?????VWXYYZ}
Key:        VIGENERECODEVIGENERxxxxxVIGENERxxxxxVIGENER
Ciphertext: LMIG}RPEDOEEWKJIQIWKJWMNDTSR}TFVUFWYOCBAJBQ
```

Completing the plaintext with the key gives us the flag - ```SECCON{ABABABCDEDEFGHIJJKLMNOPQRSTTUVWXYYZ}```:

```
Plaintext:  SECCON{ABABABCDEDEFGHIJJKLMNOPQRSTTUVWXYYZ}
Key:        VIGENERECODEVIGENERECODEVIGENERECODEVIGENER
Ciphertext: LMIG}RPEDOEEWKJIQIWKJWMNDTSR}TFVUFWYOCBAJBQ
```

