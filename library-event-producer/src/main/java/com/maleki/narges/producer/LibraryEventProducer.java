package com.maleki.narges.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maleki.narges.domain.LibraryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
@Component
public class LibraryEventProducer {

    public static final String TOPIC_NAME = "libraryEvents";
    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public ListenableFuture<SendResult<Integer, String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        ListenableFuture<SendResult<Integer, String>> eventResult = kafkaTemplate.send(TOPIC_NAME, key, value);

       // eventResult.addCallback(getSuccessCallback(), getFailureCallback());
        eventResult.addCallback(result -> {
            log.info("**********message sent: key: {} , value: {} , partition: {}, topic: {}", key, value, result.getRecordMetadata().partition() , result.getRecordMetadata().topic());
        }
        , ex -> {
            log.error("*********error in sending message" + ex.getMessage());
        });

        return eventResult;
    }


    private FailureCallback getFailureCallback() {
        return new FailureCallback() {
            @Override
            public void onFailure(Throwable ex) {
                return;
            }
        };
    }


    private SuccessCallback<SendResult<Integer, String>> getSuccessCallback() {
        return new SuccessCallback<SendResult<Integer, String>>() {
            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                return;
            }
        };
    }

    public SendResult<Integer, String> sendLibraryEventSynchronus(LibraryEvent libraryEvent) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        SendResult<Integer, String> eventResult = null;
        try {
            eventResult = kafkaTemplate.send(TOPIC_NAME, key, value).get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException  e) {
            log.error("error in sending message: {}" + e.getMessage());
            throw (e);

        }
        log.info("message successfully sent: eventResult: {}" , eventResult);
        return eventResult;

    }
}
