package example.multithreaded;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import configuration.Configs;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MultithreadedProducer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, Configs.CLIENT_ID);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Configs.BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);

        List<Thread> allThreads = new ArrayList<>();
        for (int t = 0; t < Configs.NUM_THREADS; t++) {
            ProducerRunnable pr = new ProducerRunnable("producer " + t, Configs.BOOTSTRAP_SERVERS, Configs.NUM_EVENTS, Configs.TOPIC_NAME, producer);
            Thread thread = new Thread(pr);
            allThreads.add(thread);
            System.out.println(String.format("Producer with id = %d is starting", t));
            thread.start();
        }

        try {
            for (Thread t : allThreads)
                t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            producer.close();
            System.out.println("Done with everything");
        }
    }
}
