package com.example;

import com.example.domain.dto.UserDto;
import com.example.domain.entities.User;
import com.example.repository.UserRepository;
import com.example.services.UserService;
import com.example.services.impl.StartUpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /*
    @Autowired
    private StartUpService startUpService;
    */

    // Methode appelée avant tous les tests
    @BeforeEach
    public void setUp() throws Exception {
        //userRepository.deleteAll();
        //startUpService.run(new String[0]);
    }

    @Test
    void contextLoads() {
        userRepository.deleteAll();
    }

    @Test
    public void testGetAllUser() {
        assertEquals(userService.getAllUser().size(), userRepository.count());
    }


    @Test
    public void testGetUserByEmail(){
        String email="kewou.noumia@gmail.com";
        User user =userService.getUserByEmail(email);
        assertNotNull(user);
        assertEquals(email, user.getEmail());
    }

    @Test
    @Order(1)
    public void testUpdateUser() {
        String email="kewou.noumia@gmail.com";
        User user = userService.getUserByEmail(email);
        user.setName("Tintamare");
        userService.update(user);
        assertEquals("Tintamare", userService.getUserByEmail(email).getName());
    }

    @Test
    public void testAddUser() throws Exception {
        int nbUser = (int) userRepository.count();
        UserDto dto = new UserDto();
        dto.setEmail("test@test.fr");
        dto.setName("test");
        dto.setLastName("test");
        dto.setRole("client");
        dto.setPassword("test");
        userService.register(dto);
        assertEquals(userService.getAllUser().size(), nbUser + 1);
    }

    @Test
    public void testDelete() {
        int nbUser = (int) userRepository.count();
        userService.delete(userService.getAllUser().get(0).getId());
        assertEquals(userService.getAllUser().size(), nbUser - 1);
    }

    @Test
    public void testLogement() {
        String email = "kewou.noumia@gmail.com";
        User user = userService.getUserByEmail(email);
        assertEquals(user.getLogements().size(), 1);
    }

    @Test
    public void testRecapLogement() {
        String email = "kewou.noumia@gmail.com";
        User user = userService.getUserByEmail(email);
        assertEquals(user.getLogements().iterator().next().getRecapByMonths().size(), 1);
    }
}
