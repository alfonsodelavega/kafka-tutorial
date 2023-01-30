# Ejemplo de productores y consumidores en Kafka

En este ejemplo vamos a ver como lanzar un contenedor Docker que ejecuta un broker Kafka, cómo crear topics en dicho broker, y cómo asociar productores y consumidores a dichos topics (tanto desde consola como desde programas sencillos en Java).

## Creación y lanzamiento de Kafka usando Docker

1. Abre una terminal y accede a la carpeta donde se encuentre este documento.
2. Ejecuta el siguiente comando: `docker-compose up -d`.

Este comando utiliza el fichero de configuración `docker-compose.yml` para crear una serie de contenedores. En concreto, se crean dos contenedores:

- `zookeeper`: es un programa que utiliza Kafka internamente para gestionar los distintos brokers y la localización de sus datos y metadatos (aunque este ejemplo solo contenta un broker de kafka, ya sabemos que puede haber multitud de ellos trabajando al unísono para proporcionar replicación/particionado de topics). No interactuaremos directamente con este contenedor.
- `kafka`: el propio nodo de Kafka contra el que se conectarán los clientes.

3. Ejecuta `docker ps` (o utiliza Docker Desktop) para comprobar que ambos contenedores se han arrancado correctamente. Comprueba que el contenedor `kafka` oferta el puerto 9093 (al que se conectarán nuestros clientes).

## Productores / Consumidores desde consola

Los siguientes comandos permiten crear productores y consumidores lanzables desde una consola de nuestro equipo. Para ejecutar estos comandos, necesitamos descargarnos los scripts que se incluyen junto con la distribución de Kafka. Puedes encontrar esta distribución en [este enlace](https://kafka.apache.org/downloads). **Elige la versión de Scala 2.13, en Kafka 3.3**.

1. Descomprime el archivo y abre una terminal en el directorio `bin`de la carpeta descomprimida.
2. Comenzaremos por crear el topic `testtopic` en el que nuestros consumidores trabajarán. Ejecuta el siguiente comando:

`./kafka-topics.sh --bootstrap-server localhost:9093 --create --topic testtopic `

3. Comprueba que se ha creado el topic correctamente:

`./kafka-topics.sh --bootstrap-server localhost:9093 --list`

4. De momento no utilizaremos más este comando, pero puedes comprobar la ayuda para ver todas las funciones proporcionadas:

`./kafka-topics.sh --bootstrap-server localhost:9093 --help`

5. Ahora que tenemos el topic creado, vamos a iniciar un consumidor que muestre todos los mensajes mandados:

`/kafka-console-consumer.sh --bootstrap-server localhost:9093 --topic testtopic`

6. Abre otra terminal en el mismo directorio. En esta terminal vamos a crear un productor, que nos permitirá enviar un mensaje al nuevo topic cada vez que escribamos algo y demos al intro:

`./kafka-console-producer.sh --bootstrap-server localhost:9093 --topic testtopic`

Nos debería aparecer un prompt `>`, indicándonos que podemos escribir el mensaje. Al escribirlo y pulsar la tecla intro, dicho mensaje debería aparecer en la terminal del consumidor.

7. Escribe varios mensajes desde el productor. Después, crea un nuevo consumidor, esta vez con este comando (que varía solamente al final). ¿Qué sucede?

`/kafka-console-consumer.sh --bootstrap-server localhost:9093 --topic testtopic --from-beginning`

## Productores / Consumidores desde Java

El repositorio incluye un proyecto de Java/Maven con un productor y un consumidor (Productor.java y Consumidor.java respectivamente). Importa el proyecto dentro del IDE de tu preferencia (creado con Eclispe, pero algo como IntelliJ Idea debería funcionar también) y estudia/ejecuta los programas.

## Extra: Combinación de productores/consumidores

Combina productores y consumidores de consola y de Java, para que veas que funcionan indistintamente unos con otros, siendo el punto de unión el broker Kafka.
