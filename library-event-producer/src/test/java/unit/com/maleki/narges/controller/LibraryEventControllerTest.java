package com.maleki.narges.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maleki.narges.domain.Book;
import com.maleki.narges.domain.LibraryEvent;
import com.maleki.narges.domain.LibraryEventType;
import com.maleki.narges.producer.LibraryEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventController.class)
@AutoConfigureMockMvc
class LibraryEventControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LibraryEventProducer libraryEventProducer;



    @Test
    void postLibraryEvent() throws Exception {
        //given


        LibraryEvent libraryEvent = LibraryEvent.builder()
                                            .libraryEventType(LibraryEventType.NEW)
                                            .book(Book.builder().bookId(123).bookName("test").bookAuthor("test").build())
                                            .build();
       // doNothing().when(libraryEventProducer).sendLibraryEvent(any(LibraryEvent.class));
        when(libraryEventProducer.sendLibraryEvent(libraryEvent)).thenReturn(null);
    //when & then

        mockMvc.perform( post(URI.create("/v1/libraryevent"))
                .content(objectMapper.writeValueAsString(libraryEvent))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }

    @Test
    void postLibraryEvent_4xx() throws Exception {
        //given
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventType(LibraryEventType.NEW)
                .book(Book.builder().bookId(null).bookName("").bookAuthor("test").build())
                .build();
       // doNothing().when(libraryEventProducer).sendLibraryEvent(any(LibraryEvent.class));
        when(libraryEventProducer.sendLibraryEvent(libraryEvent)).thenReturn(null);

        //when & then
        mockMvc.perform( post(URI.create("/v1/libraryevent"))
                .content(objectMapper.writeValueAsString(libraryEvent))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("book.bookId-must not be null,book.bookName-must not be blank"));

    }
}