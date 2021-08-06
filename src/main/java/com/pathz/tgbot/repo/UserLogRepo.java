package com.pathz.tgbot.repo;

import com.pathz.tgbot.entity.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepo extends JpaRepository<UserLog, Long> {
}
