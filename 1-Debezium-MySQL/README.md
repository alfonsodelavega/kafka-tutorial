# Conector Debezium para MySQL

1. Creamos los contenedores. Desde el directorio que contiene este README, ejecutamos:

```bash
docker-compose up -d
```

2. Comprobamos que todos los contenedores se encuentran correctamente arrancados mediante Docker Desktop o ejecutando `docker ps`

3. Enlazamos el conector Debezium con la instancia de MySQL mediante el siguiente comando (requiere de `curl` instalado en el sistema):

```bash
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d '{ "name": "inventory-connector", "config": { "connector.class": "io.debezium.connector.mysql.MySqlConnector", "tasks.max": "1", "database.hostname": "mysql", "database.port": "3306", "database.user": "debezium", "database.password": "dbz", "database.server.id": "184054", "database.server.name": "inventory", "topic.prefix": "dbserver1", "database.include.list": "inventory", "schema.history.internal.kafka.bootstrap.servers": "kafka:9092", "schema.history.internal.kafka.topic": "schemahistory.inventory", "skipped.operations" : "t"} }'
```

4. Comprobamos que se ha creado correctamente el conector:

```bash
curl -H "Accept:application/json" localhost:8083/connectors/
```

5. Inspeccionamos sus detalles

```
curl -i -X GET -H "Accept:application/json" localhost:8083/connectors/inventory-connector
```

6. Comprobamos los topics existentes en Kafka tras realizar la conexión

```
kafka-topics.sh --bootstrap-server localhost:9093 --list
```
