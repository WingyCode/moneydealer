# moneydealer
Rest Api endpoints list (examples assume that the server is run on localhost:7777):
GET: localhost:7777/accounts 
  - get the list of all accounts
GET: localhost:7777/accounts/1
  - get the account with id equals to 1
  
POST: localhost:7777/accounts
  payload example:
  {"name"="acctname", "money"="1000.50", "blocked"=false}
  
  - create a new account

DELETE: localhost:7777/accounts/1
  - delete the account with id equals to 1
  
PUT: localhost:7777/accounts/1
  payload example:
  {"name"="newname", "money"="100.50", "blocked"=true}
  
  - update the account with id equals to 1
  
PUT: localhost:7777/accounts/1/lock
  - lock the account with id equals to 1
  
PUT: localhost:7777/accounts/1/unlock
  - unlock the account with id equals to 1
  
POST: localhost:7777/deal?sender=1&reciever=2&money=100
  - send 100$ from the account with id equals to 1 to the account with id equals to 2
