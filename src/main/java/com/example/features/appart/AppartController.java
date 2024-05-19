package com.example.features.appart;

import com.example.exceptions.BusinessException;
import com.example.exceptions.ValidationException;
import com.example.features.appart.application.appService.AppartAppService;
import com.example.features.appart.application.mapper.AppartDto;
import com.example.features.appart.application.mapper.AppartMapper;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.logement.application.appService.LogementAppService;
import com.example.features.logement.domain.entities.Logement;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.domain.entities.Client;
import com.example.helper.ResponseHelper;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/users/{reference}/logements/{refLgt}/apparts")
public class AppartController {

    private ClientAppService clientAppService;
    private LogementAppService logementAppService;
    private AppartAppService appartAppService;

    @Autowired
    public AppartController(ClientAppService clientAppService,
                            LogementAppService logementAppService,
                            AppartAppService appartAppService) {
        this.clientAppService = clientAppService;
        this.logementAppService = logementAppService;
        this.appartAppService = appartAppService;
    }


    @PostMapping("/create")
    public ResponseEntity<AppartDto> addNewAppartement(@Valid @RequestBody AppartDto dto, Errors erros,
                                                       @NotBlank @PathVariable("refLgt") String refLgt) throws BusinessException {
        ResponseHelper.handle(erros);
        Logement logement = logementAppService.getLogementByReference(refLgt);
        Appart appart = AppartMapper.getMapper().entitie(dto);
        appart.setLogement(logement);
        appart.setBailleur(logement.getClient());
        appartAppService.register(appart);
        URI uri = URI.create("/users/" + "/logements/" + refLgt + "/apparts");
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping("")
    @Operation(description = "Get list of all appart by logement")
    public ResponseEntity<List<AppartDto>> getAllAppartByLogement(@NotBlank @PathVariable("refLgt") String refLgt) throws BusinessException {
        Logement logement = logementAppService.getLogementByReference(refLgt);
        List<AppartDto> dtoApparts = new ArrayList<>();
        appartAppService.getAllAppartByLogement(logement).forEach(appart -> {
                    AppartDto dto = AppartMapper.getMapper().dto(appart);
                    dtoApparts.add(dto);
                }
        );
        return dtoApparts.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(dtoApparts);
    }

    @GetMapping("/{refAppart}")
    public ResponseEntity<AppartDto> getAppartLogementByRef(
            @NotBlank @PathVariable("refLgt") String refLgt,
            @NotBlank @PathVariable("refAppart") String refAppart) throws BusinessException {
        Logement logement = logementAppService.getLogementByReference(refLgt);
        AppartDto dto = AppartMapper.getMapper().dto(appartAppService.getLogementApprtByRef(logement, refAppart));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{refAppart}")
    public ResponseEntity<AppartDto> updateAppartLogementByRef(
            @RequestBody AppartDto appartDto,
            Errors erros,
            @NotBlank @PathVariable("refAppart") String refAppart) throws ValidationException, BusinessException {
        ResponseHelper.handle(erros);
        AppartDto dto = AppartMapper.getMapper().dto(appartAppService.updateLogementByRef(appartDto, refAppart));
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(path = "/{refAppart}")
    public ResponseEntity<Void> deleteAppartById(@NotBlank @PathVariable("refAppart") String refAppart) throws BusinessException {
        appartAppService.deleteByRef(refAppart);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{refAppart}/nouveau-locataire/{referenceLocataire}")
    public ResponseEntity<AppartDto> updateAppartAssigneLocataire(
            @NotBlank @PathVariable("referenceLocataire") String referenceLocataire,
            @NotBlank @PathVariable("refAppart") String refAppart) throws ValidationException, BusinessException {
        Client locataire = clientAppService.getClientByReference(referenceLocataire);
        AppartDto dto = AppartMapper.getMapper().dto(appartAppService.updateAppartAssigneLocataire(refAppart, locataire));
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{refAppart}/sortir-locataire/{referenceLocataire}")
    public ResponseEntity<AppartDto> updateAppartSortLocataire(
            @NotBlank @PathVariable("referenceLocataire") String referenceLocataire,
            @NotBlank @PathVariable("refAppart") String refAppart) throws ValidationException, BusinessException {
        Client locataire = clientAppService.getClientByReference(referenceLocataire);
        AppartDto dto = AppartMapper.getMapper().dto(appartAppService.updateAppartSortirLocataire(refAppart));
        return ResponseEntity.ok(dto);
    }


}
