#!/usr/bin/env python3

import sys
import os
import time
from datetime import datetime


fyle = sys.argv[1]

fopen = open(fyle)


n = 0
lenz = []
for line in fopen:
    n += 1
    (suit, cards) = line.split()
    if cards == 'void':
        lenz.append(0)
    else:
        lenz.append(len(cards))
    if n == 4:
        break


lensort = sorted(lenz)
print(lensort)
revlen = lensort[::-1]
print(revlen)
dirstr = ''
n = 0
for k in revlen:
    if n > 0:
        dirstr += '-'
    dirstr += str(k)
    n += 1

print(dirstr)
try:
    os.mkdir('../data/' + dirstr)
except:
    pass



os.rename(fyle,'../data/%s/%d.dat' % (dirstr,datetime.timestamp(datetime.now())))

time.sleep(1)

