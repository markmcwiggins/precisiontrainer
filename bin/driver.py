#!/usr/bin/env python3

import os

for k in range(1,100000):
    os.system('./randhand.py >out.dat')
    os.system('./rightbin.py out.dat')
    print('-------->', k)
