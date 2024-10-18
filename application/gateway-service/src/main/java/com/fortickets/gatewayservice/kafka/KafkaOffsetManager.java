//package com.fortickets.gatewayservice.kafka;
//
//import java.util.Collections;
//import java.util.Properties;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.common.TopicPartition;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//@Component
//public class KafkaOffsetManager {
//
//    @Value("${kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    private final String topic = "ticket-queue-topic"; // Kafka 토픽 이름
//
//    /**
//     * 특정 파티션에서 현재 처리 중인 오프셋을 가져오는 메서드
//     * @return 현재 Kafka 오프셋 값
//     */
//    public long getCurrentOffsetFromKafka(int partition) {
//        KafkaConsumer<String, String> consumer = createConsumer("ticket-consumer-group");
//        TopicPartition topicPartition = new TopicPartition(topic, partition);
//        consumer.assign(Collections.singletonList(topicPartition));
//        consumer.seekToEnd(Collections.singletonList(topicPartition));
//        return consumer.position(topicPartition);
//    }
//
//    /**
//     * Kafka Consumer를 생성하는 메서드
//     * @param consumerGroupId 소비자 그룹 ID
//     * @return KafkaConsumer 객체
//     */
//    private KafkaConsumer<String, String> createConsumer(String consumerGroupId) {
//        Properties properties = new Properties();
//        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
//        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//
//        return new KafkaConsumer<>(properties);
//    }
//}