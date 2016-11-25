---
layout: page
title: RC3 CTF 2016 Writeup: Graphic Design
---

> 200 points
>
> The 3D Design students have been boasting about how they can trade sensitive information without anyone ever knowing. You’ve intercepted one of their USB’s and found this interesting file. Figure out what the hell is going on.
> 
> Download Link: https://drive.google.com/file/d/0Bw7N3lAmY5PCTWg5YU1uNUk3cmc/view?usp=sharing
>
> -Your friendly neighborhood httpster

The archive attached to this challenge - ```forensics200.zip``` - contains another file - ```forensics200.obj```. A quick look inside indicates that it is a [Blender](https://www.blender.org/) file:

```
# Blender v2.78 (sub 0) OBJ File: ''
# www.blender.org
mtllib forensics200.mtl
o def_not_the_flag_Text.002
v 2.131841 14.053224 -7.976235
v 1.879015 13.982867 -7.720026
...
```

The file does not seem to contain any other clues, so let's install Blender and open the file in it. When the file is loaded we see a 3D model of a Stegosaurus:

![Stegosaurus]({{ site.baseurl }}/ctfs/rc3ctf2016/graphic-design/blender.png)

So far there is nothing interesting so let's explore a little. As we move around the model using [navigation keys](https://wiki.blender.org/index.php/Doc:2.4/Manual/3D_interaction/Navigating) we finally see flag ```RC3-2016-St3GG3rz``` hidden inside the Stegosaurus:

![Flag]({{ site.baseurl }}/ctfs/rc3ctf2016/graphic-design/flag.png)
