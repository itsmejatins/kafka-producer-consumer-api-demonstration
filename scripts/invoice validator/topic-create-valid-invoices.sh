"$KAFKA_HOME"/bin/kafka-topics --create --topic valid-invoices --bootstrap-server localhost:9090 --partitions 3 --replication-factor 3