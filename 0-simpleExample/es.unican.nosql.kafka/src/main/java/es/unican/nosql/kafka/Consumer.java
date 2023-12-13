package es.unican.nosql.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

/*
 * https://www.conduktor.io/kafka/complete-kafka-consumer-with-java
 */

public class Consumer {

	public static void main(String[] args) {
		System.out.println("I am a Kafka Consumer");

		String bootstrapServers = "127.0.0.1:9093";
		String groupId = "my_first_consumer";
		String topic = "testtopic";

		// create consumer configs
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// create consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

		// get a reference to the current thread
		final Thread mainThread = Thread.currentThread();

		// adding the shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread() {

			public void run() {
				System.out.println("Detected a shutdown, let's exit by calling consumer.wakeup()...");
				consumer.wakeup();

				// join the main thread to allow the execution of the code in the main thread
				try {
					mainThread.join();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		try {
			// subscribe consumer to our topic(s)
			consumer.subscribe(Arrays.asList(topic));

			// uncomment to set the consumer offset to the end of the stream
			// if commented, the offset is set to 0, and all existing messages are processed
			// consumer.seekToEnd(consumer.assignment());

			// poll for new data
			while (true) {
				ConsumerRecords<String, String> records =
						consumer.poll(Duration.ofMillis(100));

				for (ConsumerRecord<String, String> record : records) {
					System.out.println("****************************************************");
					System.out.println("Key: " + record.key() + ", Value: " + record.value());
					System.out.println("Partition: " + record.partition() + ", Offset:" + record.offset());
				}
			}

		}
		catch (WakeupException e) {
			System.out.println("Wake up exception!");
			// we ignore this as this is an expected exception when closing a consumer
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			consumer.close(); // this will also commit the offsets if need be.
			System.out.println("The consumer is now gracefully closed.");
		}

	}
}
