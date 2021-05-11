## problem
Task 1 [Algorithm and coding]: Find the actual activation date of phone number

Statement:
Given a list of at most N = `50,000,000` records (in CSV format), each record describes an usage period of a specific mobile phone number. 

Note that one phone number can occurs multiple times in this list, because of 2 reasons:
-	This phone number can change from prepaid plan to postpaid plan, or vice versa, at anytime just by sending an SMS to the operator.
-	Or, the owner of this phone number can stop using it, and after 1-2 months, it is reused by another person.
Also remember that, the reason is not recorded in the data, we just have the phone number and its activation or deactivation date for a usage period record.
-	Activation date is the date that the phone number is started being used by a owner with a specific plan (prepaid or postpaid).
-	Deactivation date is the date that the phone number is stopped being used by a owner with the registered plan.

*Moreover, the records don't need to follow any specific order of time, and the records of the same number don't need to be consecutive. 

From the given data, we want to find a list of unique phone numbers together with the actual activation date when its current owner started using it. Note that what we need is the first activation date of current owner, not previous owner, and not the date when current owner changes prepaid/postpaid plans.

This is an example input:
```csv
PHONE_NUMBER,ACTIVATION_DATE,DEACTIVATION_DATE
0987000001,2016-03-01,2016-05-01
0987000002,2016-02-01,2016-03-01
0987000001,2016-01-01,2016-03-01
0987000001,2016-12-01,
0987000002,2016-03-01,2016-05-01
0987000003,2016-01-01,2016-01-10
0987000001,2016-09-01,2016-12-01
0987000002,2016-05-01,
0987000001,2016-06-01,2016-09-01
```
## Analyze 
-   Records of the same phone number don't need to be consecutive.
-   If same phone number owner changes prepaid/postpaid plans the ACTIVATION_DATE, DEACTIVATION_DATE must be consecutive.
-   One phone number has maximum one row has empty DEACTIVATION_DATE.
-   If same phone number there is not any `ACTIVATION_DATE, DEACTIVATION_DATE` overlap with another.
-   If same phone number change owner. It must be have a `gap` Between `DEACTIVATION_DATE` of previous owner and `ACTIVATION_DATE` current owner.

The problem `Find the actual activation date of phone number`
change `find activation date last owners of phone number`.

## Algorithm.
With the problem has change like the previous section. `Find activation date last owners of phone number`. I design algorithm to solve for find activation for specific phone number below:
-   `input`: `array` mean array tuples activation_date and deactivation_date
-   `output`: activation_date last owners of phone number.
```cpp
    func findActivation(array){
        sort(array) // sort accessing by composite key
        activateDate, deactivateDate = last(arr)
        for item in array from bottom to top {
            acti, deacti = item
            // mean changes prepaid/postpaid
            if (activateDate == deacti)
                activateDate = acti
            // mean has a gap
            else
                return activateDate
        }
        // mean not exist any gap
        return activateDate
    }
```
For resolve fully problem I design algorithm below:

```cpp
    func resolve(fileName, output){
        // read(fileName) func return map with
        // key=phone
        //value = array tuples activation_date and deactivation_date.
        mapActivities = read(fileName)
        for key, value in mapActivities.items():
            activationDate = findActivation(key)
            print('{}, {}'.format(k, activationDate), output)
    }
```

With this design
- time complexity `O(N * Mlog(M))`, `N` is number of unit phone number. `M` is maximum length array tuples activation_date and deactivation_date.
- space complexity `O(N)`, `N` total record in `file`
## simple implement python

## go implement with optimate time complexity

## flow-up

## conculation
