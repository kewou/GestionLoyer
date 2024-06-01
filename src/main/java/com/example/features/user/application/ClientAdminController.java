package com.example.features.user.application;


import com.example.exceptions.BusinessException;
import com.example.features.accueil.domain.services.AuthenticationService;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.domain.services.impl.ClientService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Joel NOUMIA
 */
@RestController
@RequestMapping("/admin/users")
public class ClientAdminController extends ClientController {

    @Autowired
    public ClientAdminController(ClientService clientAppService, AuthenticationService authenticationService) {
        super(clientAppService, authenticationService);
    }


    @Operation(summary = "Tous les Clients", description = "Tous les Clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = Client.class))),
            @ApiResponse(responseCode = "404", description = "Erreur de saisie", content = @Content),
            @ApiResponse(responseCode = "500", description = "An Internal Server Error occurred", content = @Content)

    })
    @GetMapping
    @PreAuthorize(SecurityRule.ADMIN)
    public ResponseEntity<List<ClientDto>> getAllClients() {
        return ResponseEntity.ok(clientAppService.getAllClient());
    }

    @DeleteMapping(path = "/{reference}")
    @PreAuthorize(SecurityRule.ADMIN)
    public ResponseEntity<Void> deleteClient(@NotBlank @PathVariable("reference") String reference) throws BusinessException {
        clientAppService.delete(reference);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    @PreAuthorize(SecurityRule.ADMIN)
    public Client getClientByEmail(
            @Parameter(description = "email of Client")
            @NotBlank @PathVariable("email") String email) {
        return clientAppService.getClientByEmail(email);
    }


}
