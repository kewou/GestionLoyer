/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services.impl;

import com.example.entities.User;
import com.example.exceptions.NoUserFoundException;
import com.example.repository.UserRepository;
import com.example.services.UserService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author frup73532
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUser() {
        List<User> users = new ArrayList<User>();
        userRepository.findAll().forEach(user -> users.add(user));
        if (users.isEmpty()) {
            throw new NoUserFoundException("No user found");
        }
        return users;
    }

    @Override
    public User getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoUserFoundException("No user found with this id => " + id));
        return user;
    }

    @Override
    public void delete(Long id) {
        User user = getUser(id);
        userRepository.delete(user);
    }

    @Override
    public void addOrUpdate(User user) {
        userRepository.save(user);
    }
}
