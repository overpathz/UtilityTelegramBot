package com.pathz.tgbot.service;

import com.pathz.tgbot.entity.AdminUser;
import com.pathz.tgbot.repo.AdminUserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AdminUserService {

    private final AdminUserRepo adminUserRepo;

    public AdminUserService(AdminUserRepo adminUserRepo) {
        this.adminUserRepo = adminUserRepo;
    }

    public boolean isAdmin(Long identifier) {
        Optional<AdminUser> adminUserOptional =
                Optional.ofNullable(adminUserRepo.findByAdminIdentifier(identifier));

        return adminUserOptional
                .map(adminUser -> adminUser.getAdminIdentifier().equals(identifier))
                .orElse(false);
    }

    public void addNewAdmin(AdminUser adminUser) {
        adminUserRepo.save(adminUser);
    }

    @Transactional
    public void removeFromAdmins(Long identifier) {
        adminUserRepo.deleteByAdminIdentifier(identifier);
    }
}
