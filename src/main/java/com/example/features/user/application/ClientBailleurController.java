package com.example.features.user.application;


import com.example.exceptions.BusinessException;
import com.example.features.accueil.domain.services.AuthenticationService;
import com.example.features.bail.BailService;
import com.example.features.payment.enums.ModeEncaissement;
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
@RequestMapping("/bailleur/users")
public class ClientBailleurController extends ClientController {

    @Autowired
    public ClientBailleurController(ClientService clientAppService, AuthenticationService authenticationService, JWTUtils jwtUtils, BailService bailService) {
        super(clientAppService, authenticationService, jwtUtils, bailService);
    }

    /**
     * Récupère le mode d'encaissement du bailleur.
     */
    @GetMapping("/{reference}/parametres/mode-encaissement")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    public ResponseEntity<Map<String, String>> getModeEncaissement(@PathVariable String reference) throws BusinessException {
        ModeEncaissement mode = clientService.getModeEncaissement(reference);
        return ResponseEntity.ok(Map.of("modeEncaissement", mode.name()));
    }

    /**
     * Met à jour le mode d'encaissement du bailleur.
     */
    @PutMapping("/{reference}/parametres/mode-encaissement")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    public ResponseEntity<Map<String, String>> updateModeEncaissement(
            @PathVariable String reference,
            @RequestBody Map<String, String> body) throws BusinessException {
        ModeEncaissement mode = ModeEncaissement.valueOf(body.get("modeEncaissement"));
        clientService.updateModeEncaissement(reference, mode);
        return ResponseEntity.ok(Map.of("modeEncaissement", mode.name()));
    }
}



