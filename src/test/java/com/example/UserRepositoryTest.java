package com.example;

import com.example.domain.dto.UserDto;
import com.example.domain.entities.User;
import com.example.repository.UserRepository;
import com.example.services.UserService;
import com.example.services.impl.StartUpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    public void setUp() throws Exception {
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
    public void testGetUser() {
        String name = "Joel";
        Optional<User> resOp = Optional.ofNullable(userService.getUserByName(name));
        assertEquals(name, resOp.get().getName());
    }

    @Test
    public void testGetUserByEmail(){
        String name = "Joel";
        String email="kewou.noumia@gmail.com";
        User user =userService.getByEmail(email);
        assertNotNull(user);
        assertEquals(name, user.getName());
    }

    @Test
    public void testUpdateUser() {
        String name = "Joel";
        User user = userService.getUserByName(name);
        user.setName("Tintamare");
        userService.update(user);
        assertEquals("Tintamare", userService.getUserByName("Tintamare").getName());
    }

    @Test
    public void testAddUser() throws Exception {
        int nbUser = (int) userRepository.count();
        UserDto dto = new UserDto();
        dto.setEmail("test@test.fr");
        dto.setName("test");
        dto.setLastName("test");
        dto.setRole("client");
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
        String name = "Kidou";
        User user = userService.getUserByName(name);
        assertEquals(user.getLogements().size(), 1);
    }

    @Test
    public void testRecapLogement() {
        String name = "Kidou";
        User user = userService.getUserByName(name);
        assertEquals(user.getLogements().iterator().next().getRecapByMonths().size(), 1);
    }
}
