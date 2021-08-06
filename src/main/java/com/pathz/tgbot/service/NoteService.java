package com.pathz.tgbot.service;

import com.pathz.tgbot.dto.NoteDto;
import com.pathz.tgbot.entity.Note;
import com.pathz.tgbot.repo.NoteRepo;
import com.pathz.tgbot.util.mapper.NoteDtoMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
public class NoteService {

    private final NoteRepo noteRepo;
    private final NoteDtoMapper noteDtoMapper;

    public NoteService(NoteRepo noteRepo, NoteDtoMapper noteDtoMapper) {
        this.noteRepo = noteRepo;
        this.noteDtoMapper = noteDtoMapper;
    }

    public void save(Note note) {
        noteRepo.save(note);
    }

    public long countNotesByChatId(Long chatId) {
        return noteRepo.countNotesByChatId(chatId);
    }

    public List<NoteDto> findAll(Long chatId) {
        return noteRepo.findAllByChatId(chatId).stream()
                .map(noteDtoMapper::mapToDto)
                .collect(toList());
    }
}
