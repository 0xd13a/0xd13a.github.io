---
layout: page
title: "SECCON 2016 Online Writeup: VoIP"
---

> VoIP
>
> 100 points
> 
> Extract a voice.
>
> The flag format is SECCON{[A-Z0-9]}.
>
> voip.pcap 

As usual we start by opening the PCAP file in [Wireshark](https://www.wireshark.org/). A quick Google search reveals that there is a functionality in Wireshark that deals specifcally with VoIP calls - it's under ```Telephony > VoIP Calls```. Selecting the command shows us the embedded voice stream and an option to play it.

Let's click Play Streams:

This gives us two streams to play, clicking the Play button on the first one reads the flag aloud through what sounds like machine-generated voice:

The flag is ```SECCON{9001IVR}```.


