---
layout: page
title: "FLARE-ON 2017 Writeup: login.html"
---

This is a very simple "warmup" challenge. The HTML file that is provided contains code that checks for the password:

```javascript
...
var rotFlag = flag.replace(/[a-zA-Z]/g, function(c){return String.fromCharCode((c <= "Z" ? 90 : 122) >= (c = c.charCodeAt(0) + 13) ? c : c - 26);});
if ("PyvragFvqrYbtvafNerRnfl@syner-ba.pbz" == rotFlag) {
    alert("Correct flag!");
} else {
...
``` 

The source itself hints at ROT encoding, and a site like [http://www.rot13.com/](http://www.rot13.com/) instantly gives us the flag: ```ClientSideLoginsAreEasy@flare-on.com```.