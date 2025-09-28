package com.example.features.logement;

import com.example.exceptions.BusinessException;
import com.example.exceptions.ValidationException;
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
@RequestMapping("/bailleur/users/{reference}/logements")
public class LogementController {

    private final LogementAppService logementAppService;

    @Autowired
    public LogementController(LogementAppService logementAppService) {
        this.logementAppService = logementAppService;
    }

    @GetMapping("")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    @Operation(description = "Get list of all logement by user")
    public ResponseEntity<List<LogementDto>> getAllLogementByUser(@NotBlank @PathVariable("reference") String reference) throws BusinessException {
        return ResponseEntity.ok(logementAppService.getAllLogementByUser(reference));
    }


    @PostMapping("/create")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    public ResponseEntity<LogementDto> addNewLogement(@Valid @RequestBody LogementDto logementDto, Errors erros,
                                                      @NotBlank @PathVariable("reference") String reference) throws BusinessException {
        ResponseHelper.handle(erros);
        LogementDto logementDtoReponse = logementAppService.register(reference, logementDto);
        return ResponseEntity.created(URI.create("/users/" + reference + "/logements/")).body(logementDtoReponse);
    }


    @GetMapping("/{refLgt}")
    @PreAuthorize(SecurityRule.OWNER_LOGEMENT_OR_ADMIN)
    public ResponseEntity<LogementDto> getUserLogementByRef(
            @Parameter(description = "refLgt of Logement") @NotBlank @PathVariable("reference") String refUser,
            @NotBlank @PathVariable("refLgt") String refLgt) throws BusinessException {
        return ResponseEntity.ok(logementAppService.getUserLogementByRef(refUser, refLgt));
    }

    @PutMapping("/{refLgt}")
    @PreAuthorize(SecurityRule.OWNER_LOGEMENT_OR_ADMIN)
    public ResponseEntity<LogementDto> updateLogementByRef(@RequestBody LogementDto logementDto, Errors erros,
                                                           @Parameter(description = "refLgt of Logement") @NotBlank @PathVariable("reference") String refUser,
                                                           @Parameter(description = "reference of Logement")
                                                           @NotBlank @PathVariable("refLgt") String refLgt)
            throws ValidationException, BusinessException {
        ResponseHelper.handle(erros);
        return ResponseEntity.ok(logementAppService.updateLogementByReference(logementDto, refLgt));
    }

    @DeleteMapping("/{refLgt}")
    @PreAuthorize(SecurityRule.OWNER_LOGEMENT_OR_ADMIN)
    public ResponseEntity<Void> deleteLogementByRef(@NotBlank @PathVariable("refLgt") String refLgt,
                                                    @Parameter(description = "refLgt of Logement") @NotBlank @PathVariable("reference") String refUser) throws BusinessException {
        logementAppService.deleteByReference(refLgt);
        return ResponseEntity.noContent().build();
    }


}
