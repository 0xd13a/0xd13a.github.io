---
layout: page
title: "FLARE-ON 2017 Writeup: shell.php"
---

The provided PHP file starts with a Base64-encoded that has to be decrypted by XORing it with a key. The key is (at most) 2 full MD5 strings (up to 64 characters), followed by the data decrypted earlier. The end result is a script that will be executed.

This is the decoding logic:

```
...
$o_o=isset($_POST['o_o']) ? $_POST['o_o'] : "";
$o_o=md5($o_o).substr(MD5(strrev($o_o)),0,strlen($o_o));
for($o___o=0;$o___o<2268;$o___o++)
{
	$o__o_[$o___o]=chr((ord($o__o_[$o___o])^ord($o_o[$o___o]))%256);
	$o_o.=$o__o_[$o___o];
}

if(MD5($o__o_)=='43a141570e0c926e0e3673216a4dd73d')
{
	if(isset($_POST['o_o']))
		@setcookie('o_o', $_POST['o_o']);
	$o___o=create_function('',$o__o_);
	unset($o_o,$o__o_);
	$o___o();
}
...
```

Here we have a problem - the string that the MD5s will be based on is missing - it is expected to be provided at run time. Without it the decoding of the first 64 characters is difficult - bruteforcing would take a long time. How do we approach this?

The facts that we know about the key can help us here, combined with some educated guesses. The plaintext is a PHP source, so it is a set of characters that are following PHP syntax. The characters in the key are from the set ```[0-9a-f]```. Also, once we have correctly found out some of the first 64 characters in the plaintext (let's assume that we have to deal with the full two MD5 values), we can immediately use them to decrypt plaintext further down the line. The more we find from the plaintext the more guesses about the possible key characters we can make, and so on, until we have the full plaintext decoded.

We start by building a table of possible plaintext values based on the first 64 characters of the ciphertext and the possible ranges of key characters (here we denote ```tab```, ```cr``` and ```lf``` characters as ```<9>```, ```<d>```, and ```<a>``` respectively):

```
   Key chars        Plaintext chars
   
00 0123456789abcdef pqrstuvwxy!"#$%&
01 0123456789abcdef 67452301>?gdebc`
02 0123456789abcdef ;:98?>=<32jihonm
03 0123456789abcdef ./,-*+()&'|}z{x	
04 0123456789abcdef "# !&'$%*+spqvwt
05 0123456789abcdef 98;:=<?>10hkjmlo
06 0123456789bef    _^]\[ZYXWV<d><a><9>
07 8abcdef          <a>SPQVWT				
08 0123456789abcdef  !"#$%&'()qrstuv
09 0123456789abcdef :;89>?<=23khinol
10 0123456789abcdef a`cbedgfih032547
11 0123456789abcdef pqrstuvwxy!"#$%&
12 0123456789abcdef rspqvwtuz{# !&'$
13 0123456789abcdef 45670123<=efg`ab
14 0123456789abcdef #"! '&%$+*rqpwvu
15 0123456789abcdef &'$%"# !./wtursp
16 0123456789abcdef srqpwvut{z"! '&%
17 0123456789abcdef hijklmno`a9:;<=>
18 0123456789bef    _^]\[ZYXWV<d><a><9>
19 347abcdef        <d><a><9>_\]Z[X		
20 0123456789abcdef jkhinolmbc;89>?<
21 0123456789abcdef `abcdefghi123456
22 0123456789abcdef $%&' !"#,-uvwpqr	
23 0123456789abcdef )(+*-,/.! x{z}|	
24 0123456789abcdef a`cbedgfih032547
25 0123456789abcdef "# !&'$%*+spqvwt
26 0123456789abcdef '&%$#"! /.vutsrq
27 0123456789abcdef lmnohijkde=>?89:
28 0123456789abcdef  !"#$%&'()qrstuv
29 0123456789abcdef ! #"%$'&)(psrutw
30 0123456789abcdef '&%$#"! /.vutsrq
31 126abcdef        <a><9><d>ZYX_^]
32 0123456789       RSPQVWTUZ[
33 0123456789       LMNOHIJKDE
34 0123456789       TUVWPQRS\]
35 9abcdef          <9>QRSTUV
36 047abcdef        <d><9><a>\_^YX[
37 0123456789abcdef '&%$#"! /.vutsrq
38 0123456789abcdef jkhinolmbc;89>?<
39 0123456789abe    XYZ[\]^_PQ<9><a><d>
40 0123456789abcdef ihkjmlona`8;:=<?
41 0123456789abcdef ./,-*+()&'|}z{x
42 156abcdef        <d><9><a>]^_XYZ
43 0123456789abcdef ~}|{zyxwv.-,+*)
44 0123456789abcdef z{xy~|}rs+()./,	
45 037abcdef        <a><9><d>[XY^_\		
46 9abcdef          <a>RQPWVU				
47 0123456789abcdef &'$%"# !./wtursp
48 0123456789abcdef tuvwpqrs|}%&' !"
49 0123456789abcdef $%&' !"#,-uvwpqr
50 0123456789abcdef mlonihkjed<?>98;
51 0123456789abcdef bc`afgdejk301674
52 0123456789abcdef +*)(/.-,#"zyx~}
53 0123456789abcdef srqpwvut{z"! '&%
54 0123456789abcdef nolmjkhifg?<=:;8
55 0123456789abcdef rspqvwtuz{# !&'$
56 0123456789abcdef '&%$#"! /.vutsrq
57 0123456789abe    XYZ[\]^_PQ<9><a><d>
58 0123456789cd     YX[Z]\_^QP<a><d>
59 abcdef           HKJMLO
60 0123456789       QPSRUTWVYX
61 abcdef           VUTSRQ
62 037abcdef        <a><9><d>[XY^_\
63 0123456789abcdef tuvwpqrs|}%&' !"
```

As we look closely there are some interesting features in the plaintext characters column:

- On some of the lines we see a ```;``` as one of the possible characters, with ```<d>``` and ```<a>``` appearing on the following lines (e.g. on lines 5-7). This could be the semicolon terminating the PHP statement followed by Windows EOL characters 

- As we look at the decryption logic we see references to ```$_POST```, ```if``` statements, ```isset``` function calls, variable assignments - we can guess that they are also present in this plaintext, guess their positions, and then test our guesses by decrypting plaintext further down the line and checking if it's still syntactically correct.

Here are intermediate results of our guesses:

```
   Key chars        Plaintext chars      Decoded
   
00 0123456789abcdef pqrstuvwxy!"#$%&     $
01 0123456789abcdef 67452301>?gdebc`
02 0123456789abcdef ;:98?>=<32jihonm     =
03 0123456789abcdef ./,-*+()&'|}z{x      '
04 0123456789abcdef "# !&'$%*+spqvwt     '
05 0123456789abcdef 98;:=<?>10hkjmlo     ;
06 0123456789bef    _^]\[ZYXWV<d><a><9>  <d>
07 8abcdef          <a>SPQVWT            <a>
08 0123456789abcdef  !"#$%&'()qrstuv
09 0123456789abcdef :;89>?<=23khinol
10 0123456789abcdef a`cbedgfih032547
11 0123456789abcdef pqrstuvwxy!"#$%&
12 0123456789abcdef rspqvwtuz{# !&'$
13 0123456789abcdef 45670123<=efg`ab
14 0123456789abcdef #"! '&%$+*rqpwvu
15 0123456789abcdef &'$%"# !./wtursp
16 0123456789abcdef srqpwvut{z"! '&%
17 0123456789abcdef hijklmno`a9:;<=>     ;
18 0123456789bef    _^]\[ZYXWV<d><a><9>  <d>
19 347abcdef        <d><a><9>_\]Z[X      <a>
20 0123456789abcdef jkhinolmbc;89>?<
21 0123456789abcdef `abcdefghi123456
22 0123456789abcdef $%&' !"#,-uvwpqr	
23 0123456789abcdef )(+*-,/.! x{z}|	
24 0123456789abcdef a`cbedgfih032547
25 0123456789abcdef "# !&'$%*+spqvwt
26 0123456789abcdef '&%$#"! /.vutsrq
27 0123456789abcdef lmnohijkde=>?89:
28 0123456789abcdef  !"#$%&'()qrstuv
29 0123456789abcdef ! #"%$'&)(psrutw
30 0123456789abcdef '&%$#"! /.vutsrq     $
31 126abcdef        <a><9><d>ZYX_^]      _
32 0123456789       RSPQVWTUZ[           P
33 0123456789       LMNOHIJKDE           O
34 0123456789       TUVWPQRS\]           S
35 9abcdef          <9>QRSTUV            T
36 047abcdef        <d><9><a>\_^YX[      [
37 0123456789abcdef '&%$#"! /.vutsrq     '
38 0123456789abcdef jkhinolmbc;89>?<
39 0123456789abe    XYZ[\]^_PQ<9><a><d>
40 0123456789abcdef ihkjmlona`8;:=<?
41 0123456789abcdef ./,-*+()&'|}z{x      '
42 156abcdef        <d><9><a>]^_XYZ      ]
43 0123456789abcdef ~}|{zyxwv.-,+*)
44 0123456789abcdef z{xy~|}rs+()./,	
45 037abcdef        <a><9><d>[XY^_\      <d>
46 9abcdef          <a>RQPWVU            <a>
47 0123456789abcdef &'$%"# !./wtursp
48 0123456789abcdef tuvwpqrs|}%&' !"
49 0123456789abcdef $%&' !"#,-uvwpqr
50 0123456789abcdef mlonihkjed<?>98;
51 0123456789abcdef bc`afgdejk301674
52 0123456789abcdef +*)(/.-,#"zyx~}
53 0123456789abcdef srqpwvut{z"! '&%
54 0123456789abcdef nolmjkhifg?<=:;8
55 0123456789abcdef rspqvwtuz{# !&'$
56 0123456789abcdef '&%$#"! /.vutsrq     $
57 0123456789abe    XYZ[\]^_PQ<9><a><d>  _
58 0123456789cd     YX[Z]\_^QP<a><d>     P
59 abcdef           HKJMLO               O
60 0123456789       QPSRUTWVYX           S
61 abcdef           VUTSRQ               T
62 037abcdef        <a><9><d>[XY^_\      [
63 0123456789abcdef tuvwpqrs|}%&' !"     '
```

Continuing in this fashion we can recover the full encryption key: ```db6952b84a49b934acb436418ad9d93d237df05769afc796d067bccb379f2cac```, and use it to decrypt [the plaintext]({{ site.baseurl }}/ctfs/flareon2017/shell/shell_decrypted.php).

In the decoded portion we see a reference to a site with interesting JavaScript code experiments. Three of those are related to the 3 encoded binary blobs that we need to decipher in order to recover the key.

Since the blob content is meant to be rendered in a browser we can guess that it is an HTML snippet of some kind. With a little bit of experientation we can see that all 3 start with text ```<html>\x0d\x0a<title>```. We can use the following code to recover the repeating keys in all 3 cases:

```python
import base64

d1=bytearray(base64.b64decode("SDcGHg1feVUIEhsbDxFhIBIYFQY+VwMWTyAcOhEYAw4VLVBaXRsKADMXTWxrSH4ZS1IiAgA3GxYUQVMvBFdVTysRMQAaQUxZYTlsTg0MECZSGgVcNn9AAwobXgcxHQRBAxMcWwodHV5EfxQfAAYrMlsCQlJBAAAAAAAAAAAAAAAAAFZhf3ldEQY6FBIbGw8RYlAxGEE5PkAOGwoWVHgCQ1BGVBdRCAAGQVQ2Fk4RX0gsVxQbHxdKMU8ABBU9MUADABkCGHdQFQ4TXDEfW0VDCkk0XiNcRjJxaDocSFgdck9CTgpPDx9bIjQKUW1NWwhERnVeSxhEDVs0LBlIR0VlBjtbBV4fcBtIEU8dMVoDACc3ORNPI08SGDZXA1pbSlZzGU5XVV1jGxURHQoEK0x+a11bPVsCC1FufmNdGxUMGGE="))

d2=bytearray(base64.b64decode("VBArMg1HYn1XGAwaAw1GDCsACwkeDgABUkAcESszBEdifVdNSENPJRkrNwgcGldMHFVfSEgwOjETEE9aRlJoZFMKFzsmQRALSilMEQsXHEUrPg9ZDRAoAwkBHVVIfzkNGAgaBAhUU00AAAAAAAAAAAAAAAAASkZSVV0KDAUCHBFQHA0MFjEVHB0BCgBNTAJVX3hkAkQiFh8ESw0AG0M5MBNRGkpdWV4bVEEVdGJGRR9XGBgcAgpVCDAsCA0GGAVWBAwcBxQqKwRCGxgbVkJFR11IdHcbRFxOUkNNV0RAVXIKSgxCWk1aVkdGQVI8dxRTVl5CR0JLVAQdOStbXkRfXlxOFEULUCp2SFJIUlVGQlUtRhExMQQLJyMmIFgDTUQtYmZIRUAECB4MHhtWRHA9Dh0WSWZmWUEHHBUzYQ=="))

d3=bytearray(base64.b64decode("DycdGg1hYjl8FURaAVZxPhgNOQpdMxVIRwNKc0YDCCsDVn5sJxJMHmJJOgArB1olFA0JHQN+TlcpOgFBKUEAA1M+RVUVDjsWEy8PQUEMV3IsSgJxCFY0IkJAGVY3HV9DbQsRaU1eSxl6IR0SEykOX2gnEAwZGHJHRU0OUn4hFUUADlw8UhRPNwpaJwlZE14Df1IRDi1HS30JFlZAHnRAEQ4tR0p9CRZXQB50LFkHNgNfEgROWkVLZV1bGHVbHyJMSRFZCQtGRU0bQAFpSEtBHxsLVEdaeEEUfCd2akdKYAFaJXBdT3BeHBRFV3IdXCV1PhsUXFUBBR5hXFwwdxsab1kECFoaM0FET2pEd2owBXpAC2ZAS11sMhVmJREWVlFyDV4ldFIdcUMBWlBbcl5CSGFTUCEPW08eEyYNSgJhYjl8Tk9BCUpvDxsAODBeLwUfE08AAAAAAAAAAAAAAAAAEXFkfV1wB0ctDRM="))

plain = "<html>\x0d\x0a<title>"

out = ""
for x in range(len(plain)):
	out += chr(ord(plain[x]) ^ d1[x])
print out

out = ""
for x in range(len(plain)):
	out += chr(ord(plain[x]) ^ d2[x])
print out

out = ""
for x in range(len(plain)):
	out += chr(ord(plain[x]) ^ d3[x])
print out
```

The script outputs the following characters:

```
C:\work\flareon17\shell>\Python27\python.exe shell_solve.py
t_rsaat_4froct_
hx__ayowklenohx
3Oiwa_o3@a-.m3O
```

Notice that the last 2 characters in each line match the beginning of that line, so they are likely the beginning of the repeating key. When we combine the characters in the column-first order we get the key: ```th3_xOr_is_waaaay_too_w34k@flare-on.com``` 