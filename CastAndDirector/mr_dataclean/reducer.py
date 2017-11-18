from __future__ import division
from itertools import groupby
from itertools import groupby
from operator import itemgetter
import itertools
import groupby
import operator
import sys

def load_map(file):
    for line in file:
        yield line.strip().split("\t")
data = load_map(sys.stdin)

for key, group in groupby(data, itemgetter(0)):

    print('%s\t%s\t%s' % (key, len(list(group)), len(key)))
'''
oldDirector_name = None
moviesCount = 0
for line in sys.stdin:
    #print line 
    data_mapped = line.strip().split("\t")
    if len(data_mapped) != 2:
        # Something has gone wrong. Skip this line.
        continue

    thisDirector_name, thisMovie_title = data_mapped
    
    
    if oldDirector_name and (oldDirector_name != thisDirector_name): 
        oldDirector_name = thisDirector_name
        moviesCount = 0
    #print thisDirector_name
    oldDirector_name = thisDirector_name
    moviesCount = moviesCount+1
    print ('%s\t%s\t%d' % (oldDirector_name, moviesCount, len(oldDirector_name)))
'''