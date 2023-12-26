package example.transaction;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import configuration.Configs;

import java.util.Properties;

public class TransactionalProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, Configs.CLIENT_ID);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Configs.BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, Configs.TRANSACTION_ID);

        KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);

        producer.initTransactions();

        System.out.println("Starting the first transaction");
        producer.beginTransaction();

        try {
            for (int event = 0; event < Configs.NUM_EVENTS; event++) {
                producer.send(new ProducerRecord<>(Configs.TT_1, event, String.format("Transaction - 1, topic = %s, message %d", Configs.TT_1, event)));
                producer.send(new ProducerRecord<>(Configs.TT_2, event, String.format("Transaction - 1, topic = %s, message %d", Configs.TT_2, event)));
            }

            producer.commitTransaction();
        } catch (Exception e) {
            producer.abortTransaction();
            System.out.println("Transaction-2 failed");
        }

        System.out.println("Starting the second transaction");
        producer.beginTransaction();

        try {
            for (int event = 0; event < Configs.NUM_EVENTS; event++) {
                producer.send(new ProducerRecord<>(Configs.TT_1, event, String.format("Transaction - 2, topic = %s, message %d", Configs.TT_1, event)));
                producer.send(new ProducerRecord<>(Configs.TT_2, event, String.format("Transaction - 2, topic = %s, message %d", Configs.TT_2, event)));
                if(event == 20)
                {
                    int x = 5 / 0; // to deliberately throw an exception
                }
            }
            producer.commitTransaction();
        } catch (Exception e) {
            producer.abortTransaction();
            System.out.println("Transaction-2 failed");
        }

        System.out.println("Closing Kafka producer");
        producer.close();
    }
}