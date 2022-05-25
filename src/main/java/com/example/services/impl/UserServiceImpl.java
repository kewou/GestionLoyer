/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services.impl;

import com.example.entities.User;
import com.example.exceptions.NoInstanceFoundException;
import com.example.repository.UserRepository;
import com.example.services.UserService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
            throw new NoInstanceFoundException("No user found");
        }
        return users;
    }

    @Override
    public User getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoInstanceFoundException("No user found with this id => " + id));
        return user;
    }

    @Override
    public User getUserByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void delete(Long id) {
        User user = getUser(id);
        userRepository.delete(user);
    }

    @Override
    public void update(User user) {
        userRepository.save(user);
    }

    @Override
    public void register(User userData) throws Exception {
        if(checkIfUserExist(userData.getEmail())){
            throw new Exception("User is already exist on database");
        }
        User user=new User();
        BeanUtils.copyProperties(userData,user);
        encodePassword(user,userData);
        userRepository.save(user);
    }

    @Override
    public boolean checkIfUserExist(String email){
        return userRepository.findByEmail(email) !=null ? true : false;
    }

    private void encodePassword(User user,User userData){
        String encoded = new BCryptPasswordEncoder().encode(userData.getPassword());
        user.setPassword(encoded);
    }

}
