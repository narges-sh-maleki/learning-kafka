package com.maleki.narges.controller;

import com.maleki.narges.domain.Book;
import com.maleki.narges.domain.LibraryEvent;
import com.maleki.narges.producer.LibraryEventProducer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = LibraryEventProducer.TOPIC_NAME, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})

class LibraryEventControllerTestIT {


    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Integer,String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1","true",embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs,new IntegerDeserializer(),new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
   @Timeout(5)
    void postLibraryEvent() throws InterruptedException {
        //given
        LibraryEvent libraryEvent = LibraryEvent.builder().book(Book.builder().bookId(123).bookName("test").bookAuthor("test").build()).build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type",MediaType.APPLICATION_JSON.toString());
        HttpEntity<?> requestEntity = new HttpEntity<>(libraryEvent,headers);
        //when

        ResponseEntity<LibraryEvent> result = testRestTemplate.exchange("/v1/libraryevent", HttpMethod.POST
                , requestEntity, LibraryEvent.class);
        //then

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        System.out.println("********consumer topics:" + consumer.listTopics().toString());
        ConsumerRecord<Integer, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, LibraryEventProducer.TOPIC_NAME);

        String expected = "{\"libraryEventId\":null,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":123,\"bookName\":\"test\",\"bookAuthor\":\"test\"}}";

        assertThat(singleRecord.value()).isEqualTo(expected);
    }
}