---
layout: page
title: "PlaidCTF 2017 Writeup: Pykemon"
---

> Pykemon
>
> Web (151 pts)
>
> Gotta catch them FLAGs! 
>
> Take [this]({{ site.baseurl }}/ctfs/pctf2017/pykemon/pykemon.tar.bz2) with you.

Pykemon was a game of catching Pokemon-like sprites, one of which is a flag. I played it for a while and caught a bunch of "pykemons", but flag seemed elusive.

Analysis of supplied code seems to suggest that catching it would be impossible, because it's "rarity" is 0 - there was no code branch for handling catching a pykemon like that.

There were other things that the code revealed, however - the flag is stored in the FLAG pykemon's description, and all pykemons live in the session list called "room".

The session data is stored in a Flask session cookie, which can be decoded but not modified. Once the flag pykemon is in the game we can capture the session:

```http
Set-Cookie: session=.eJzdl29vqkgUxr_Khte-QJBWTPqiYwtolK54BWFzsxlmXFAG
JCIqNP3u94DVKhdts9smzb5S5h9zfvOc5wzPnIsZS7hOu8ERnHr-mus8c3EWzMJlxHX-e
uboLCGrebyeF8_P3B8u1-EmGsvsqc5sceTh6dDDgrShQsujatvDlhQ6U53vqTufhJT1NC
NzLDPvqWbaU5s-EXXWU5XU6aKAhErszlFKp0aGLTOl2tBzov7GHSMeqxPPVWXRmfY8qvW
bbph4tiUFPQ36RSMfeHd33EuD82OuI902uAiHs7oNloOiOQn2A7g_sxVOZmTNNbh4Tutm
NPXtfukVXs3XGde55RtcEsP_txdgq-k7gpkPRCRBFLlj6fwgQqlTTn35WcxeLsMPwuTtq
eH3VAaQdB9gxK5YAGMpwFs6Y8RmJcQmG8yRQkMAqdEltnYAkvH7eUruqiYj3S0cyMizBT
klouG7kZHMxijBcCCOJcG4vgSHsKEhS50MxSVwS8lIhuCwZIA8Sg9QBfl3qOVGz4HWdFa
4lu3DRXDGVL7OlKoKb4-lrRuSgmfjMjoqtEsN2mM0mllr37Vk0JkOuBQIGVBEgQerpg5g
tTO0Bg3FRDMTu8AstI4aaoo14cLidxfDPXRWw4X23bAioeY74Wp0cyKfK-F-R6W0PlUp-
sN3V0pduP9aKU-jL1CKqfoxnNRJmL4PzwxOfgFmGtsC42fjZk5VGYxXinoa2lBLXzqAB0
IFTEahsCUY94pkLQ_WY0SYePYUbQsVgNo2oBAG6lkXqntbV0lcLQBV-aX5FyZPp_0YDge
Kgw8qlGJH8PmjKhUkYUFZYG3iEWHHnCk6M3axxthXNmyUvlKrw302oIJ83_djKJ4ib11H
_rrBlKhSdFVh36smtj9cEy8MuFQen-7_a3m8jDDAlnzwKUCjCNiC371K1oUyKSgXdxEkZ
T8C5YmFekm2BfWA4qJ7wA0KFKR80EU-eFSR7DkGldPC71R5gwUzKJVrJUdUN63fUZUbuY
Tp2FlBVLbri1Eloa_zcVS2od13DexRnxjR-mmoGf_oipzjByWdmbKuq2SHcyMjGn-M57Z
ZiccIdsjYXsiY087TeMr2nOxOY7kSieAktkXeSZL_iy_d1iTXZ_gSaX2FLwWAcLVPqkdv
ZplJ4SFnSQXHQLsoA49irtVPnH2VfJ0nZ4A0JqoZ2BYkGngY1diWTodHHO2bmgQqJl9Mo
ENnNYGK9qcH-yyBbt7JH81c4OwjF4DveHcq3eETr9k_7rdfcXmC2KgPsVQlMnc1k4HX5q
TIxJAtConAqgFR5cQZt7wxcMLA7Vj6VFMq55ZlC8VFCXNVtoVsfrtg1VT9_QYueVh1QAX
Nvi9_PKv67etoYFPiiaH9bBw-5P4myzSCz-QmVI1V8R7-5eUXXH3iyQ.C92Cxg.1ZP-Dj
q5RziaZ9MmJZeu1Cy7V0g; HttpOnly; Path=/
```

The session cookie is base64-encoded and compressed, the following code decodes it:

```python
from itsdangerous import base64_decode
import zlib

compressed = False
payload = "..." # put cookie value here...

if payload.startswith('.'):
	compressed = True
	payload = payload[1:]

data = payload.split(".")[0]

data = base64_decode(data)
if compressed:
	data = zlib.decompress(data)

print data.decode("utf-8")
```

The decoded cookie is a JSON document. Individual text fields are also base64-encoded. Here's how the section we are interested in looks after decoding:

```json
{
	"balls":8,
	"caught":{"pykemon":[]},
	"room":{
		"pykemon":[...,
			{
				"description":{" b":"PCTF{N0t_4_sh1ny_M4g1k4rp}"},
				"hp":71,
				"name":{" b":"FLAG"},
				"nickname":{" b":"FLAG"},
				"pid":{" b":"FLAG71"},
				"rarity":0,
				"sprite":{" b":"images/flag.png"}
			},...],
		
		"pykemon_count":14,
		"rid":0
	}
}
```

The flag is ```PCTF{N0t_4_sh1ny_M4g1k4rp}```.