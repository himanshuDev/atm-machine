# ATM Simulation App
This application simulates the working of an ATM. Once successfully up and running it provides the APIs for

1. View ATM inventory
   /atm/api/atm-inventory
2. View Bank Accounts
   /atm/api/account-inventory
3. View Balance of an account holder
   /atm/api/balance
4. Withdraw money from ATM
   /atm/api/balance/withdraw

## Description
This project uses following features to achieve the desired functionality.

1. Aspects for Logging and time tracing.
2. Spring Based Exceptional Handling
3. Swagger 3.0 Open API
4. Javax Validation
5. Internally Uses H2 database for simulating Run time DB.
6. JUnit 5 Based test Cases.
7. Jacoco Code Coverage
8. jasypt-spring-boot for Encryption. [For one way PIN encryption]

## Getting Started
Perform the following steps to run this on local

### Dependencies
Following dependencies must be installed.
1. Maven
2. Java 11

### Installing
To run this repo on local

1. clone this repo 
  <br>**$ git clone https://github.com/himanshuDev/atm-machine.git**

2. Once cloned, Go inside the atm-machine directory and execute
  <br>**$ mvn clean install**
  <br>This will build the project

3. Once project is build, Copy the property file 
<br>[https://github.com/himanshuDev/atm-machine/blob/main/src/main/resources/application.properties]
   in a 
<br>temp folder and modify the below property to put the logs at yours system desired folder
<br>**logging.file.path= path_to_generate_log_file**<br>
   example:<br>
   **logging.file.path= /Users/Work/atm-machine/logs**<br>
   this should be a valid path.

4. to execute the project, provide the following command.<br>
   **$ java -jar ~/atm-machine/target/atm-machine-1.0.0.jar --spring.config.location=file:///Users/...../application.properties**
              
5. Once the application is up and running, open chrome and put below url in the address bar.<br>
   http://localhost:8080/atm-machine-api.html<br>
   This is swagger API docs.
   
6. You can try the APIs directly from there or use below curl command to test the application<br>

  &nbsp;&nbsp;   **Get list of all accounts [audit feature]**<br>

  &nbsp;  $ curl -X 'GET' \
  'http://localhost:8080/atm/api/account-inventory' \
  -H 'accept: application/json'

 &nbsp;&nbsp;   **Get list of atm inventory** [audit feature] 

  &nbsp;  curl -X 'GET' \
  'http://localhost:8080/atm/api/atm-inventory' \
  -H 'accept: application/json'

 &nbsp;&nbsp;   **Check balance for a user**

  &nbsp;&nbsp;   curl -X 'POST' \
  'http://localhost:8080/atm/api/balance' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "pin": "1234",
  "userName": "clint_west"
   }'

  &nbsp;&nbsp;   **With draw money from ATM.**

  &nbsp;&nbsp;   curl -X 'POST' \
  'http://localhost:8080/atm/api/withdraw' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "userName": "clint_west",
  "pin": "1234",
  "withDrawlAmount": 100,
  "useOverDraft": true
  }'


## Code Coverage
This project uses Junit5, mockito and Jacoco to provide the unit testing.
Unit tests are extensively done for service module only as of now.
To view the code coverage use the following commands

$ mvn clean test

or

$ mvn clean install site --offline

Code coverage reports can be found at.

~/atm-machine/target/site/jacoco/index.html
~/atm-machine/target/site/jacoco/com.abcbank.service.bussiness.bankacc/BankAccountServiceImpl.html
~/atm-machine/target/site/jacoco/com.abcbank.service.bussiness.atm/ATMServiceImpl.html


## Authors

Contributor names and contact info
Himanshu Upadhyay [himanshu.udhyay@gmail.com]

## Version History

* 1.0
    * Initial Release

## License

This project is licensed under the MIT License - see the LICENSE.md file for details
