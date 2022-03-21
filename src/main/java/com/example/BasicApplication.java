package com.example;

import com.example.entities.User;
import com.example.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BasicApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(BasicApplication.class, args);
        UserRepository userRepository = configurableApplicationContext.getBean(UserRepository.class);

        User admin = new User();
        admin.setName("Joel");
        admin.setLastName("beezy");
        admin.setEmail("kewou.noumia@gmail.com");
        admin.setRole("Admin");
        userRepository.save(admin);

    }

}
