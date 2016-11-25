---
layout: page
title: RC3 CTF 2016 Writeup: Bridge of Cyber
---

> 200 points
>
> Welcome to the "Bridge of Cyber"! It's the same concept as the "Bridge of Death" in Holy Grail. Our DNS servers aren't very good at their job because they ask more questions then they answer. Let's see if you can get the flag from our DNS.
>
>Nameserver: ns-920.awsdns-51.net
>
>Domain: misc200.ctf.rc3.club

I watched [Monty Python and the Holy Grail](http://www.imdb.com/title/tt0071853/) a **long** time ago so I carefully reviewed a ["Bridge of Death" clip](https://www.youtube.com/watch?v=cV0tCphFMr8) before proceeding.

This is a DNS challenge, so let's use [dig](https://en.wikipedia.org/wiki/Dig_%28command%29) to query the nameserver. The hints section also suggested this approach, and emphasized *subdomains*.

```
root@kali:~# dig any @ns-920.awsdns-51.net misc200.ctf.rc3.club

; <<>> DiG 9.10.3-P4-Debian <<>> any @ns-920.awsdns-51.net misc200.ctf.rc3.club
; (2 servers found)
;; global options: +cmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 39248
;; flags: qr aa rd; QUERY: 1, ANSWER: 6, AUTHORITY: 0, ADDITIONAL: 1
;; WARNING: recursion requested but not available

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags:; udp: 4096
;; QUESTION SECTION:
;misc200.ctf.rc3.club.		IN	ANY

;; ANSWER SECTION:
misc200.ctf.rc3.club.	172800	IN	NS	ns-1526.awsdns-62.org.
misc200.ctf.rc3.club.	172800	IN	NS	ns-1842.awsdns-38.co.uk.
misc200.ctf.rc3.club.	172800	IN	NS	ns-489.awsdns-61.com.
misc200.ctf.rc3.club.	172800	IN	NS	ns-920.awsdns-51.net.
misc200.ctf.rc3.club.	900	IN	SOA	ns-1526.awsdns-62.org. awsdns-hostmaster.amazon.com. 1 7200 900 1209600 86400
misc200.ctf.rc3.club.	300	IN	TXT	"What is the air-speed velocity of an unladen swallow"

;; Query time: 31 msec
;; SERVER: 205.251.195.152#53(205.251.195.152)
;; WHEN: Tue Nov 22 12:13:58 EST 2016
;; MSG SIZE  rcvd: 315
```

Notice that the TXT field contains the question from the movie. The answer is ```"What do you mean? An African or European swallow?"```. After trying a lot of different combinations of these words as the name of subdomain of ```misc200.ctf.rc3.club``` we finally get the following answer:

```
root@kali:~# dig any @ns-920.awsdns-51.net african.misc200.ctf.rc3.club
...
african.misc200.ctf.rc3.club. 300 IN	TXT	"Why was the developer broke? He spent all of his"
```

Googling this joke we find the answer - ```cache``` - and prepend it to the name. 

Several more questions and answers follow and we finally get this:

```
root@kali:~# dig any @ns-920.awsdns-51.net 1forrest1.penaltea.fsh.phishing.firewall.cache.african.misc200.ctf.rc3.club
...
1forrest1.penaltea.fsh.phishing.firewall.cache.african.misc200.ctf.rc3.club. 300 IN TXT	"Welp this is a depressing their is no flag here I guess your swallow was just to slow"
```

Son of a... :-) Ok, let's try ```european``` as the first answer. Some more questions and answers later we finally get the flag: ```RC3-2016-cyb3rxr05```

```
root@kali:~# dig any @ns-920.awsdns-51.net time.tomorrow.pi.european.misc200.ctf.rc3.club
...
time.tomorrow.pi.european.misc200.ctf.rc3.club.	300 IN SPF "RC3-2016-cyb3rxr05"
```
