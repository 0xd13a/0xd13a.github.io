---
layout: page
title: "Sharif CTF 2018 Writeup: Fifteen Puzzle"
---

> 150 
> 
> Fifteen Puzzle
> 
> You are given 128 puzzles (https://en.wikipedia.org/wiki/15_puzzle)
> The ith puzzle determines the ith bit of the flag:
> * 1 if the puzzle is soluble
> * 0 if the puzzle is unsoluble
>
> Implement is_soluble() below, and use the code to get the flag!
> 
>      def is_soluble(i):
>           return 0
>
>      flag = ' '
>
>      for i in range(128):
>           flag = ('1' if is_soluble(i) else '0') + flag
>      {% raw %}
>      print('SharifCTF{%016x}' % int(flag, 2))
>      {% endraw %}
> 
> [Download]({{ site.baseurl }}/ctfs/sharif2018/fifteen-puzzle/puzzles.txt)

This was a fairly easy problem to solve, but the right flag took forever to submit. What I believe was happening is the bits on the flag in the system were inverted, but the admins were adamant that everything was correct. :smile: Whatever, all is well that ends well.

There are many algorithms available on the Web that implement solvability for 15 Puzzle. We can take the one from [here](https://github.com/BrunoGomesCoelho/15-puzzle-problem/blob/master/utils.py) and modify it.

Let's add the code to parse the file to it and we are done:

```python
N = 4

def isSolvable(array):

	inversions = 0
	for i in range(N*N-1):
		for j in range(i+1, N*N):
			if array[i] != 0 and array[j] != 0 and array[i] > array[j]:
				inversions += 1

	zero_pos = -1
	for i in range(N*N):
		if array[i] == 0:
			zero_pos = i / N
			break

	return (zero_pos % 2 == 0 and inversions % 2 == 1) or (zero_pos % 2 == 1 and inversions % 2 == 0)

flag = ""
puzzle = []

for x in open("puzzles.txt","r").readlines():

	# parse values into an array
	if x[0] == '|':
		values = x.split('|')[1:5]

		for i in range(N):
			val = values[i].strip()
			if len(val) == 0:
				puzzle.append(0)
			else:
				puzzle.append(int(val)) 
		
	if len(puzzle) == N*N:
		# we are inverting bits here to make the flag what the organizers expect :)
		flag = ('0' if isSolvable(puzzle) else '1') + flag
		puzzle = []
		
# the format mask needs to be %032x instead of %016x
{% raw %}
print 'SharifCTF{%032x}' % int(flag, 2)
{% endraw %}
```

The flag is ```SharifCTF{52d3b36b2167d2076b06d8101582b7af}```.