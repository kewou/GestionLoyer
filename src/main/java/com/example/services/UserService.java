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

    User getUserByReference(String reference);

    User getUserByEmail(String email);

    List<User> getAllUser();

    void delete(String reference);

    User update(UserDto user,String reference);

    UserDto register(UserDto user) throws Exception;

    boolean checkIfUserExist(String email);
}
