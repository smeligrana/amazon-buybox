Per lanciare KAFKA è sufficiente utilizzare il comando docker-compose up

Il lancio dell'applicazione mvnw spring-boot:run cerca ed eventualmente crea i topic su kafka. 
Per testare lo scambio di messaggi è sufficiente utilizzare il comando 

curl -X POST http://localhost:8081/productslist/1W8UeooEtPwf3tykbbfe0JPIiu6UsNQz1Qic1hn2IVL8

che simula la sottomissione dell'id di uno sheet
