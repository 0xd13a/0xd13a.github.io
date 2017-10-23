---
layout: page
title: "Pwn2Win 2017 Writeup: Resistance"
---

> Resistance
>
> For the infiltration, Case and Molly had to deal with the brain chip's electronics, which was extremely difficult to analyze. In order to pull this off, they asked you to help them calculate the equivalent resistance of a connection between two given weld spots, given the chip's description. The input is composed of an initial description of the circuit, where in each line the first two integers indicate the two points(weld spots) of a connection and the third indicates the resistance of such connection. For the following lines you must answer each one with another line containing only one number (with 3-digit precision), indicating the equivalent resistance of the connection between the two given weld spots.
>
>e.g.:
>
>Input:
>
>1 2 1
>
>2 3 1
>
>1 2
>
>2 3
>
>1 3
>
>Output:
>
>1.000
>
>1.000
>
>2.000
>
>Server: openssl s_client -connect programming.pwn2win.party:9001
>
>Id: resistance
>
> Total solves: 11
>
>Score: 347
>
>Categories: PPC-M

Implementing an algorithm for calculating resistances between two arbitrary points on a circuit board seems like a complex problem to solve in a short period of time. Luckily, [someone already solved it](https://gist.github.com/aelguindy/1747940) (and in Python too!) :smile: The only thing that is left is to add scaffolding that parses the inputs, and collects and returns the answers:

```python
# portion of the code "borrowed" from https://gist.github.com/aelguindy/1747940

import ssl, socket

def gauss_jordan(m, eps = 1.0/(10**10)):
  """Puts given matrix (2D array) into the Reduced Row Echelon Form.
     Returns True if successful, False if 'm' is singular.
     NOTE: make sure all the matrix items support fractions! Int matrix will NOT work!
     Written by Jarno Elonen in April 2005, released into Public Domain"""
  (h, w) = (len(m), len(m[0]))
  for y in range(0,h):
    maxrow = y
    for y2 in range(y+1, h):    # Find max pivot
      if abs(m[y2][y]) > abs(m[maxrow][y]):
        maxrow = y2
    (m[y], m[maxrow]) = (m[maxrow], m[y])
    if abs(m[y][y]) <= eps:     # Singular?
      return False
    for y2 in range(y+1, h):    # Eliminate column y
      c = m[y2][y] / m[y][y]
      for x in range(y, w):
        m[y2][x] -= m[y][x] * c
  for y in range(h-1, 0-1, -1): # Backsubstitute
    c  = m[y][y]
    for y2 in range(0,y):
      for x in range(w-1, y-1, -1):
        m[y2][x] -=  m[y][x] * m[y2][y] / c
    m[y][y] /= c
    for x in range(h, w):       # Normalize row y
      m[y][x] /= c
  return True


def preprocess(edges, V):
    lists = []
    for i in range(V): lists.append([])
    for fro, to, res in edges:
        lists[fro].append((to, res))
        lists[to].append((fro, res))
    return lists

def make_eqns(lists, node1, node2):
    coeffs = []
    Vars = len(lists)
    for i, l in enumerate(lists):
        cs = [0.0]*Vars
        cs[i] = sum(1/b for (a, b) in l)
        for other, res in l:
            cs[other] -= 1.0/res
        coeffs.append(cs)
    rhs = [0] * Vars
    rhs[node1] = 1.0
    rhs[node2] = -1.0
    n = max(node1, node2)
    coeffs = [c[:n] + c[n + 1:] for c in coeffs]
    return coeffs[:-1], rhs[:-1]

def calculate_resistance(nodes, edges, source, destination):
    src = source
    dst = destination
    ls = preprocess(edges, nodes)
    a, b = make_eqns(ls, src, dst)
    M = [a[i] + [b[i]] for i in range(len(a))]
    gauss_jordan(M)
    return abs(M[min(src, dst)][-1])
	
	
class Connect(object):
    def __init__(self, host, port):
        self.context = ssl.create_default_context()
        self.conn = self.context.wrap_socket(
            socket.socket(socket.AF_INET),
            server_hostname=host)
        self.conn.connect((host, port))
        self.f = self.conn.makefile('rwb', 0)
    def __enter__(self):
        return self.f
    def __exit__(self, type, value, traceback):
        self.f.close()

dim = 0
conns = []
results = []


with Connect('programming.pwn2win.party', 9001) as f:
	for line in f:
		line = line.strip()
		tuple = line.split()
		print line
		
		# parse resistances
		if len(tuple) == 3:
			conn = (int(tuple[0])-1,int(tuple[1])-1,float(tuple[2]))
			conns.append(conn)
			if conn[0]+1 > dim:
				dim = conn[0]+1
			if conn[1]+1 > dim:
				dim = conn[1]+1
		# parse paths
		if len(tuple) == 2:
			start = int(tuple[0])-1
			end = int(tuple[1])-1
			if start == end:
				# resistance from point to itself is 0
				val = 0
			else:
				val = calculate_resistance(dim, conns, start, end)
			results.append(val)		

		if line == "":
			print "-end of data-"
			
			for x in results:
				val = "%.3f" % x
				f.write(('%s\n' % val).encode('utf-8'))
				print val
				
			dim = 0
			conns = []
			results = []
```

The flag is ```CTF-BR{Us1n6_R3s1S73NCe_d1s7AnCe_15_3x7R3Me1y_us3ful1_in_p8ysIC5}```