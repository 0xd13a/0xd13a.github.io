---
layout: page
title: "Sharif CTF 2018 Writeup: The Skeleton Key"
---

> 200
>
> The Skeleton Key
> 
> Find the flag :)
>
> [Download]({{ site.baseurl }}/ctfs/sharif2018/the-skeleton-key/The Skeleton Key.zip)

Let's open the APK with the following set of commands:

```
apktool d -s The\ Skeleton\ Key.apk 
dex2jar classes.dex 
java -jar cfr_0_113.jar --outputdir src classes_dex2jar.jar
```

Close inspection reveals nothing interesting - pretty much all the app does is display the picture of a [skull]({{ site.baseurl }}/ctfs/sharif2018/the-skeleton-key/logo.svg):

```java
        WebView webView = (WebView)this.findViewById(2131492941);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL("", "<p><img height=\"100%\" width=\"100%\" 
		src=\"file:///android_asset/logo.svg\" /></p>", "text/html", "utf-8", "");
```

![skull]({{ site.baseurl }}/ctfs/sharif2018/the-skeleton-key/skull.png)

At first glance there is nothing special about this image. However the Google search for similar images brings up the original for this picture, which is much smaller in size: https://upload.wikimedia.org/wikipedia/commons/e/e3/Skull-Icon.svg

Let's load the image in http://www.clker.com/inc/svgedit/svg-editor.html. We can select and delete the skull, which reveals a large black dot remaining in the image. Once we stretch it out a little bit we discover the hash value:

![skull]({{ site.baseurl }}/ctfs/sharif2018/the-skeleton-key/key.png)

The flag is ```SharifCTF{be278492ae9b998eaebe3ca54c8000de}```.