package com.example.features.user.application;

import com.example.exceptions.BusinessException;
import com.example.exceptions.ValidationException;
import com.example.features.accueil.domain.services.AuthenticationService;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.application.mapper.ResetPasswordRequestDto;
import com.example.features.user.application.mapper.UpdatePasswordDto;
import com.example.features.user.application.mapper.VerificationUserInscriptionDto;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.domain.services.impl.ClientService;
import com.example.helper.ResponseHelper;
import com.example.security.Role;
import com.example.security.SecurityRule;
import com.example.utils.JWTUtils;
import com.example.utils.jwt.JwtResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.example.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;

/**
 * @author Joel NOUMIA
 */
@RestController
@Slf4j
@RequestMapping("/users")
public class ClientController {

    protected ClientAppService clientAppService;
    protected AuthenticationService authenticationService;

    protected JWTUtils jwtUtils;

    @Autowired
    public ClientController(ClientService clientAppService, AuthenticationService authenticationService, JWTUtils jwtUtils) {
        this.clientAppService = clientAppService;
        this.authenticationService = authenticationService;
        this.jwtUtils = jwtUtils;
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

    @PostMapping(path = "/verify-account")
    public JwtResponse verifyAccount(@RequestBody VerificationUserInscriptionDto verificationUserInscriptionDto) throws BusinessException, UnsupportedEncodingException {
        final Client client = clientAppService.getClientFromDatabase(verificationUserInscriptionDto.getReference());
        verifyAndValidateAccount(client, verificationUserInscriptionDto.getVerificationToken());
        final String token = jwtUtils.generateToken(client);
        return new JwtResponse(token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) throws BusinessException {
        final String email = request.getEmail().trim();
        Client client = clientAppService.getClientByEmail(email);
        if (client == null) {
            throw new BusinessException("Aucun compte n'est associé à cet e-mail.", NOT_FOUND);
        }
        clientAppService.sendResetPasswordMail(client);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto) throws BusinessException {
        Client client = clientAppService.getClientByEmail(updatePasswordDto.getEmail());
        if (client == null) {
            throw new BusinessException("Aucun compte n'est associé à cet e-mail.", NOT_FOUND);
        }
        verifyAndValidateAccount(client, updatePasswordDto.getVerificationToken());
        clientAppService.updatePasswordClient(updatePasswordDto);
        return ResponseEntity.ok().build();
    }

    private void verifyAndValidateAccount(Client client, String verificationToken) throws BusinessException {
        if (verificationToken == null || !verificationToken.equals(client.getVerificationToken())) {
            throw new BusinessException("Le lien de vérification est invalide ou expiré.");
        }
        clientAppService.validateToken(client);
    }

    @GetMapping("/{reference}/search")
    @PreAuthorize("hasAuthority('BAILLEUR') or hasAuthority('ADMIN')")
    public ResponseEntity<List<ClientDto>> searchLocataires(
            @RequestParam String name
    ) {
        return ResponseEntity.ok(clientAppService.searchLocatairesByName(name));
    }


}

