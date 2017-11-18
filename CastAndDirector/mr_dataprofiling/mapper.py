#!/usr/bin/python

import sys

# input comes from STDIN (standard input)
for line in sys.stdin:
    line = line.strip()
    line = line.split(",")

    title = "-1"
    Director = "-1"
    actor1 = "-1"
    actor2 = "-1"
    actor3 = "-1"


    #If length of the line is four, the data comes from movie_actors
    if len(line) ==4:
        Director = line[0]
        actor1 = line[1]
        actor2= line[2]
        actor3 = line[3]

    else:
        title = line[1]
        Director = line[0]
    print ('%s\t%s\t%s\t%s\t%s' % (title, Director, actor1, actor2, actor3))