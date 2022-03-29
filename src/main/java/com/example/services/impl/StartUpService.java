package com.example.services.impl;

import com.example.entities.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class StartUpService implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        User admin = new User();
        admin.setName("Joel");
        admin.setLastName("beezy");
        admin.setEmail("kewou.noumia@gmail.com");
        admin.setRole("Admin");
        userRepository.save(admin);
    }
}
