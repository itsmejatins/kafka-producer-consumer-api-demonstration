package example.invoice_validator;

import configuration.Configs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Random;

public class InvoiceProducer {

    private static Logger logger = LoggerFactory.getLogger(InvoiceProducer.class.getName());
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "all-invoice-producer");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Configs.BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);
        final Random random = new Random();
        Runnable invalidInvoiceRunnable = () -> {
            logger.info("invalid invoice producer thread starting");
            for (int i = 0; i < 1000; i += 2) {
                ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>("all-invoices", i, "null");
                producer.send(producerRecord);
            }
            logger.info("invalid invoice producer thread finishing");
        };

        Runnable validInvoiceRunnable = () -> {
            logger.info("valid invoice producer thread starting");
            for (int i = 1; i < 1000; i += 2) {
                ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>("all-invoices", i, random.nextInt(1, 101) + "");
                producer.send(producerRecord);
            }
            logger.info("valid invoice producer thread finishing");
        };

        Thread validInvoiceGenerator = new Thread(validInvoiceRunnable); // all invoices with odd key are valid
        Thread invalidInvoiceGenerator = new Thread(invalidInvoiceRunnable); // all invoices with even key are invalid

        invalidInvoiceGenerator.start();
        validInvoiceGenerator.start();

        try {
            invalidInvoiceGenerator.join();
            validInvoiceGenerator.join();
            producer.close();
        } catch (InterruptedException e) {
            logger.error("all-invoices producer failed");
        }

        logger.info("All invoices have been produced, now closing the program");
    }
}
