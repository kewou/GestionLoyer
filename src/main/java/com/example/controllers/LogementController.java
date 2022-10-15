package com.example.controllers;

import com.example.domain.entities.Logement;
import com.example.services.LogementService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/logements")
public class LogementController {
    @Autowired
    private LogementService logementService;

    @GetMapping()
    @Operation(description = "Get list of all users")
    public List<Logement> getAll() throws Exception {
        return logementService.getAllLogement();
    }

}
