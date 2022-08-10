/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.controllers;

import com.example.domain.dto.UserDto;
import com.example.domain.entities.Logement;
import com.example.domain.entities.User;
import com.example.helper.ResponseHelper;
import com.example.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
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

    @Operation(summary = "Tous les users", description = "Tous les users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Erreur de saisie", content = @Content),
            @ApiResponse(responseCode = "500", description = "An Internal Server Error occurred", content = @Content)

    })
    @GetMapping(path = "")
    public List<User> getAllUsers() throws Exception {
        return userService.getAllUser();
    }

    @Operation(summary = "Retourne un user", description = "Retourne un user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Erreur de saisie", content = @Content),
            @ApiResponse(responseCode = "500", description = "An Internal Server Error occurred", content = @Content)

    })
    @GetMapping("/{id}")
    public User getUser(
            @Parameter(description = "id of user")
            @PathVariable("id") long id) {
        return userService.getUser(id);
    }

    @PostMapping(path = "/add")
    public ResponseEntity<User> addNewUser(@Valid @RequestBody UserDto dto, Errors erros) throws Exception {
        ResponseHelper.handle(erros);
        User user = userService.register(dto);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteUser(@PathVariable("id") long id) {
        userService.delete(id);
    }

    @PutMapping(path = "/update")
    public User updateUser(@RequestBody User user) {
        userService.update(user);
        return user;
    }

    @GetMapping("/{id}/logements")
    public Set<Logement> getAllLogement(@PathVariable("id") long id) {
        return userService.getUser(id).getLogements();
    }

}
