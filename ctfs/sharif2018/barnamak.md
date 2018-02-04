---
layout: page
title: "Sharif CTF 2018 Writeup: Barnamak"
---

> 200
>
> Barnamak
> 
> Run the application and capture the flag!
>
> [Download]({{ site.baseurl }}/ctfs/sharif2018/barnamak/Find_Flag.zip)

The Android application contains the hidden flag. We crack it open and reverse to Java with the following commands:

```
apktool d -s Find_Flag.apk 
dex2jar classes.dex 
java -jar cfr_0_113.jar --outputdir src classes_dex2jar.jar
```

There are a couple of interesting places in the source, all of them in ```com/challenge_android/fragments/ChallengeFragment.java```.

First, it looks like it is expecting the current latitude/longitude to be ```45/-93```:

```java
    public boolean b() {
        Integer n = Integer.parseInt("2C", 16); // 44
        Integer n2 = Integer.parseInt("5B", 16); //91
        int n3 = 1 + n; // 45
        int n4 = -2 + (- n2.intValue()); // -93
        Location location = this.location;
        boolean bl = false;
        if (location == null) return bl;
        if ((int)this.location.getLatitude() == n3 && (int)this.location.getLongitude() == n4) {
```		

Second, the flag seems to be XORed with the current latitude value:

```java
    private static String iia(int[] arrn, String string2) {
        String string3 = "";
        for (int i = 0; i < arrn.length; ++i) {
            string3 = string3 + (char)(-48 + arrn[i] ^ string2.charAt(i % (-1 + string2.length())));
        }
        return string3;
    }
...
    String string2 = ChallengeFragment.iia(new int[]{162, 136, 133, 131, 68, 141, 119, 68, 169, 160, 
	49, 68, 171, 130, 68, 168, 139, 138, 131, 112, 141, 113, 128, 129}, 
	String.valueOf((int)Math.round(ChallengeFragment.this.location.getLatitude())));
```

As a shortcut let's run the values in the array through the ```xortool```. One of the values that it produces is the following string:

```
::::::::::::::
xortool_out/128.out
::::::::::::::
fLAG.I..md..oF.lONG.I.DE
``` 

So it seems that the flag is the MD5 of longitude, which we already know to be ```-93```:

```sh
$ echo -n -93 | md5sum
87a20a335768a82441478f655afd95fe -
```

The flag is ```SharifCTF{87a20a335768a82441478f655afd95fe}```.