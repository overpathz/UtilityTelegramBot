package com.pathz.tgbot.repo;

import com.pathz.tgbot.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepo extends JpaRepository<AdminUser, Long> {

    AdminUser findByAdminIdentifier(Long adminIdentifier);

    void deleteByAdminIdentifier(Long adminIdentifier);
}
