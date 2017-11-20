---
layout: page
title: "HXP 2017 Writeup: irrgarten"
---

> irrgarten
> 
> Be ready for some long running in the irrgarten.
>
> Connection:
> 
> dig -t txt -p53535 @35.198.105.104 950ae439-d534-4b0c-8722-9ddcb97a50f6.maze.ctf.link
> 
> 100 Basepoints + 100 Bonuspoints * min(1, 3/35 Solves) = 108 Points

When we run the command it returns a TXT record suggesting the we try ```down.<domain>```. When we run the following command we get a record of another subdomain:

```
dig -t txt -p53535 @35.198.105.104 down.950ae439-d534-4b0c-8722-9ddcb97a50f6.maze.ctf.link
```

It seems that the nameserver is set up as a maze that we can traverse by specifying directions. If the direction exists for that particular node a name of the new subdomain node is returned. I was not sure what the valid directions were, but ```up```, ```down```, ```right```, and ```left``` seemed to work.

By specifying all directions on all nodes that we see we should eventually stumble upon a flag. When we see a new node we will add it to the "fresh" list, so that we could explore it. As we check all direction on a node we will add it to the "seen" list so that we won't have to check it again. Let's put this logic in a script:

```python
import subprocess
import re

# new nodes
fresh = {}

# nodes that were already seen
seen = {}

def run_command(command):
	p = subprocess.Popen(command.split(),stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
	return iter(p.stdout.readline, b'')

def run(node,dir=None):
	global fresh, seen

	# mark the node as seen
	seen[node] = True

	dir_str = ""
	if dir != None:
		dir_str = dir + "."
	
	out = run_command("dig -t txt -p53535 @35.198.105.104 {}{}.maze.ctf.link".format(dir_str, node))
	in_answer = False
	for x in out:
		if ";; ANSWER SECTION" in x:
			in_answer = True
			continue
		if ";; AUTHORITY SECTION:" in x:
			break

		# if we are in the answer section and the line is not empty...
		if in_answer and x.strip() != "":
			val = x.strip()
			
			# extract the new node name
			m = re.search(r'IN CNAME\s+([0-9a-f-]{36}).maze.ctf.link.', val)
			if m:
				new_node = m.group(1)
				# if we have not seen this node - add it to the "fresh" list
				if new_node not in seen:
					print "Adding node " + new_node
					fresh[new_node] = True
			else:
				# this is not a line containing new node - let's print it just in case it has the flag
				print val
		
def explore(node):		
	print "Exploring " + node
	run(node)
	run(node,'down')
	run(node,'up')
	run(node,'left')
	run(node,'right')

fresh['950ae439-d534-4b0c-8722-9ddcb97a50f6'] = True
while True:
	
	next_key = next(iter(fresh))
	fresh.pop(next_key)
	explore(next_key)

	if len(fresh) == 0:
		break
```

The script runs for a long while but eventually the flag is returned:

```
...
Adding node 20b3aaad-20c0-4da5-9ff1-f5c98fe23efc
Exploring 91e3cc82-ced7-4150-8eeb-0374c85fcb68
Adding node 4206714c-494f-4da4-aab7-cb0e523fd4ce
Exploring 4206714c-494f-4da4-aab7-cb0e523fd4ce
Adding node 20b3aaad-20c0-4da5-9ff1-f5c98fe23efc
Exploring 20b3aaad-20c0-4da5-9ff1-f5c98fe23efc
Adding node 8f3bb677-b8f8-4c48-a4c4-d8451f361d03
8f3bb677-b8f8-4c48-a4c4-d8451f361d03.maze.ctf.link. 3600 IN TXT	"Flag:" "hxp{w3-h0p3-y0u-3nj0y3d-dd051n6-y0ur-dn5-1rr364r73n}"
Exploring 8f3bb677-b8f8-4c48-a4c4-d8451f361d03
...
```

The flag is ```hxp{w3-h0p3-y0u-3nj0y3d-dd051n6-y0ur-dn5-1rr364r73n}```.