package com.example.features.logement;

import com.example.exceptions.BusinessException;
import com.example.exceptions.ValidationException;
import com.example.features.logement.application.appService.LogementAppService;
import com.example.features.logement.application.mapper.LogementDto;
import com.example.features.logement.application.mapper.LogementMapper;
import com.example.features.logement.domain.entities.Logement;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.domain.entities.Client;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users/{reference}/logements")
public class LogementController {

    private ClientAppService clientAppService;
    private LogementAppService logementAppService;


    @Autowired
    public LogementController(ClientAppService clientAppService, LogementAppService logementAppService) {
        this.clientAppService = clientAppService;
        this.logementAppService = logementAppService;
    }


    @PostMapping("/create")
    public ResponseEntity<LogementDto> addNewLogement(@Valid @RequestBody LogementDto dto, Errors erros, @NotBlank @PathVariable("reference") String reference) throws BusinessException {
        ResponseHelper.handle(erros);
        Client bailleur = clientAppService.getClientByReference(reference);
        Logement logement = LogementMapper.getMapper().entitie(dto);
        logement.setClient(bailleur);
        logementAppService.register(logement);
        URI uri = URI.create("/users/" + bailleur.getReference() + "/logements/");
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping("")
    @Operation(description = "Get list of all logement by user")
    public ResponseEntity<List<LogementDto>> getAllLogementByUser(@NotBlank @PathVariable("reference") String reference) throws BusinessException {
        Client bailleur = clientAppService.getClientByReference(reference);
        List<LogementDto> dtoLogements = new ArrayList<>();
        logementAppService.getAllLogementByUser(bailleur).forEach(logement -> {
                    LogementDto dto = LogementMapper.getMapper().dto(logement);
                    dtoLogements.add(dto);
                }
        );
        return dtoLogements.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(dtoLogements);
    }

    @GetMapping("/{refLgt}")
    public ResponseEntity<LogementDto> getUserLogementByRef(
            @Parameter(description = "refLgt of Logement")
            @NotBlank @PathVariable("reference") String refUser,
            @NotBlank @PathVariable("refLgt") String refLgt) throws BusinessException {
        Client bailleur = clientAppService.getClientByReference(refUser);
        LogementDto dto = LogementMapper.getMapper().dto(logementAppService.getUserLogementByRef(bailleur, refLgt));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{refLgt}")
    public ResponseEntity<LogementDto> updateLogementByRef(@RequestBody LogementDto logementDto, Errors erros,
                                                           @Parameter(description = "reference of Logement")
                                                           @NotBlank @PathVariable("refLgt") String refLgt)
            throws ValidationException, BusinessException {
        ResponseHelper.handle(erros);
        LogementDto dto = LogementMapper.getMapper().dto(logementAppService.updateLogementByReference(logementDto, refLgt));
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(path = "/{refLgt}")
    public ResponseEntity<Void> deleteLogementByRef(@NotBlank @PathVariable("refLgt") String refLgt) throws BusinessException {
        logementAppService.deleteByReference(refLgt);
        return ResponseEntity.noContent().build();
    }


}
