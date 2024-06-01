/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.features.user.application;

import com.example.exceptions.BusinessException;
import com.example.exceptions.ValidationException;
import com.example.features.accueil.domain.services.AuthenticationService;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.domain.services.impl.ClientService;
import com.example.helper.ResponseHelper;
import com.example.security.Role;
import com.example.security.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author Joel NOUMIA
 */
@RestController
@RequestMapping("/users")
public class ClientController {

    protected ClientAppService clientAppService;
    protected AuthenticationService authenticationService;

    @Autowired
    public ClientController(ClientService clientAppService, AuthenticationService authenticationService) {
        this.clientAppService = clientAppService;
        this.authenticationService = authenticationService;
    }


    @PostMapping("/create-locataire")
    public ResponseEntity<ClientDto> addNewLocataire(@Valid @RequestBody ClientDto dto, Errors erros) throws BusinessException {
        return addNewClient(erros, dto, Role.LOCATAIRE);
    }

    @PostMapping("/create-bailleur")
    public ResponseEntity<ClientDto> addNewBailleur(@Valid @RequestBody ClientDto dto, Errors erros) throws BusinessException {
        return addNewClient(erros, dto, Role.BAILLEUR);
    }

    @PostMapping("/admin/create-admin")
    public ResponseEntity<ClientDto> addNewAdmin(@Valid @RequestBody ClientDto dto, Errors erros) throws BusinessException {
        return addNewClient(erros, dto, Role.ADMIN);
    }

    protected ResponseEntity<ClientDto> addNewClient(Errors erros, ClientDto clientDto, Role clientRole) throws BusinessException {
        ResponseHelper.handle(erros);
        return ResponseEntity.ok(clientAppService.register(clientDto, clientRole));

    }

    @Operation(summary = "Retourne un Client", description = "Retourne un Client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = Client.class))),
            @ApiResponse(responseCode = "404", description = "Erreur de saisie", content = @Content),
            @ApiResponse(responseCode = "500", description = "An Internal Server Error occurred", content = @Content)

    })
    @GetMapping("/{reference}")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    public ResponseEntity<ClientDto> getClientByReference(
            @Parameter(description = "reference of Client")
            @NotBlank @PathVariable("reference") String reference) throws BusinessException {
        return ResponseEntity.ok(clientAppService.getClientByReference(reference));
    }


    @PutMapping(path = "/{reference}")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    public ResponseEntity<ClientDto> updateClient(@RequestBody ClientDto ClientDto, Errors erros,
                                                  @NotBlank @PathVariable("reference") String reference) throws ValidationException, BusinessException {
        ResponseHelper.handle(erros);
        return ResponseEntity.ok(clientAppService.update(ClientDto, reference));
    }


}
