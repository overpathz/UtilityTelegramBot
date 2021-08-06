package com.pathz.tgbot.repo;

import com.pathz.tgbot.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepo extends JpaRepository<Note, Long> {

    int countNotesByChatId(Long chatId);

    List<Note> findAllByChatId(Long chatId);

    void deleteAllByChatId(Long chatId);

    int deleteByIdAndChatId(Long id, Long chatId);
}
