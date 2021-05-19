package com.maleki.narges.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maleki.narges.domain.LibraryEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(topics = LibraryEventsConsumer.TOPIC_NAME,partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
,"spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"})

class LibraryEventsConsumerIT {

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaTemplate<Integer,String> kafkaTemplate;

    @Autowired
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    ObjectMapper objectMapper;

    @SpyBean
    LibraryEventsConsumer libraryEventsConsumer;


    @BeforeEach
    void setUp() {
        for(MessageListenerContainer messageListenerContainer: kafkaListenerEndpointRegistry.getAllListenerContainers()){
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @Test
    void onMessage() throws JsonProcessingException, InterruptedException {
        //given
        LibraryEvent libraryEvent = LibraryEvent.builder().build();
        kafkaTemplate.send(LibraryEventsConsumer.TOPIC_NAME,objectMapper.writeValueAsString(libraryEvent));

        //when
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(3, TimeUnit.SECONDS);
        //then
        verify(libraryEventsConsumer,times(1)).onMessage(any(ConsumerRecord.class));
    }
}