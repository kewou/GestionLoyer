/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.controllers;

import com.example.domain.dto.ClientDto;
import com.example.domain.entities.Client;
import com.example.domain.entities.Logement;
import com.example.domain.exceptions.NoClientFoundException;
import com.example.domain.exceptions.ValidationException;
import com.example.helper.ResponseHelper;
import com.example.services.impl.ClientService;
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
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

/**
 * @author frup73532
 */
@RestController
@RequestMapping("/users")
public class ClientController {

    @Autowired
    private ClientService clientService;


    @Operation(summary = "Tous les Clients", description = "Tous les Clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = Client.class))),
            @ApiResponse(responseCode = "404", description = "Erreur de saisie", content = @Content),
            @ApiResponse(responseCode = "500", description = "An Internal Server Error occurred", content = @Content)

    })
    @GetMapping
    public List<Client> getAllClients() throws Exception {
        return clientService.getAllClient();
    }

    @Operation(summary = "Retourne un Client", description = "Retourne un Client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = Client.class))),
            @ApiResponse(responseCode = "404", description = "Erreur de saisie", content = @Content),
            @ApiResponse(responseCode = "500", description = "An Internal Server Error occurred", content = @Content)

    })
    @GetMapping("/{reference}")
    public Client getClientByReference(
            @Parameter(description = "reference of Client")
            @NotBlank @PathVariable("reference") String reference) throws NoClientFoundException {
        return clientService.getClientByReference(reference);
    }

    @GetMapping("/email/{email}")
    public Client getClientByEmail(
            @Parameter(description = "email of Client")
            @NotBlank @PathVariable("email") String email) throws NoClientFoundException {
        return clientService.getClientByEmail(email);
    }

    @PostMapping("/create")
    public ResponseEntity<ClientDto> addNewClient(@Valid @RequestBody ClientDto dto, Errors erros) throws Exception {
        ResponseHelper.handle(erros);
        ClientDto ClientDto = clientService.register(dto);
        return ResponseEntity.ok(ClientDto);
    }

    @DeleteMapping(path = "/{reference}")
    public void deleteClient(@NotBlank @PathVariable("reference") String reference) {
        clientService.delete(reference);
    }

    @PutMapping(path = "/{reference}")
    public ResponseEntity<Client> updateClient(@RequestBody ClientDto ClientDto, @NotBlank @PathVariable("reference") String reference, Errors erros) throws ValidationException, NoClientFoundException {
        ResponseHelper.handle(erros);
        Client Client = clientService.update(ClientDto, reference);
        return ResponseEntity.ok(Client);
    }

    @GetMapping("/{id}/logements")
    public Set<Logement> getAllLogement(@PathVariable("id") long id) throws NoClientFoundException {
        return clientService.getClient(id).getLogements();
    }

}
