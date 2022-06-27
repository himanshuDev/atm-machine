# ATM Machine App
This application simulates the working of on an atm. Once successfully up and running it provides the APIs for 

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
5. Intenally Uses H2 database for simulating Run time DB.
6. JUnit 5 Based test Cases.
7. Jacoco Code Coverage
8. jasypt-spring-boot for Encrytpion. [For one way PIN encryption]

## Getting Started
Perform the following steps to run this on local

### Dependencies
Following dependencies must be installed.
1. Maven
2. Java 11

### Installing
To run this repo on local
1. clone this repo
   $ git clone https://github.com/himanshuDev/atm-machine.git
2. Once cloned, execute go inside the atm-machine directory and execute
   $ mvn clean install
   This will build the project
3. Once project is build, Copy the property file [https://github.com/himanshuDev/atm-machine/blob/main/src/main/resources/application.properties]
   in a temp folder and modify the property to put the logs at your's sysem desired folder
   logging.file.path= path_to_generate_log_file
   example:
   logging.file.path= /Users/Work/atm-machine/logs
   this should be a valid path.
4. to execute the project, provide the following command.
   $ java -jar ~/atm-machine/target/atm-machine-1.0.0.jar --spring.config.location=file:///Users/...../application.properties
                path to jar [build in step 2]                                      spring property externalized file.   
5. Once the application is up and running, open chrome and put below url in the address bar.
   http://localhost:8080/atm-machine-api.html
   This is swagger API docs.
6. You can try the APIs directly from there or use below curl command to test the application

  6.1 Get list of all accounts [audit feature]
    
   curl -X 'GET' \
  'http://localhost:8080/atm/api/account-inventory' \
  -H 'accept: application/json'
  
  6.2 Get list of atm inventory [audit feature]
  
   curl -X 'GET' \
  'http://localhost:8080/atm/api/atm-inventory' \
  -H 'accept: application/json'
   
  6.3 Check balance for a user
  
   curl -X 'POST' \
  'http://localhost:8080/atm/api/balance' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "pin": "1234",
  "userName": "clint_west"
   }'
   
   6.4 With draw money from ATM.
   
   curl -X 'POST' \
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
This proejct uses Junit5, mockito and Jacobo to provide the unit testing.
Unit tests are extensivly done for service module only as of now. 
To view the code coverage use the following commands

$ mvn clean test

or 

$ mvn clean install site --offline

Code coverage reports can be found at.

~/atm-machine/target/site/jacoco/index.html
~/atm-machine/target/site/jacoco/com.abcbank.service.bussiness.bankacc/BankAccountServiceImpl.html
~/atm-machine/target/site/jacoco/com.abcbank.service.bussiness.atm/ATMServiceImpl.html


## Authors

Contributors names and contact info
Himanshu Upadhyay [himanshu.udhyay@gmail.com]

## Version History

* 1.0
    * Initial Release

## License

This project is licensed under the MIT License - see the LICENSE.md file for details
