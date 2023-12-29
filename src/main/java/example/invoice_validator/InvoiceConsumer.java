package example.invoice_validator;

import configuration.Configs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Iterator;
import java.util.Properties;
import java.util.List;

public class InvoiceConsumer {

    private static Logger logger = LoggerFactory.getLogger(InvoiceConsumer.class);
    public static void main(String[] args) {
        Properties prodProps = new Properties();
        prodProps.put(ProducerConfig.CLIENT_ID_CONFIG, "invoice-filter-producer");
        prodProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Configs.BOOTSTRAP_SERVERS);
        prodProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        prodProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        Properties consProps = new Properties();
        consProps.put(ConsumerConfig.CLIENT_ID_CONFIG, Configs.CONSUMER_ID);
        consProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Configs.BOOTSTRAP_SERVERS);
        consProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        consProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consProps.put(ConsumerConfig.GROUP_ID_CONFIG, Configs.GROUP_ID);
        consProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, Configs.AUTO_OFFSET_RESET_CONFIG);

        KafkaProducer<Integer, String> invoiceFilterProducer = new KafkaProducer<>(prodProps);
        KafkaConsumer<Integer, String> invoiceConsumer = new KafkaConsumer<>(consProps);

        invoiceConsumer.subscribe(List.of("all-invoices"));

        logger.info("starting consumer");
        while(true) {
            ConsumerRecords<Integer, String> consumerRecords = invoiceConsumer.poll(Duration.ofMillis(5000));
            Iterator<ConsumerRecord<Integer, String>> itr = consumerRecords.iterator();
            while(itr.hasNext()) {
                ConsumerRecord<Integer, String> consumerRecord = itr.next();
                if(consumerRecord.value().equals("null")) { // the invoice is invalid, send to invalid-invoice topic
                    ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>("invalid-invoices", consumerRecord.key(), "NaN");
                    invoiceFilterProducer.send(producerRecord);
                } else {
                    ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>("valid-invoices", consumerRecord.key(), consumerRecord.value());
                    invoiceFilterProducer.send(producerRecord);
                }
            }
            logger.info("Consumed some records");
        }
    }
}
