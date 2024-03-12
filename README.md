# Run with docker

1. #### cd into project root
2. #### Run ./run-with-docker.sh

# Example API call

### Account balance

``curl --location --request GET 'http://localhost:8080/api/v1/account/14537780/balance' \
--header 'api-key: FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP' \
--header 'Auth-Schema: S2S'``

### Money Transfer

``curl --location --request POST 'http://localhost:8080/api/v1/account/14537780/money-transfer' \
--header 'api-key: FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP' \
--header 'Auth-Schema: S2S' \
--header 'Content-Type: application/json' \
--data '{
"creditor": {
"name": "John Doe",
"account": {
"accountCode": "IT23A0336844430152923804660"
}
},
"executionDate": "2019-04-01",
"description": "Payment invoice 75/2017",
"amount": 800,
"currency": "EUR"
}'``


# In Memory DB
## See transaction requests
1. #### Navigate to http://localhost:8080/h2-console
2. #### Access using credentials:
   1. JDBC URL - jdbc:h2:mem:account
   2. User name - user
   3. Password - password
