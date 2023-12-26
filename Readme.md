# Kafka Producer and Consumer API.

- This repository demonstrates the use of Kafka Producer and Consumer API.
- Sending messages to Kafka topics is shown using three kind of producers -
  - basic producer.
  - multithreaded producer.
  - transactional producer.
- Later consumer api will also be created that will demonstrate how to consume messages from relevant topics.

## Basic producer

- Present in the package `example.basic`.
- This is a basic producer which sends messages to a topic called `my-topic`.
- This is later consumed by the console consumer.

## Multithreaded producer

- We can increase the number of machines running producers to send a large amount of data. We can increase the number of brokers in the cluster to increase the amount of messages that we can receive per second. However to increase the amount of data that we can send in a second, we can also create a multi threaded Kafka producer.
- When the amount of messages that we need to send are large, we can create multiple threads to send messages from the same producer instance.
- Creating multiple threads is faster and more efficient than creating many producer instances.

## Transactional producer

- We use transactional producers when we want either all of our messages to be send to relevant topics or none. All the topics which are included in the transaction should be configured with -
  - Replication factor ≥ 3
  - `min.insync.replicas` ≥ 2
- We need to set a transaction id for every producer instance - `props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, Configs.TRANSACTION_ID)`. We need to keep two things in mind -
  - When you set the transactional id for a producer, idempotence is automatically enabled for producers because transactions are automatically dependent on idempotence. Producers must be idempotent producers if they want to do some transaction.
  - Every producer must have a unique transaction id. The primary purpose of the transactional id is to rollback the older unfinished transactions for the same transactional id in case of producer application bounces or restarts. So if you run two instances of producer with the same transaction id, one of them will be aborted.
- The example to demonstrate transactional producer is present in `TransactionalProducer.java` file which is in the package `example.transaction`.
- Here we create two transactions. In both the transactions, we send some messages to two topics -
  - transaction-topic-1
  - transactional-topic-2
- Transaction-1 executes normally but we deliberately fail the second transaction. Now to check the atomicity of the `send()` call of transactional producer, we consume messages from both the topics.
  - It is found that all the messages send in the first transaction were successfully delivered to both the topics.
  - But no message from the second transaction reaches to any of the topics. The reason is that even though some messages were successfully sent to the topics, since the transaction failed, the messages sent were rollbacked to maintain atomicity of the transaction.
- In a multithreaded producer implementation, you will call the `send()` API from different threads. However, you must call the `beginTransaction()`  before starting those threads and either commit or abort when all the threads are complete.

## How to run

- Run the relevant scripts present in the `script` directory.
- Run the relevant examples by running the files - `BasicProducer.java`, `MultithreadedProducer.java`, and `TransactionalProdcer.java`.

### Configs

- This class is present in `configuration` package.
- It contains the constants that will be used for various purposes like configuration of producer properties object, topic names, transaction ids, Kafka broker addresses (ip, port), etc.

### Scripts

- The directory `kafka-producer-consumer-api-demonstration/scripts` contains shell scripts for -
  - starting zookeeper.
  - starting Kafka brokers.
  - starting console consumers.
  - creating topics.
- Run relevant scripts before running the examples - `BasicProducer.java`, `MultithreadedProducer.java`, and `TransactionalProdcer.java`.
  - Start zookeeper and Kafka brokers before running every example.
  - Run the script for creating relevant topics if they are not already created -
    - for basic producer and multithreaded producer, run `topic-create-my-topic.sh` . To consume messages from this topic, run `consumer-for-my-topic.sh` .
    - for transactional producer, run - `topic-create-transaction-topic-1.sh` and `topic-create-transaction-topic-2.sh` . To consume messages from these topics, run `consumer-for-transaction-topics.sh`.

### properties
- The directory `kafka-producer-consumer-api-demonstration/properties` contains the property files that are being used for starting zookeeper and Kafka brokers. 