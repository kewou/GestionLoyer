package com.example;

import com.example.entities.User;
import com.example.repository.UserRepository;
import com.example.services.UserService;


import com.example.services.impl.StartUpService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartUpService startUpService;

    // Methode appelée avant tous les tests
    @BeforeEach
    public void setUp() throws Exception{
        userRepository.deleteAll();
        startUpService.run(new String[0]);
    }


    @Test
    void contextLoads() {
    }

    @Test
    public void testGetAllUser() {
        assertEquals(userService.getAllUser().size(), userRepository.count());
    }

    @Test
    public void testGetUser(){
        String name="Joel";
        Optional<User> resOp = Optional.ofNullable(userService.getUserByName(name));
        assertEquals(name,resOp.get().getName());
    }

    @Test
    public void testUpdateUser(){
        String name="Joel";
        User user=userService.getUserByName(name);
        user.setName("Tintamare");
        userService.addOrUpdate(user);
        assertEquals("Tintamare",userService.getUserByName("Tintamare").getName());
    }

    @Test
    public void testAddUser(){
        int nbUser= (int) userRepository.count();
        User user = new User();
        user.setEmail("test@test.fr");
        user.setName("test");
        user.setLastName("test");
        user.setRole("client");
        userService.addOrUpdate(user);
        assertEquals(userService.getAllUser().size(),nbUser+1);
    }

    @Test
    public void testDelete(){
        int nbUser= (int) userRepository.count();
        userService.delete(userService.getAllUser().get(0).getId());
        assertEquals(userService.getAllUser().size(),nbUser-1);
    }

    @Test
    public void testLogement(){
        String name="Kidou";
        User user=userService.getUserByName(name);
        assertEquals(user.getLogements().size(),1);
    }
}
