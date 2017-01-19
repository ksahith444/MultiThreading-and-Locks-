#! /usr/bin/python2.6
#! /usr/lib/python2
#! /usr/lib64/python2.6
#! /usr/include/python2.6 
import subprocess

"""
g = open("resultsFile","ab") 
for i in range(10, 101, 10):
    j = 2
    while j < 33:
    	for l in range(1,20,1):
		for m in range(1,20,1):
            		subprocess.call(["perf", "stat", "-e L1-dcache-loads,L1-dcache-load-misses,LLC-loads,LLC-load-misses", "java", "TTASBackoffLock", "%s" % j, "%s" % i, "%s" % l, "%s" % m],stdout=g,stderr=g) 
    	j = j * 2
"""
f = open("outputFiles","ab")
print("starting execution")
locks = ["TASLock", "TTASLock", "CLHLock", "TTASBackoffLock"]
for i in range(10, 101, 10):
    j = 2
    while j < 33:
         for k in locks:
	         subprocess.call(["perf", "stat", "-e L1-dcache-loads,L1-dcache-load-misses,LLC-loads,LLC-load-misses", "java", "%s" % k, "%s" % j, "%s" % i],stdout=f,stderr=f)
         j = j * 2
print("done executing")
