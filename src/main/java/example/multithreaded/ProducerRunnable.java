package example.multithreaded;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProducerRunnable implements Runnable{ // the dispatcher
    private String producerId, bootstrapServers, topic;
    private int numEvents;
    private KafkaProducer<Integer, String> producer;

    public ProducerRunnable(String producerId, String bootstrapServers, int numEvents, String topic, KafkaProducer<Integer, String> producer) {
        this.producerId = producerId;
        this.bootstrapServers = bootstrapServers;
        this.numEvents = numEvents;
        this.topic = topic;
        this.producer = producer;
    }

    @Override
    public void run()
    {
        System.out.println(String.format("%s: starting to send", producerId));
        for(int i = 0 ; i < numEvents; i++)
        {
            producer.send(new ProducerRecord<>(topic, i, "From " + producerId + " " + i));
        }
        System.out.println(String.format("%s: sent all messages", producerId));
    }
}
