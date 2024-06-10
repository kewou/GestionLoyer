package com.example.features.appart.application;

import com.example.exceptions.BusinessException;
import com.example.exceptions.ValidationException;
import com.example.features.appart.application.appService.AppartAppService;
import com.example.features.appart.application.mapper.AppartDto;
import com.example.helper.ResponseHelper;
import com.example.security.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users/{reference}/logements/{refLgt}/apparts")
public class AppartController {

    private final AppartAppService appartAppService;

    @Autowired
    public AppartController(AppartAppService appartAppService) {
        this.appartAppService = appartAppService;
    }

    @GetMapping("")
    @PreAuthorize(SecurityRule.OWNER_LOGEMENT_OR_ADMIN)
    @Operation(description = "Get list of all appart by logement")
    public ResponseEntity<List<AppartDto>> getAllAppartOfLogement(
            @Parameter(description = "refUser of User") @NotBlank @PathVariable("reference") String refUser,
            @NotBlank @PathVariable("refLgt") String refLgt) throws BusinessException {
        return ResponseEntity.ok(appartAppService.getAllAppartByLogement(refLgt));
    }


    @PostMapping("/create")
    @PreAuthorize(SecurityRule.OWNER_APPART_OR_ADMIN)
    public ResponseEntity<AppartDto> addNewAppartement(@Valid @RequestBody AppartDto appartDto, Errors erros,
                                                       @NotBlank @PathVariable("refLgt") String refLgt) throws BusinessException {
        ResponseHelper.handle(erros);
        appartAppService.register(refLgt, appartDto);
        return ResponseEntity.created(URI.create("/users/" + "/logements/" + refLgt + "/apparts")).body(appartDto);
    }

    @GetMapping("/{refAppart}")
    @PreAuthorize(SecurityRule.OWNER_BAILLEUR_APPART_OR_ADMIN + "or" + SecurityRule.OWNER_LOCATAIRE_APPART_OR_ADMIN)
    public ResponseEntity<AppartDto> getOneAppartOfLogements(
            @Parameter(description = "refUser of User") @NotBlank @PathVariable("reference") String refUser,
            @NotBlank @PathVariable("refLgt") String refLgt,
            @NotBlank @PathVariable("refAppart") String refAppart) throws BusinessException {
        return ResponseEntity.ok(appartAppService.getLogementApprtByRef(refLgt, refAppart));
    }

    @PutMapping("/{refAppart}")
    @PreAuthorize(SecurityRule.OWNER_BAILLEUR_APPART_OR_ADMIN)
    public ResponseEntity<AppartDto> updateAppartLogementByRef(
            @RequestBody AppartDto appartDto, Errors erros,
            @Parameter(description = "refUser of User") @NotBlank @PathVariable("reference") String refUser,
            @NotBlank @PathVariable("refAppart") String refAppart) throws ValidationException, BusinessException {
        ResponseHelper.handle(erros);
        return ResponseEntity.ok(appartAppService.updateLogementByRef(appartDto, refAppart));
    }

    @DeleteMapping(path = "/{refAppart}")
    @PreAuthorize(SecurityRule.ADMIN)
    public ResponseEntity<Void> deleteAppartById(@NotBlank @PathVariable("refAppart") String refAppart) throws BusinessException {
        appartAppService.deleteByRef(refAppart);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{refAppart}/nouveau-locataire/{referenceLocataire}")
    @PreAuthorize(SecurityRule.OWNER_BAILLEUR_APPART_OR_ADMIN)
    public ResponseEntity<AppartDto> updateAppartAssigneLocataire(
            @Parameter(description = "refUser of User") @NotBlank @PathVariable("reference") String refUser,
            @NotBlank @PathVariable("referenceLocataire") String referenceLocataire,
            @NotBlank @PathVariable("refAppart") String refAppart) throws ValidationException, BusinessException {
        return ResponseEntity.ok(appartAppService.updateAppartAssigneLocataire(refAppart, referenceLocataire));
    }

    @PatchMapping("/{refAppart}/sortir-locataire/{referenceLocataire}")
    @PreAuthorize(SecurityRule.OWNER_BAILLEUR_APPART_OR_ADMIN)
    public ResponseEntity<AppartDto> updateAppartSortLocataire(
            @Parameter(description = "refUser of User") @NotBlank @PathVariable("reference") String refUser,
            @NotBlank @PathVariable("refAppart") String refAppart) throws ValidationException, BusinessException {
        return ResponseEntity.ok(appartAppService.updateAppartSortirLocataire(refAppart));
    }


}
