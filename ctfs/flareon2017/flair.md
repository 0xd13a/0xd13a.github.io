---
layout: page
title: "FLARE-ON 2017 Writeup: flair.apk"
---

Flair challenge was the most enjoyable of all - no dead ends, just a steady exploration and unwinding of the code.

We are given an Android app to analyze. For some reason it consistently crashed on my phone, but running it was not necessary for discovering the flag.

It is important to mention that the app logic uses a cipher that I could only find in [BouncyCastle](https://www.bouncycastle.org/latest_releases.html) so installation of that library is a requirement. Also the [export strength encryption](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html) must be enabled in Java.

We begin by taking apart the APK file and decompiling the Java code. Using ```apktool```, ```d2j-dex2jar``` and any Java decompiler achieves that.

The logic that pieces together the flag is located in package ```com.flare_on.flair```. In ```Chotchkies.java``` the following code collects 4 pieces of flair and displays the resulting flag:

```java
...
String string2 = Chotchkies.decrypt(Util.getAllFlairPieces(flairs));
this.showStanDialog("Flair's about the fun!", "Oh yes, I hope this extra piece puts a terrific smile on your face:\n" + string2);
...
```

The 4 required pieces of flair are generated by the following classes:

- ```Brian.java```

Flair from Brian requires delving into the application resources and assets. We need to get text view current text color and text value, image view tag and a string from application info object.

- ```Michael.java```

Michael's flair comes from simple string manipulation with a little bit of bruteforcing of a text hash value.

- ```Milton.java```

Here flair is built by decrypting pieces of data and joining them together.

- ```Printer.java```

Finally printer flair comes from function reflection. We need to decrypt a number of strings and then continuously  reduce and simplify the code that in the end loads data that we need from a resource file.

As we collect and clean up the logic from different classes we put it together in one Java application: [Solve.java]({{ site.baseurl }}/ctfs/flareon2017/flair/Solve.java)

When we run it we get the flag:

```
Flair from Brian: hashtag_covfefe_Fajitas!

Flair from Michael: MYPRSHE__FTW

Milton phrase: A rich man is nothing but a poor man with money.
Flair from Milton: 10aea594831e0b42b956c578ef9a6d44ee39938d

Phrase from Printer: Give a man a fire and he'll be warm for a day. Set a man on fire and he'll be warm for the rest of his life.
Flair from Printer: 5f1be3c9b081c40ddfc4a0238156008ee71e24a4

Combined flair pieces: flair-MYPRSHE__FTW-hashtag_covfefe_Fajitas!-10aea594831e0b42b956c578ef9a6d44ee39938d-5f1be3c9b081c40ddfc4a0238156008ee71e24a4

Flair correct: true

Solution: pc_lo4d_l3tt3r_gl1tch@flare-on.com
```