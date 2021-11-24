/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services.impl;

import com.example.dto.UserDTO;
import com.example.entities.User;
import com.example.exceptions.NoUserFoundException;
import com.example.repository.UserRepository;
import com.example.services.UserService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author frup73532
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoUserFoundException("No user found with this id =>" + id));
    }

    @Override
    public void delete(Long id) {
        User user = getUser(id);
        userRepository.delete(user);
    }

    @Override
    public UserDTO add(UserDTO userFront) {
        userRepository.save(userFront.convertToEntity());
        return userFront;
    }

    @Override
    public UserDTO update(UserDTO userFront) {
        User user = userRepository.findByName(userFront.getName());
        user.setName(userFront.getName());
        if (userFront.getLastName() != null) {
            user.setLastName(userFront.getLastName());
        }
        if (userFront.getEmail() != null) {
            user.setEmail(userFront.getEmail());
        }
        userRepository.save(user);
        return user.convertToDTO();
    }

    @Override
    public List<User> getAllUser() {
        List<User> users = (List<User>) userRepository.findAll();
        if (users.isEmpty()) {
            throw new NoUserFoundException("No user found");
        }
        return users;
    }

}
