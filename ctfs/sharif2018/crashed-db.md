---
layout: page
title: "Sharif CTF 2018 Writeup: Crashed DB"
---

> 50
> 
> Crashed DB
> 
> We lost some data when we were delivering our DB.
> Can you recover it??
> 
> Hint: SQLite
> 
> [Download]({{ site.baseurl }}/ctfs/sharif2018/crashed-db/db0.db)

As the hint suggests we are dealing with a SQLite file. Let's try to open it:

```
$ sqlite3 db0.db
SQLite version 3.21.0 2017-10-24 18:55:49
Enter ".help" for usage hints.
sqlite> .tables
Error: file is not a database
sqlite> .dbinfo
unable to read database header
```

The file is damaged. When we do a comparison with a valid SQLite database file we can see that the header is missing. We can copy it using any binary editor:

![missing header]({{ site.baseurl }}/ctfs/sharif2018/crashed-db/missing-header.png)

After the modification the database can be read:

```
$ sqlite3 db0.db 
SQLite version 3.21.0 2017-10-24 18:55:49
Enter ".help" for usage hints.
sqlite> .tables
tbl
sqlite> select * from tbl;
0|S|ln?KxFjBA
3o78Gv0!N&b|h|h0EdNc#L08H
n6a$AvNIux?(|a|u
Lsoz6Y|r|u9C#1cKQ3x@
bWmk*JC|i|0P8Hy4Yc%g5Xzh
...
sqlite> .schema
CREATE TABLE tbl (Glaf varchar(15), Flag varchar(1), Lfag varchar(15));
```

The database seems to be full of garbage, but notice the middle column. Not only it is called ```Flag``` it seems to contain the flag, one character per row:

``` 
sqlite> select group_concat(Flag,'') from tbl;
SharifCTF{7d9ed4a5867f6bd376928a3ed7837a07}
```

The flag is ```SharifCTF{7d9ed4a5867f6bd376928a3ed7837a07}```.