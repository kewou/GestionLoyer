/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services.impl;

import com.example.domain.dto.UserDto;
import com.example.domain.entities.User;
import com.example.domain.exceptions.NoUserFoundProblem;
import com.example.domain.mapper.UserMapper;
import com.example.repository.UserRepository;
import com.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * @author frup73532
 */
@Service
@Transactional
public class UserServiceImpl implements UserService{

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
    public User getUserByReference(String reference){
        User user = userRepository.findByReference(reference)
                .orElseThrow(() -> new NoUserFoundProblem(reference));
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void delete(String reference) {
        userRepository.deleteByReference(reference);
    }

    @Override
    public User update(UserDto userDto,String reference) {
        User user=getUserByReference(reference);
        User userUpdate= UserMapper.getMapper().dtoToUser(userDto);
        UserMapper.getMapper().update(user,userUpdate);
        userRepository.save(user);
        return user;
    }

    @Override
    public UserDto register(UserDto dto) throws Exception {
        if (!checkIfUserExist(dto.getEmail())) {
            User user=UserMapper.getMapper().dtoToUser(dto);
            if(user.getReference() == null) {
                user.setReference(generateReference());
            }
            user.setPassword(encoder.encode(dto.getPassword()));
            user.setRoles("LOCATAIRE");
            userRepository.save(user);
            return UserMapper.getMapper().userToUserDto(user);
        } else {
            throw new Exception("User is already exist on database");
        }
    }

    @Override
    public boolean checkIfUserExist(String email) {
        return userRepository.findByEmail(email) != null ? true : false;
    }

    private String generateReference(){
        Random r = new Random();
        String[] alphabet ={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        String ref=alphabet[r.nextInt(25)] + r.nextInt(9) + alphabet[r.nextInt(25)] +r.nextInt(9) + alphabet[r.nextInt(25)];
        return ref;
    }

}
