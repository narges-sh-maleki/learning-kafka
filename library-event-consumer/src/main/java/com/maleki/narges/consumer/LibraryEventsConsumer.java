package com.maleki.narges.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maleki.narges.domain.LibraryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LibraryEventsConsumer {

    public static final String TOPIC_NAME = "libraryEvents";
    private final ObjectMapper objectMapper;

/*
    @KafkaListener(topics = TOPIC_NAME)
    public void onMessage(@Payload LibraryEvent libraryEvent){
            log.info("Message received: {}" + libraryEvent.toString());

    }*/

    @KafkaListener(topics = TOPIC_NAME)
    public void onMessage(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException {
        log.info("Message received: {}" , consumerRecord);
        LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
        log.info("Message received: {}" + libraryEvent.toString());
    }
}
