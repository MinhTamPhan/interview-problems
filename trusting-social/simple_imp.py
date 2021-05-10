def read(fileName):
    f = open(fileName)
    res = []
    i = 0
    for line in f:
        if i != 0:
            p, a, d = line.split(',')
            res.append((p, a, d))
        i += 1
    res.sort()
    return res

sortedList = read('./input.txt')
p, a, d = sortedList[0]
m[p] = a
for i in range(1, len(sortedList):
        p1, a1, d1 = sortedList[i]
        if p1 != p:
            m[p] = a
            p, a, d = p1, a1, d1
        else:
            if d != a1: # grab
                a, d = a1, d1
fOut = 'output.txt'
for k, v in m:
    print('{},{}'.format(k, v), file=fOut)

