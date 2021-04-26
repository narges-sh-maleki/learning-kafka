package com.maleki.narges.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maleki.narges.domain.Book;
import com.maleki.narges.domain.LibraryEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class LibraryEventProducerTest {

    @Mock
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper;

    @InjectMocks
    LibraryEventProducer libraryEventProducer;

    @Test
    void sendLibraryEvent_failure() throws JsonProcessingException {
        //given
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .book(Book.builder().bookId(123).bookName("dd").bookAuthor("dd").build())
                .build();
        SettableListenableFuture settableListenableFuture = new SettableListenableFuture();
        settableListenableFuture.setException(new RuntimeException("kafka exp"));
        when(kafkaTemplate.send(anyString(), any(), anyString())).thenReturn(settableListenableFuture);
        //when&then
         assertThrows(Exception.class,() -> libraryEventProducer.sendLibraryEvent(libraryEvent).get()) ;


    }

    @Test
    void sendLibraryEvent_success() throws JsonProcessingException, ExecutionException, InterruptedException {
        //given
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .book(Book.builder().bookId(123).bookName("dd").bookAuthor("dd").build())
                .build();
        SettableListenableFuture settableListenableFuture = new SettableListenableFuture();
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(LibraryEventProducer.TOPIC_NAME,1,libraryEvent.getLibraryEventId(),objectMapper.writeValueAsString(libraryEvent));
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(LibraryEventProducer.TOPIC_NAME,1),1,1,1,1l,1,1);
        SendResult<Integer, String> sendResult = new SendResult<>(producerRecord,recordMetadata);
        settableListenableFuture.set(sendResult);
        when(kafkaTemplate.send(anyString(), any(), anyString())).thenReturn(settableListenableFuture);
        //when
        SendResult<Integer, String> realSendResult = libraryEventProducer.sendLibraryEvent(libraryEvent).get();

        //then
        assertThat(settableListenableFuture.get()).isEqualTo(realSendResult);
        assertThat(realSendResult.getRecordMetadata().partition()).isEqualTo(1);


    }
}