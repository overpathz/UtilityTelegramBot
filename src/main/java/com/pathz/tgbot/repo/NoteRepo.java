package com.pathz.tgbot.repo;

import com.pathz.tgbot.entity.Note;
import com.pathz.tgbot.entity.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepo extends JpaRepository<Note, Long> {
    // @Query("select count(Note) from Note where chatId =: ")
    int countNotesByChatId(Long chatId);
}
