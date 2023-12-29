package example.multithreaded;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerRunnable implements Runnable{ // the dispatcher
    private String producerThreadId, bootstrapServers, topic;
    private int numEvents;
    private KafkaProducer<Integer, String> producer;

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public ProducerRunnable(String producerThreadId, String bootstrapServers, int numEvents, String topic, KafkaProducer<Integer, String> producer) {
        this.producerThreadId = producerThreadId;
        this.bootstrapServers = bootstrapServers;
        this.numEvents = numEvents;
        this.topic = topic;
        this.producer = producer;
    }

    @Override
    public void run()
    {
        logger.info(String.format("%s: starting to send", producerThreadId));
        for(int i = 0 ; i < numEvents; i++)
        {
            producer.send(new ProducerRecord<>(topic, i, "From " + producerThreadId + " " + i));
        }
        logger.info(String.format("%s: sent all messages", producerThreadId));
    }
}
