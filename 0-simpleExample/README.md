# Ejemplo de productores y consumidores en Kafka

En este ejemplo vamos a ver como lanzar un contenedor Docker que ejecuta un broker Kafka, cómo crear topics en dicho broker, y cómo asociar productores y consumidores a dichos topics (tanto desde consola como desde programas sencillos en Java).

## Creación y lanzamiento de Kafka usando Docker

1. Abre una terminal y accede a la carpeta donde se encuentra este documento.
2. Ejecuta el siguiente comando: `docker-compose up -d`. Si tenéis instalado Docker Desktop, deberíais tener ya disponible este comando. Si no, es posible que necesitéis [instalarlo aparte](https://docs.docker.com/compose/install/).

Este comando utiliza el fichero de configuración `docker-compose.yml` para crear una serie de contenedores. En concreto, se crean dos contenedores:

- `zookeeper`: es un programa que utiliza Kafka internamente para gestionar los distintos brokers y la localización de sus datos y metadatos (aunque este ejemplo solo contenga un broker de kafka, ya sabemos que puede haber multitud de ellos trabajando al unísono para proporcionar replicación/particionado de topics). No interactuaremos directamente con este contenedor.
- `kafka`: el propio nodo de Kafka contra el que se conectarán los clientes.

3. Ejecuta `docker ps` (o utiliza Docker Desktop) para comprobar que ambos contenedores se han arrancado correctamente. Es similar a `docker stats`, pero ofrece alguna información extra, como los puertos que el contenedor ofrece para su conexión con algún protocolo (e.g. tcp). Comprueba que el contenedor `kafka` oferta el puerto 9093 (al que se conectarán nuestros clientes).

## Productores / Consumidores desde consola

Los siguientes comandos permiten crear productores y consumidores lanzables desde una consola de nuestro equipo. Para ejecutar estos comandos, necesitamos descargarnos los scripts que se incluyen junto con la distribución de Kafka. Puedes encontrar esta distribución en [este enlace](https://kafka.apache.org/downloads). **Elige la versión de Scala 2.13, en Kafka 3.3**.

1. Descomprime el archivo y abre una terminal en el directorio `bin` de la carpeta descomprimida.
    - Todos los comandos que se muestran a continuación utilizan los scripts escritos en Bash y terminados en `.sh`, que requieren o bien usar sistemas Linux o Mac, o instalar el entorno WSL2 en Windows, que es posible que ya tengáis por haber instalado Docker.
    - Dentro del directorio `bin`, existe un subdirectorio `windows`, con scripts adaptados para este sistema (y terminados en `.bat`). Entonces, los pasos y opciones serían los siguientes:
        - Prueba a usar los scripts `.sh` como se describe a continuación.
        - Si no funcionan, instala WSL2, o prueba a utilizar las versiones `.bat`.

2. Comenzaremos por crear el topic `testtopic` en el que nuestros consumidores trabajarán. Ejecuta el siguiente comando:

```bash
./kafka-topics.sh --bootstrap-server localhost:9093 --create --topic testtopic
```

3. Comprueba que se ha creado el topic correctamente:

```bash
./kafka-topics.sh --bootstrap-server localhost:9093 --list
```

```bash
./kafka-topics.sh --bootstrap-server localhost:9093 --topic testtopic --describe
```

4. De momento no utilizaremos más este comando, pero puedes comprobar la ayuda para ver todas las funciones proporcionadas:

```bash
./kafka-topics.sh --bootstrap-server localhost:9093 --help
```

5. Ahora que tenemos el topic creado, vamos a iniciar un consumidor que muestre todos los mensajes mandados:

```bash
./kafka-console-consumer.sh --bootstrap-server localhost:9093 --topic testtopic
```

6. Abre otra terminal en el mismo directorio. En esta terminal vamos a crear un productor, que nos permitirá enviar un mensaje al nuevo topic cada vez que escribamos algo y demos al intro:

```bash
./kafka-console-producer.sh --bootstrap-server localhost:9093 --topic testtopic
```

Nos debería aparecer un prompt `>`, indicándonos que podemos escribir el mensaje. Al escribirlo y pulsar la tecla intro, dicho mensaje debería aparecer en la terminal del consumidor.

7. Escribe varios mensajes desde el productor. Después, crea un nuevo consumidor, esta vez con este comando (que varía solamente al final). ¿Qué sucede?

```bash
./kafka-console-consumer.sh --bootstrap-server localhost:9093 --topic testtopic --from-beginning
```

## Productores / Consumidores desde Java

El repositorio incluye un proyecto de Java/Maven con un productor y un consumidor (`Producer.java` y `Consumer.java` respectivamente). Importa el proyecto dentro del IDE de tu preferencia (creado con Eclipse, pero algo como IntelliJ Idea debería funcionar también) y estudia/ejecuta los programas.

### Id de los grupos de consumidores

En el caso de Java, el `group id` utilizado por los consumidores se asigna manualmente como parte de sus propiedades. Esto no era así cuando lanzábamos consumidores desde la consola, ya que cada uno utilizaba un grupo autogenerado distinto.

Podemos listar los grupos de consumidores existentes:

```bash
./kafka-consumer-groups.sh --bootstrap-server localhost:9093 --list
```

- Tras haber lanzado una vez `Consumidor.java`, comprueba qué ocurre si lo paras y lo lanzas de nuevo. Una vez lanzado, genera mensajes desde un productor (o bien desde Java o desde la terminal).
    - Recuerda que no deberíamos tener más de un consumidor con el mismo `group id` conectado a la misma partición.
- Para de nuevo el consumidor, cambia el valor de su `group id`, y lánzalo de nuevo.

También podemos ver los detalles de un grupo, mostrando los offsets por los que van:

```bash
./kafka-consumer-groups.sh --bootstrap-server localhost:9093 --group my_first_consumer --describe
```

Por último, podemos comprobar los miembros de cada grupo de consumidores:

```bash
./kafka-consumer-groups.sh --bootstrap-server localhost:9093 --group my_first_consumer --describe --members
```
