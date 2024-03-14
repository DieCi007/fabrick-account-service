# Run with docker
1. #### Update application.properties - insert correct api-key under `it.fabrick.server.apiKey`
2. #### cd into project root
3. #### Run ./run-with-docker.sh

# Example API call

### Account balance

``curl --location --request GET 'http://localhost:8080/api/v1/account/14537780/balance' \
--header 'api-key: FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP' \
--header 'Auth-Schema: S2S'``

### Account transfers

``curl --location --request GET 'http://localhost:8080/api/v1/account/14537780/transactions?from=2024-03-09&to=2024-03-13' \
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
