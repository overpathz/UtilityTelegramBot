package com.pathz.tgbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class NoteDto {

    private Long id;
    private String username;
    private String text;

    @Override
    public String toString() {
        return "Id: %s | User: %s | Text: %s%s".formatted(id, username, text, "\n");
    }
}
