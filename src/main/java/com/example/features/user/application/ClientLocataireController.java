package com.example.features.user.application;


import com.example.exceptions.BusinessException;
import com.example.features.accueil.domain.services.AuthenticationService;
import com.example.features.bail.BailService;
import com.example.features.user.domain.services.impl.ClientService;
import com.example.security.SecurityRule;
import com.example.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Joel NOUMIA
 */
@RestController
@RequestMapping("/locataire/users")
public class ClientLocataireController extends ClientController {

    @Autowired
    public ClientLocataireController(ClientService clientAppService, AuthenticationService authenticationService, JWTUtils jwtUtils, BailService bailService) {
        super(clientAppService, authenticationService, jwtUtils, bailService);
    }

    /**
     * Lie le compte réel du locataire à un compte lite via un code de liaison.
     */
    @PostMapping("/{reference}/link-lite-account")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    public ResponseEntity<Map<String, String>> linkLiteAccount(
            @PathVariable String reference,
            @RequestBody Map<String, String> body) throws BusinessException {
        String linkingCode = body.get("linkingCode");
        if (linkingCode == null || linkingCode.isBlank()) {
            throw new BusinessException("Le code de liaison est requis.", BusinessException.BusinessErrorType.OTHER);
        }
        clientService.linkLiteAccount(reference, linkingCode);
        return ResponseEntity.ok(Map.of("message", "Compte lié avec succès. Votre historique est maintenant disponible."));
    }
}

