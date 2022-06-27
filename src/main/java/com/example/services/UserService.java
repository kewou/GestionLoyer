/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.domain.dto.UserDto;
import com.example.domain.entities.User;

import java.util.List;

/**
 * @author frup73532
 */
public interface UserService {

    User getUser(Long id);

    User getUserByName(String name);

    User getByEmail(String email);

    List<User> getAllUser();

    void delete(Long id);

    void update(User user);

    User register(UserDto user) throws Exception;

    boolean checkIfUserExist(String email);
}
