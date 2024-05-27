/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.features.user.application;

import com.example.exceptions.BusinessException;
import com.example.exceptions.ValidationException;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.domain.services.impl.ClientService;
import com.example.helper.ResponseHelper;
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
import java.util.List;

/**
 * @author frup73532
 */
@RestController
@RequestMapping("/users")
public class ClientController {

    private ClientAppService clientAppService;

    @Autowired
    public ClientController(ClientService clientAppService) {
        this.clientAppService = clientAppService;
    }


    @Operation(summary = "Tous les Clients", description = "Tous les Clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = Client.class))),
            @ApiResponse(responseCode = "404", description = "Erreur de saisie", content = @Content),
            @ApiResponse(responseCode = "500", description = "An Internal Server Error occurred", content = @Content)

    })
    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        return ResponseEntity.ok(clientAppService.getAllClient());
    }


    @PostMapping("/create-locataire")
    public ResponseEntity<ClientDto> addNewLocataire(@Valid @RequestBody ClientDto dto, Errors erros) throws BusinessException {
        return addNewClient(erros, dto, "LOCATAIRE");
    }

    @PostMapping("/create-bailleur")
    public ResponseEntity<ClientDto> addNewBailleur(@Valid @RequestBody ClientDto dto, Errors erros) throws BusinessException {
        return addNewClient(erros, dto, "BAILLEUR");
    }

    @PostMapping("/create-admin")
    public ResponseEntity<ClientDto> addNewAdmin(@Valid @RequestBody ClientDto dto, Errors erros) throws BusinessException {
        return addNewClient(erros, dto, "ADMIN");
    }

    private ResponseEntity<ClientDto> addNewClient(Errors erros, ClientDto clientDto, String clientRole) throws BusinessException {
        ResponseHelper.handle(erros);        
        clientAppService.register(clientDto, clientRole);
        return ResponseEntity.created(URI.create("/users/" + clientDto.getReference())).body(clientDto);

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
            @NotBlank @PathVariable("reference") String reference) throws BusinessException {
        return ResponseEntity.ok(clientAppService.getClientByReference(reference));
    }

    @GetMapping("/email/{email}")
    public Client getClientByEmail(
            @Parameter(description = "email of Client")
            @NotBlank @PathVariable("email") String email) {
        return clientAppService.getClientByEmail(email);
    }

    @PutMapping(path = "/{reference}")
    public ResponseEntity<ClientDto> updateClient(@RequestBody ClientDto ClientDto, Errors erros,

                                                  @NotBlank @PathVariable("reference") String reference) throws ValidationException, BusinessException {
        ResponseHelper.handle(erros);
        return ResponseEntity.ok(clientAppService.update(ClientDto, reference));
    }


    @DeleteMapping(path = "/{reference}")
    public ResponseEntity<Void> deleteClient(@NotBlank @PathVariable("reference") String reference) throws BusinessException {
        clientAppService.delete(reference);
        return ResponseEntity.noContent().build();
    }


}
