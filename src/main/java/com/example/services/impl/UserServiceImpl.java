/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services.impl;

import com.example.domain.dto.UserDto;
import com.example.domain.entities.User;
import com.example.domain.exceptions.NoUserFoundProblem;
import com.example.repository.UserRepository;
import com.example.services.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author frup73532
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoUserFoundProblem(id));
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void update(User user) {
        userRepository.save(user);
    }

    @Override
    public User register(UserDto dto) throws Exception {
        if (!checkIfUserExist(dto.getEmail())) {
            User user = new User();
            BeanUtils.copyProperties(dto, user, "id");
            user.setPassword(encoder.encode(dto.getPassword()));
            userRepository.save(user);
            return user;
        } else {
            throw new Exception("User is already exist on database");
        }
    }

    @Override
    public boolean checkIfUserExist(String email) {
        return userRepository.findByEmail(email) != null ? true : false;
    }

}
