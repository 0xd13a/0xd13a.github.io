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

Let's fire-up [IDA Pro](https://www.hex-rays.com/products/ida/support/download_freeware.shtml) and open this executable in it. The first thing to do is to disable all exceptions and pass them on to the application. Go to ```Debugger > Debugger options...``` and click the ```Edit``` button. Then go through the list of exception, press ```Ctrl+E``` on each one and uncheck ```Stop program``` and check ```Pass to application```. Once done, may as well set this as the default (by selecting ```Windows > Save desktop...```):

![IDA1]({{ site.baseurl }}/ctfs/seccon2016/anti-debugging/ida-exceptions.png)

Now on to code analysis. Going through the strings we see that function ```sub_4012F0``` uses both the prompt for the password (```Input password >```) and the correct password message (```Your password is correct.```), so let's concentrate on it.

We will set the breakpoint at the password prompt and run to it:

![IDA1]({{ site.baseurl }}/ctfs/seccon2016/anti-debugging/ida1.png)

So far so good. Stepping through we enter an arbitrary password at the password prompt and get to the junction where if the password were correct a success message would be displayed:

![IDA2]({{ site.baseurl }}/ctfs/seccon2016/anti-debugging/ida2.png)

```sub_402045``` compares the password that we entered with the expected password. Examination of ```[ebp+var_74]``` reveals what the password should be:

![IDA3]({{ site.baseurl }}/ctfs/seccon2016/anti-debugging/ida3.png)

Just in case user-entered password is relied on later upon, let's restart the application and supply the right password.

As we step through we now get the correct password message:

```
Input password >I have a pen.
Your password is correct.
```

Now we hit the code that checks for presence of debugger, VM and reverse engineering tools:

![IDA4]({{ site.baseurl }}/ctfs/seccon2016/anti-debugging/ida4.png)

For all of these checks we do the same trick - we allow the check to execute and then set Instruction Pointer to the branch that would be taken as if the check didn't find anything. For example in the picture above we run to line ```004013D3``` and then use ```Set IP``` command to set the current IP to ```004013E9```.

Eventually we get to the following location that will cause a Divide by Zero exception. To ignore it simply use the same ```Set IP``` command to not execute the ```idiv``` instruction:

![IDA5]({{ site.baseurl }}/ctfs/seccon2016/anti-debugging/ida5.png)

When we get to ```0040165D``` we seem to be taking the branch to exit. Let's go to ```00401663``` instead, especially since it references what looks like an encoded flag. ;-)

![IDA6]({{ site.baseurl }}/ctfs/seccon2016/anti-debugging/ida6.png)

As we look further down the line we see interesting code that displays a message box, and nothing between the current location and that location that checks for a debugger. Let's set a breakpoint there and run to it:

![IDA7]({{ site.baseurl }}/ctfs/seccon2016/anti-debugging/ida7.png)

Once the ```MessageBoxA``` call is executed we get the flag - ```SECCON{check_Ascii85}```:

![Solution]({{ site.baseurl }}/ctfs/seccon2016/anti-debugging/bin_solution.png)
