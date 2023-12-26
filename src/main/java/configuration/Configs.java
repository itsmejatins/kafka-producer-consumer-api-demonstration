package configuration;

public class Configs {
    public static final String CLIENT_ID = "producer-1";
    public static final String BOOTSTRAP_SERVERS = "localhost:9092, localhost:9093";
    public static final int NUM_EVENTS = 1000;
    public static final String TOPIC_NAME = "my-topic";
    public static final int NUM_THREADS = 3;
    public static final String TRANSACTION_ID = "transaction-1";
    public static final String TT_1 = "transaction-topic-1";
    public static final String TT_2 = "transaction-topic-2";
}
