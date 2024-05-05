/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.controllers;

import com.example.domain.dto.ClientDto;
import com.example.domain.entities.Client;
import com.example.domain.exceptions.NoClientFoundException;
import com.example.domain.exceptions.ValidationException;
import com.example.domain.mapper.ClientMapper;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<List<ClientDto>> getAllClients() throws Exception {
        List<ClientDto> dtoClients = new ArrayList<>();
        clientService.getAllClient().forEach(client -> {
                    ClientDto dto = ClientMapper.getMapper().dto(client);
                    dtoClients.add(dto);
                }
        );
        return dtoClients.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(dtoClients);
    }

    @PostMapping("/create")
    public ResponseEntity<ClientDto> addNewClient(@Valid @RequestBody ClientDto dto, Errors erros) throws Exception {
        ResponseHelper.handle(erros);
        Client client = ClientMapper.getMapper().entitie(dto);
        clientService.register(client);
        URI uri = URI.create("/users/" + client.getReference());
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(summary = "Retourne un Client", description = "Retourne un Client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = Client.class))),
            @ApiResponse(responseCode = "404", description = "Erreur de saisie", content = @Content),
            @ApiResponse(responseCode = "500", description = "An Internal Server Error occurred", content = @Content)

    })
    @GetMapping("/{reference}")
    public ResponseEntity<ClientDto> getClientByReference(
            @Parameter(description = "reference of Client")
            @NotBlank @PathVariable("reference") String reference) throws NoClientFoundException {
        ClientDto dto = ClientMapper.getMapper().dto(clientService.getClientByReference(reference));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/email/{email}")
    public Client getClientByEmail(
            @Parameter(description = "email of Client")
            @NotBlank @PathVariable("email") String email) throws NoClientFoundException {
        return clientService.getClientByEmail(email);
    }

    @PutMapping(path = "")
    public ResponseEntity<ClientDto> updateClient(@RequestBody ClientDto ClientDto, Errors erros) throws ValidationException, NoClientFoundException {
        ResponseHelper.handle(erros);
        ClientDto dto = ClientMapper.getMapper().dto(clientService.update(ClientDto));
        return ResponseEntity.ok(dto);
    }


    @DeleteMapping(path = "/{reference}")
    public ResponseEntity<Void> deleteClient(@NotBlank @PathVariable("reference") String reference) throws NoClientFoundException {
        ClientDto dto = ClientMapper.getMapper().dto(clientService.delete(reference));
        return ResponseEntity.noContent().build();
    }


}
