package com.maleki.narges.libraryeventconsumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryEvent {
    private Integer libraryEventId;

    private LibraryEventType libraryEventType;

    private Book book;
}
