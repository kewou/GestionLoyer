/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.controllers;

import com.example.entities.Logement;
import com.example.entities.User;
import com.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * @author frup73532
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "")
    public List<User> getAllUsers() throws Exception {
        return userService.getAllUser();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") long id) {
        return userService.getUser(id);
    }

    @PostMapping(path = "/add")
    public ResponseEntity<String> addNewUser(@Valid @RequestBody User user) {
        userService.addOrUpdate(user);
        return ResponseEntity.ok("User is valid");
    }

    @DeleteMapping(path = "/{id}")
    public void deleteUser(@PathVariable("id") long id) {
        userService.delete(id);
    }

    @PutMapping(path = "/update")
    public User updateUser(@RequestBody User user) {
        userService.addOrUpdate(user);
        return user;
    }

    @GetMapping("/{id}/logements")
    public Set<Logement> getAllLogement(@PathVariable("id") long id) {
        return userService.getUser(id).getLogements();
    }

}
