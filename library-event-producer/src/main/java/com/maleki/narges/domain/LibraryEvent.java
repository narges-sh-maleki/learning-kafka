package com.maleki.narges.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryEvent {
    private Integer libraryEventId;

    private LibraryEventType libraryEventType;
    @NotNull
    @Valid
    private Book book;
}
