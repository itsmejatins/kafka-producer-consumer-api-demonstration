"$KAFKA_HOME"/bin/kafka-topics --create --topic transaction-topic-2 --bootstrap-server localhost:9090 --partitions 3 --replication-factor 3 --config min.insync.replicas=2