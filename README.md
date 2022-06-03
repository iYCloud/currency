# currency
Currency conversion provider - programming task

# Problem
Imagine you're a currency conversion service provider. Through your service, your clients can make non-direct currency conversions. It means that your service is converting currency through a third currency (called intermediary currency). For example, when converting from EUR to USD, you're doing EUR to GBP and then GBP to USD conversion. Your service has to tell your clients which third currency to use. And as the world of finance is full of clever bankers/businessmen (like you in this exercise), you have managed to slip a clause in tiny font to your clients' contracts, allowing you to keep fractions of cents to yourself.

Write a program in Java that pulls currency rates from any free API on the internet (extra points for caching the results) and allows users to see which conversion is best for the service provider and the customer. Pick 10 of your favourite (or random) currencies and use only those. The user will select two currencies amongst the 10, the remaining 8 will be used as intermediary currencies.

Users must be able to insert the amount, „from currency“ and „to currency“ as input for the program.

Output:
The table displays the results for every intermediary currency that was used. Also in that table highlight:
1) which conversion is most profitable for your client.
2) which conversion is most profitable for you as the service provider

A graphical user interface is not mandatory, but the extra effort is rewarded with extra points

# Solution
Some kind of IDE to write and run the code with Java.

## Requirements
1) Access to the internet as API requests can not be made otherwise
2) Active API key requires registration and documentation, can be found at https://apilayer.com/marketplace/fixer-api
2) Maven dependency that is used for JSON, can be found at https://mvnrepository.com/artifact/org.json/json

## API keys
API keys can be acquired from - https://apilayer.com/marketplace/fixer-api.
After every 100 requests, you need to get a new API key. With Gmail, you can use `+` to reuse the same email:
```
email+apilayer1@gmail.com
email+apilayer2@gmail.com
email+apilayer3@gmail.com
...
```
### Available keys that still can be used
```
PzzhfP9DKFZrsPYOIfn3s3xJJt8UiGwS
0HpSi7GpxSf41uEpyxEvDM6vSNIhJRDI
IWZQ51jbJRjsTpRGDqaMbkB8pp9aHK9a
```
For Command line version: API key can be changed in the source code at line 97 or by looking for a keyword `apikey`.

## Command line version
Code is located in the `cli` directory.

### Sample output
```
Available currencies are:
[USD, EUR, JPY, GBP, AUD, CAD, CHF, HKD, NZD, SEK]

Enter currency you want to convert:
EUR

Enter amount you want to convert:
100

Enter currency you want to receive:
SEK

Please wait for the API request results...

INTERMEDIARY CURRENCY                   EUR TO INTERMEDIARY CURRENCY            INTERMEDIARY CURRENCY TO SEK            PROFIT!                                 
USD                                     107.5581                                1041.5499                               0.00024                                 
JPY                                     13969.597                               1041.5452                               0.005                                   
GBP                                     85.5034                                 1041.5508                               -0.00061                                
AUD                                     148.1823                                1041.55                                 0.00012                                 
CAD                                     135.15219                               1041.5503                               -0.00012                                
CHF                                     102.826706                              1041.5507                               -0.00049                                
HKD                                     843.7473                                1041.5504                               -0.00024                                
NZD                                     164.0073                                1041.55                                 0.00012                                 

Best for customer: GBP
Best for service provider: JPY

Process finished with exit code 0
```
## Servlet version

Code is located in the `servlet/code` directory.

Servlet directory contains `Dockerfile` and `README.md` to help and run including `ROOT.war`.

`ROOT.war` is built based on code from the `servlet/code` directory.
