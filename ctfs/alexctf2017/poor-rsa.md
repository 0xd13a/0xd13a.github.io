---
layout: page
title: "Alex CTF 2017 Writeup: Poor RSA"
---

> CR4: Poor RSA
>
> 200
>
> This time Fady decided to go for modern cryptography implementations, He is fascinated with choosing his own prime numbers, so he picked up RSA once more. Yet he was unlucky again!
>
> poor_rsa.tar.gz

For this challenge we are given the tarball with ciphertext and a public key. The goal is to derive the private key from the public key and decrypt the flag.

We will use [RsaCtfTool](https://github.com/Ganapati/RsaCtfTool) to cheat :smile::

```sh
$ python RsaCtfTool.py --publickey ./key.pub --uncipher ./flag --verbose --private
Try weak key attack
-----BEGIN RSA PRIVATE KEY-----
MIH5AgEAAjJSqZ4knufPPAy/ljoAlmF3K8nN9uHj+/xuRKB6Xg+JRFep+Bw64TKs
VoPTWyi6XDJCQwIDAQABAjIzrQnKBvUPnpCxrK5x85DWuS8dbTtmFP+HEYHE3wja
TF9QEkV6ZDCUBers1jQeQwJ5MQIaAImWgwYMdrnA3lgaaeDqnZG+0Qcb6x2SSjcC
GgCZzedK7e6Hrf/daEy8R451mHC08gaS9lJVAhlmZEB1y+i/LC1L27xXycIhqKPe
aoR6qVfZAhlbPhKLmhFavne/AqQbQhwaWT/rqHUL9EMtAhk5pem+TgbW3zCYF8v7
j0mjJ31NC+0sLmx5
-----END RSA PRIVATE KEY-----
Clear text : .?&?d??#H?u6L???:ALEXCTF{SMALL_PRIMES_ARE_BAD}
```

The flag is ```ALEXCTF{SMALL_PRIMES_ARE_BAD}```.