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
-   records of the same phone number don't need to be consecutive.
-   if same phone number owner changes prepaid/postpaid plans the PHONE_NUMBER,ACTIVATION_DATE consecutive.
-   one phone number has maximum one row has empty DEACTIVATION_DATE

`the problem Find the actual activation date of phone number`
change `find activation date of last owner of phone number`.

## Algorithm
```
    
```
## simple implement python

## go implement with optimate time complexity

## flow-up

## conculation
