package com.example.features.logement;

import com.example.exceptions.BusinessException;
import com.example.exceptions.ValidationException;
import com.example.features.logement.application.appService.LogementAppService;
import com.example.features.logement.application.mapper.LogementDto;
import com.example.helper.ResponseHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users/{reference}/logements")
public class LogementController {

    private LogementAppService logementAppService;

    @Autowired
    public LogementController(LogementAppService logementAppService) {
        this.logementAppService = logementAppService;
    }

    @GetMapping("")
    @Operation(description = "Get list of all logement by user")
    public ResponseEntity<List<LogementDto>> getAllLogementByUser(@NotBlank @PathVariable("reference") String reference) throws BusinessException {
        return ResponseEntity.ok(logementAppService.getAllLogementByUser(reference));
    }


    @PostMapping("/create")
    public ResponseEntity<LogementDto> addNewLogement(@Valid @RequestBody LogementDto logementDto, Errors erros,
                                                      @NotBlank @PathVariable("reference") String reference) throws BusinessException {
        ResponseHelper.handle(erros);
        logementAppService.register(reference, logementDto);
        return ResponseEntity.created(URI.create("/users/" + reference + "/logements/")).body(logementDto);
    }


    @GetMapping("/{refLgt}")
    public ResponseEntity<LogementDto> getUserLogementByRef(
            @Parameter(description = "refLgt of Logement") @NotBlank @PathVariable("reference") String refUser,
            @NotBlank @PathVariable("refLgt") String refLgt) throws BusinessException {
        return ResponseEntity.ok(logementAppService.getUserLogementByRef(refUser, refLgt));
    }

    @PutMapping("/{refLgt}")
    public ResponseEntity<LogementDto> updateLogementByRef(@RequestBody LogementDto logementDto, Errors erros,
                                                           @Parameter(description = "reference of Logement")
                                                           @NotBlank @PathVariable("refLgt") String refLgt)
            throws ValidationException, BusinessException {
        ResponseHelper.handle(erros);
        return ResponseEntity.ok(logementAppService.updateLogementByReference(logementDto, refLgt));
    }

    @DeleteMapping(path = "/{refLgt}")
    public ResponseEntity<Void> deleteLogementByRef(@NotBlank @PathVariable("refLgt") String refLgt) throws BusinessException {
        logementAppService.deleteByReference(refLgt);
        return ResponseEntity.noContent().build();
    }


}