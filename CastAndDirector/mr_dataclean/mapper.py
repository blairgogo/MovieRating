import sys
import csv
reader = csv.reader(sys.stdin)
reader.next()
for line in reader:

    if len(line) == 5:
        title, Director, actor1, actor2, actor3 = line

        print ('%s\t%s' % (Director, title))