---
layout: post
title: My Experience With OSCP Exam
draft: false
---

![OSCP]({{ site.baseurl }}/images/oscp/oscp.png)

This year I decided to try OSCP certification. It took a lot of effort but I passed the exam successfully last month.

From what I have seen OSCP and other Offensive Security certifications are fairly unusual - most other popular certifications are really "book" exams, something you can read, memorize, and maybe even cram for. OSCP on the other hand is as hands-on as you can get, and that gives it unique value. When you see "OSCP" next to someone's title you can tell that that person has real practical hacking skills, and haven't just read a book on the subject. This is not to say other certs are not valuable, but OSCP in my mind is in a class of its own.

Lots of blog posts have been written on the subject, and a lot of them helped me prepare (thank you to those who wrote them!). Below I'll share a few random thoughts on how I approached preparation for the exam and what advice I would have given my past self. 

Preparing for OSCP starts with the course and continues in the practical lab. The amount of time you spend there depends heavily on your schedule and past experience. I have a fair bit of pentesting and software engineering experience, so I decided to go with 2 months of lab time. My family commitments, however, made it hard to find more than a few hours of lab time every day. In the end I spent about 3 weeks going through the course and completing exercises, and then a little over a month in the lab. It was grueling, and I think I would have benefitted from more time in the lab, so if you have time and finances to extend the lab - take advantage of that.

I feel that ability to work in the lab is the most important part of OSCP, this is where you get you money's worth. It's a little counter-intuitive to say that, but to me it's more important than actually passing the exam - the skills you acquire and practice are what will matter in the long run. A person that learned a lot in the lab and maybe failed the first exam in the end will be better off than the one that didn't work hard in the lab and passed the exam by some lucky circumstance.

There are 3 areas of preparation that I concentrated on - mental, physical and technical.

## Mental

Mental prep for the exam is probably the most important one. When you feel calm and composed you can methodically grind all obstacles down. When you are frustrated and panicking your brain shuts down. Finding vulnerabilities requires patience, attention to detail, and creative thinking - and stress is a killer of these qualities.

Some people complain that the exam systems are harder to exploit than the ones in the lab - I didn't find that to be the case. That's not to say the exam was easy - I just found it _as hard_ to do as exploiting machines in the lab. I think the reason why exam seems harder to some is that in the lab you are relaxed and can calmly explore your targets, while during the exam you are stressed and under the gun to meet the deadline, which makes everything more difficult. Practicing calming myself down, and pursuading myself that even failing the test is not a big deal but rather yet another step in my learning helped me stay more relaxed and think more clearly.

The brain gets tired after a while so it is important to remove as many steps requiring mental energy from the exam as possible. Any time you need to write a script, research a code snippet, or look up a piece of information it drains your brain power a little - so doing as many of these things in advance as possible helps free mental energy for actual looking for vulnerabilities. I prepared a lot of scripts for running scan tools, building reverse shells, compiling exploits, and others; put together lists of commonly used code snippets; precompiled some of the exploits; and so on - all of which helped me think less about the operational side of things, and more about my targets.

Another method that helped was switching context and taking mental breaks. It is often recommended that if you hit a wall with one system, or spend an inordinate amount of time on it without progress (e.g. 2+ hours) you should switch to another system. This was very true for me, switching between systems helped keep things fresh and allowed me to take advantage of ["unconscious mind"](https://www.huffpost.com/entry/neuroscience-and-consciousness_b_3468999). Some of the breakthroughs that I had occurred when I put the problem I was trying to solve out of my mind completely and concentrated on something else.

## Physical

Much has been said about getting enough food, drink and rest during the exam - and it all makes sense. When your body is well nourished you can fire on all cylinders. 

Frequent rest is important, so taking a quick break to walk outside, or to lay down to rest definitely helps one recharge. I've found that [power naps](https://lifehacker.com/how-long-to-nap-for-the-biggest-brain-benefits-1251546669) do wonders for giving me an energy boost - as long as I do not sleep for too long and become groggy.

## Technical 

Here some of the things that I found on the technical side of the equation:

- *Host machine setup.* You have to make sure not only that your host machine OS is on the [list of supported OSes](https://www.offensive-security.com/faq/#proc-2), but also that it supports camera and can run _Java JNLP apps_. I was using a Kali VM on a Kali host (yes, I run Kali on Kali, 'coz that's how I roll), which is supported - and yet I had to scramble to set up my machine during the exam to get it to run JNLP apps - my Java version was too fresh and had that functionality disabled. In the end installing Iceweasel solved the issue.

- *Types of systems.* In the exam you will likely get a mix of Windows and Linux machines, some easier, some harder (which is reflected in the points awarded for each one). The systems I had reminded me a lot of the types of systems in the course and the lab, so don't expect something completely out of the ordinary. On the other hand some of the machines I had were at the very latest patch levels - in those cases kernel exploits are not an option, you have to look for a vulnerable application or a misconfiguration that you have to take advantage of. 

- *Enumeration.* It has been said that enumeration is key in the exam and I think that flows from the very nature of this course. We are trying to apply known exploits (with minor modifications) to vulnerable systems - and before you can do that you have to fully understand what exactly you are dealing with. You cannot exploit what you don't understand. Once you fully understand your target you give your brain enough "food" to chew on, ideas for different things to try start to flow.

- *Curve balls.* Of course the exam would be too easy if you could just apply a known exploit and call it a day. So do expect decoys, and vulnerabilities that are real but not fully exploitable. That's again why enumeration is so crucial - if you have a full picture of the system then you have a list of things that you can try, not just the most obvious, low hanging fruit. On one of the systems during the exam I found a very obvious vulnerability, and yet the last step - overwriting the file that I needed - was not possible. However, with the help of thorough recon I found a misconfiguration that I could combine with this vulnerability to take it in a whole other direction and _retrieve_ a file that allowed me to get shell. So be ready to get creative and combine different vulnerable pieces together. 

- *Go from simple to complex solutions.* Sometimes the vulnerability that you are looking for may be hiding on the surface so be methodical in trying all ideas, even the ones that seem too easy. Take a step back and think before jumping in. It maybe tempting to start bruteforcing the login - but maybe try a list of known default credentials first? You could start compiling and running kernel exploits, but first see if there is an application installed that should not normally be on the system and that is vulnerable? 

- *Weak areas.* Much has been said about extra practice for the skills that are your weakest - that rang very true for me during the exam. I knew that my Windows privesc skills needed more work but did not practice them enough - and (of course) got stuck with Windows escalation on 2 systems. If you know that you are weak at something - spend extra time practicing it.

- *Staying organized.* Keeping your research, notes, logs, and screenshots organized is absolutely critical. Human brain has a limited bandwidth and things can very easily be missed or forgotten. I found [KeepNote](http://keepnote.org/) invaluable for keeping track of everything - whenever I was investigating a machine I created a node in the document tree for it and created sub-notes for enumeration logs, code snippets, todo lists, and other information. Searching and taking snapshots was very easy from within the tool, and everything you enter is automatically saved. As I gathered evidence I added new ideas to the todo list for each machine and then came back to try them.

- *Documenting your work.* Do not cut corners on documenting all your steps, even if it feels that you are stating the obvious. Same goes for screenshots - take plenty, especially the ones that contain output of proof files and ```i[fp]config``` (also note that ```ifconfig``` may be missing on newer systems - use ```ip addr``` in that case). Before you finish the exam re-check that you have captured everything that you need for the report. You will be kicking yourself the next day if some crucial piece of evidence is missing. Overdocumenting seems like an overkill but actually reinforces a valuable skill - if you will be interacting with clients and colleagues in your career you will inevitably need to learn to communicate technical information in a clear and detailed way, with any claims you make backed up by evidence. 

- *Tools and resources.* The following are the tools and resources that I got the most value out of  during the exam and lab (of course lots of others were used as well, but these were the most helpful):
  
  #### Enumeration

  - **searchsploit** - This tool was invaluable during my exam. It helped me find most vulnerabilities that I used. Use it to look up identified software listening on ports, internal software installed on the system, OS components that are installed - _everything_.
  - **nmap** - Goes without saying...
  - **nikto**
  - [dirsearch](https://github.com/maurosoria/dirsearch)
  - **gobuster**
  
  #### Escalation
  
  - [unix-privesc-check](http://pentestmonkey.net/tools/audit/unix-privesc-check)
  - [linuxprivchecker.py](https://github.com/sleventyeleven/linuxprivchecker/blob/master/linuxprivchecker.py)
  - [Powerless.bat](https://github.com/M4ximuss/Powerless/blob/master/Powerless.bat)
  - [windows-privesc-check](https://github.com/pentestmonkey/windows-privesc-check)
  
  #### Guides
  
  - [Network penetration testing](https://guif.re/networkpentest)
  - [Linux elevation of privileges](https://guif.re/linuxeop)
  - [Basic Linux Privilege Escalation](https://blog.g0tmi1k.com/2011/08/basic-linux-privilege-escalation/)
  - [Windows elevation of privileges](https://guif.re/windowseop)
  - [Windows Privilege Escalation Fundamentals](http://www.fuzzysecurity.com/tutorials/16.html)


All in all it was a hard and stressful journey, but a very rewarding one. I'm already starting to miss the lab. :)
