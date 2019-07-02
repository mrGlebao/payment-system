# payment-system
Revolut test for role backend

### To start server:
* mvn clean package
* java -jar .\payment-system-1.0-SNAPSHOT-shaded.jar

### Available endpoints
* GET /id - receive unique id for transfer request
* PUT /transfer - transfer money. Expected body:
```
{
	"id": number, transaction id. (/id)
	"from": number, id of payer
	"to": number, id of receiver
	"amount": decimal number, amount to transfer
}
```
There are 3 initial users with ids 1, 2, 3 in system. Each of them has 10$.

Their data is stored in heap so changes are lost on restart.