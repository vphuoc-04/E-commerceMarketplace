package com.example.user_service.databases.seeders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_service.entities.User;
import com.example.user_service.repositories.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class UserDatabaseSeeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(UserDatabaseSeeder.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public void run (String ...args) throws Exception {
        logger.info("Seeding database...");

        if (isTableEmpty()) {
            String passwordEncode = passwordEncoder.encode("password");

            User user = new User();
            user.setFirstName("Phước");
            user.setMiddleName("Văn");
            user.setLastName("Đoàn");
            user.setEmail("vphuoc2409@gmail.com");
            user.setPhone("0123456789");
            user.setPassword(passwordEncode);
            user.setImg(null);

            userRepository.save(user);
        }
    }

    private boolean isTableEmpty() {
        Long count = (Long) entityManager.createQuery("SELECT COUNT(id) FROM User").getSingleResult();
        return count == 0;
    }
}
