---
layout: page
title: "SECCON 2016 Online CTF Writeup: Anti-Debugging"
---

> 100 points
> 
> Reverse it.
>
> bin
>
> may some AV will alert,but no problem.

The file attached to the challenge - ```bin``` - seems to be a 32-bit Windows executable:

```
C:\sc>file bin
bin: PE32 executable (console) Intel 80386, for MS Windows
```

When running it from command line we get a password prompt:

```
C:\sc>bin
Input password >a
password is wrong.
```

As the name of the challenge suggests, we will try to reverse it and will likely have to deal with anti-debugging measures in the executable.

Let's fire-up [IDA Pro](https://www.hex-rays.com/products/ida/support/download_freeware.shtml) and open this executable in it. The first thing to do is to disable all exceptions and pass them on to the application. Go to ```Debugger > Debugger options...```, click the ```Edit``` butoon. Then go through the list of exception, press ```Ctrl+E``` on each one and uncheck ```Stop program``` and check ```Pass to application```. Once done, may as well set this as the default (by selecting ```Windows > Save desktop...```).



