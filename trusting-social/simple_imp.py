import sys

def read(file=sys.stdin):
    res = dict()
    i = 0
    for line in file:
        if i != 0:
            phone, activate, deactivate = line.rstrip().split(',')
            if phone not in res:
                res[phone] = []
            res[phone].append((activate, deactivate))
        i += 1
    return res

def processPhone(arr=[]):
    arr.sort()
    activateDate, _ = arr[-1]
    for i in range(len(arr) - 2, 0, -1):
        a, d = arr[i]
        if activateDate == d:
            activateDate = a
        else:
            return activateDate
    return arr[0][0]

if __name__ == '__main__':
    mapPhone = read()
    for k, v in mapPhone.items():
        activationDate = processPhone(v)
        print('{}, {}'.format(k, activationDate))
