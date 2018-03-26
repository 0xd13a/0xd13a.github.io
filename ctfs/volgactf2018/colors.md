---
layout: page
title: "VolgaCTF 2018 Quals Writeup: The Colors - Listen!"
---

> Look at these images. See the music.
> 
> [left.png]({{ site.baseurl }}/ctfs/volgactf2018/colors/left.png) [right.png]({{ site.baseurl }}/ctfs/volgactf2018/colors/right.png)

The description hints at some music embedded in the images. Let's open one in ```StegSolve``` and extract its data as a binary file:

![savebin]({{ site.baseurl }}/ctfs/volgactf2018/colors/savebin.png)

The binary can then be imported into ```Audacity``` as raw data. Data plays as a melody, which confirms that it is a sound stream.

Switching to spectrogram view reveals the flag:

![spectrogram]({{ site.baseurl }}/ctfs/volgactf2018/colors/spectrogram.png)

The flag is ```VolgaCTF{SOUND_IS_3D_LIKE_IMAGE}```.