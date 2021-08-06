package com.pathz.tgbot.util.mapper;

import com.pathz.tgbot.dto.NoteDto;
import com.pathz.tgbot.entity.Note;
import org.springframework.stereotype.Component;

@Component
public final class NoteDtoMapper {

    public NoteDto mapToDto(Note note) {
        return new NoteDto(note.getId(), note.getUsername(), note.getText());
    }
}
