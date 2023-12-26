package example.basic;

import configuration.Configs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class BasicProducer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, Configs.CLIENT_ID);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Configs.BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer<Integer, String> producer = new KafkaProducer<>(props)) {
            System.out.print("Send started...");
            for (int i = 0; i < Configs.NUM_EVENTS; i++) {
                ProducerRecord<Integer, String> pr = new ProducerRecord<>(Configs.TOPIC_NAME, i, "My message" + i);
                producer.send(pr);
            }
            System.out.print("Send completed...");
        } catch (Exception e) {
            System.out.print("Some error has occurred");
        }
    }
}
