import struct, sys

d = bytearray(open("covfefe.exe","rb").read())[0xa08:]

print_trace = False

def pr(s):
	if print_trace:
		print "\t" + s

def int32(x):
	if x>0xFFFFFFFF:
		raise OverflowError
	if x>0x7FFFFFFF:
		x=int(0x100000000-x)
	if x<2147483648:
		return -x
	else:
		return -2147483648
	return x

def get(x):
	global d
	
	if x == 0x111:
		pr("error message location")
	if x == 0xefa:
		pr("0xefa - char to print")
	if x == 0xf0a:
		pr("0xf0a - ptr to char to print")
	if (x >= 0x4d and x <= 0x6c):
		pr("read input char #%x" % (x - 0x4d))

	v = struct.unpack("<I",d[x*4:x*4+4])[0]
	return v

def set(x,v):
	global d
	
	if (x == 4) and (v != 0):
		pr("request putc")
	if (x == 3) and (v != 0):
		pr("request getc")
	if (x == 2) and (v != 0):
		pr("set char to print")
	if (x == 1):
		pr("save read char")
		
	struct.pack_into("<I", d, x*4, v)
	
def step(p,a,b,c):
	
	ind = " "
	
	if (p >= 0xb50 and p <= 0xc77):
		ind = 'A'
	if (p >= 0xca1 and p <= 0xdc8):
		ind = 'B'
	if (p >= 0xa79 and p <= 0xab1):
		ind = 'C'
	if (p >= 0xaf6 and p <= 0xb32):
		ind = 'D'
		
	if p == 0xf1c:
		pr("check for null in string")
	if p == 0xeae:
		pr("print routine")
	if p == 0xb50:
		pr("begin loop A")
	if p == 0xc77:
		pr("end loop A")
	if p == 0xca1:
		pr("begin loop B")
	if p == 0xdc8:
		pr("end loop B")
	if p == 0xa79:
		pr("begin loop C")
	if p == 0xab1:
		pr("end loop C")
	if p == 0xaf6:
		pr("begin loop D")
	if p == 0xb32:
		pr("end loop D")
	
	aval = get(a)
	bval = get(b)
	v = (bval-aval)&0xffffffff
	
	
	set(b, v)
	
	jump = False
	if (c != 0):
		jump = (v == 0) or (v > 0x7FFFFFFF)
		
	if jump:
		jumpstr = "jmp %x" % c
	else:
		jumpstr = "next %x" % (p+3)
	pr("%c\t%x: %x(%x) %x(%x) =(%x) %x %s" % (ind,p,a,aval,b,bval,v,c,jumpstr))
	
	return jump

def run(end, start):
	global print_trace
	
	inpos = 0
	instr = "\x0a"
	
	for x in range(1,len(sys.argv)):
		if sys.argv[x] == "-t":
			print_trace = True
		else:
			instr = sys.argv[x] + "\x0a"

	p = start
	while (p + 3 <= end):
		if step(p,get(p),get(p+1),get(p+2)):
			if get(p+2) == 0xFFFFFFFF:
				return 1
			p = get(p+2)
		else:
			p += 3
			
		if get(4) == 1:
			sys.stdout.write(chr(get(2)))
			set(4, 0)
			set(2, 0)

		if get(3) == 1:
			
			set(1, ord(instr[inpos]))
			sys.stdout.write(instr[inpos])
			inpos += 1
			set(3, 0)
			
run(4352, 1123)
