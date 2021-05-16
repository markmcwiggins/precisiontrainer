#!/usr/bin/env python3

import re
import fileinput

for line in fileinput.input():
    if re.match('exp:', line):
        newline = re.sub('exp:', '<input type="hidden" value="%s" name="explanation/>', line)
        flds = newline.rstrip().split(':')
        what = ' '.join(flds[1:])
        print(newline % what)
    else:
        print(line,end='')
    


#if explanation:
#    fh.write('<input type="hidden" value="%s" name="explanation"/>' % explanation)
