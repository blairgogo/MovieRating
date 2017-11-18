#!/usr/bin/python

import sys

director_dict ={}
actors_dict={}
for line in sys.stdin:
    line = line.strip().split('\t')
    title, Director, actor1, actor2, actor3 = line


    #if actor1 ==-1, it means the data comes from directordata
    if actor1 == "-1":
        director_dict[Director] = title
    
    #Oterwise, the data comes from customer data

    else:
 
        actors_dict[actor1] = [actor2,Director,actor3]
#print(director_dict,actors_dict)

for actor1 in actors_dict.keys():
    try:
        title = director_dict[actors_dict[actor1][1]]
        actor2 = actors_dict[actor1][0]
        actor3 = actors_dict[actor1][2]
        print('%s\t%s\t%s\t%s' % (actor1, actor2, title, actor3))
    except:
        pass