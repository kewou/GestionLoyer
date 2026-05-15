package com.example.features.user.application;

import com.example.exceptions.BusinessException;
import com.example.features.accueil.domain.services.AuthenticationService;
import com.example.features.user.application.appService.VirtualLocataireAppService;
import com.example.features.user.application.mapper.ClaimAccountDto;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.application.mapper.CreateVirtualLocataireDto;
import com.example.features.user.application.mapper.GenerateInvitationResponseDto;
import com.example.helper.ResponseHelper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/virtual")
public class VirtualLocataireController {

    private final VirtualLocataireAppService virtualLocataireAppService;
    private final AuthenticationService authenticationService;

    public VirtualLocataireController(VirtualLocataireAppService virtualLocataireAppService,
                                      AuthenticationService authenticationService) {
        this.virtualLocataireAppService = virtualLocataireAppService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BAILLEUR') or hasAuthority('ADMIN')")
    public ResponseEntity<ClientDto> createVirtual(@Valid @RequestBody CreateVirtualLocataireDto dto,
                                                   Errors errors) throws BusinessException {
        ResponseHelper.handle(errors);
        String bailleurEmail = authenticationService.getLoggedInUsername();
        return ResponseEntity.ok(virtualLocataireAppService.createVirtual(dto, bailleurEmail));
    }

    @PostMapping("/{reference}/invitation")
    @PreAuthorize("hasAuthority('BAILLEUR') or hasAuthority('ADMIN')")
    public ResponseEntity<GenerateInvitationResponseDto> generateInvitation(
            @NotBlank @PathVariable("reference") String reference) throws BusinessException {
        String bailleurEmail = authenticationService.getLoggedInUsername();
        return ResponseEntity.ok(virtualLocataireAppService.generateInvitation(reference, bailleurEmail));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAuthority('BAILLEUR') or hasAuthority('ADMIN')")
    public ResponseEntity<List<ClientDto>> listMine() throws BusinessException {
        String bailleurEmail = authenticationService.getLoggedInUsername();
        return ResponseEntity.ok(virtualLocataireAppService.listMyVirtualLocataires(bailleurEmail));
    }
}
