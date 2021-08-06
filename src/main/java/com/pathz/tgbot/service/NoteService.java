package com.pathz.tgbot.service;

import com.pathz.tgbot.entity.Note;
import com.pathz.tgbot.repo.NoteRepo;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteRepo noteRepo;

    public NoteService(NoteRepo noteRepo) {
        this.noteRepo = noteRepo;
    }

    public void save(Note note) {
        noteRepo.save(note);
    }

    public long countNotesByChatId(long chatId) {
        return noteRepo.countNotesByChatId(chatId);
    }
}
