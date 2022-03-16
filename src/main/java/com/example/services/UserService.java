/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.entities.User;

import java.util.List;

/**
 * @author frup73532
 */
public interface UserService {

    User getUser(Long id);

    List<User> getAllUser();

    void delete(Long id);

    void addOrUpdate(User user);

}
