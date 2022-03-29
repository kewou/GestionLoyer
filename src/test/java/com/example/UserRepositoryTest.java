package com.example;

import com.example.entities.User;
import com.example.services.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTest {

    public static int nbUserDatabase=1;

    @Autowired
    private UserService userService;

    @Test
    @Order(1)
    void contextLoads() {
    }

    @Test
    @Order(2)
    public void testGetAllUser() {
        int res = userService.getAllUser().size();
        assertEquals(nbUserDatabase, res);
    }

    @Test
    @Order(3)
    public void testGetUser(){
        Long id= 1L;
        Optional<User> resOp = Optional.ofNullable(userService.getUser(id));
        assertEquals(id,resOp.get().getId());
    }

    @Test
    @Order(4)
    public void testUpdateUser(){
        Long id= 1L;
        User user=userService.getUser(id);
        user.setName("Tintamare");
        userService.addOrUpdate(user);
        assertEquals("Tintamare",userService.getUser(id).getName());
    }

    @Test
    @Order(5)
    public void testAddUser(){
        User user = new User();
        user.setEmail("test@test.fr");
        user.setName("test");
        user.setLastName("test");
        user.setRole("client");
        userService.addOrUpdate(user);
        assertEquals(userService.getAllUser().size(),nbUserDatabase+1);
    }

    @Test
    @Order(6)
    public void testDelete(){
        userService.delete(Long.valueOf(2));
        assertEquals(userService.getAllUser().size(),nbUserDatabase);
    }
}
