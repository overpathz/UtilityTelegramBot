package com.pathz.tgbot.service;

import com.pathz.tgbot.entity.UserLog;
import com.pathz.tgbot.repo.UserLogRepo;
import org.springframework.stereotype.Service;

@Service
public class UserLogService {

    private final UserLogRepo userLogRepo;

    public UserLogService(UserLogRepo userLogRepo) {
        this.userLogRepo = userLogRepo;
    }

    public void save(UserLog userLog) {
        userLogRepo.save(userLog);
    }
}
