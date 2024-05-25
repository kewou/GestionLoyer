package com.example.features.appart;

import com.example.exceptions.BusinessException;
import com.example.exceptions.ValidationException;
import com.example.features.appart.application.appService.AppartAppService;
import com.example.features.appart.application.mapper.AppartDto;
import com.example.helper.ResponseHelper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users/{reference}/logements/{refLgt}/apparts")
public class AppartController {

    private AppartAppService appartAppService;

    @Autowired
    public AppartController(AppartAppService appartAppService) {
        this.appartAppService = appartAppService;
    }

    @GetMapping("")
    @Operation(description = "Get list of all appart by logement")
    public ResponseEntity<List<AppartDto>> getAllAppartByLogement(@NotBlank @PathVariable("refLgt") String refLgt) throws BusinessException {
        return ResponseEntity.ok(appartAppService.getAllAppartByLogement(refLgt));
    }


    @PostMapping("/create")
    public ResponseEntity<AppartDto> addNewAppartement(@Valid @RequestBody AppartDto appartDto, Errors erros,
                                                       @NotBlank @PathVariable("refLgt") String refLgt) throws BusinessException {
        ResponseHelper.handle(erros);
        appartAppService.register(refLgt, appartDto);
        return ResponseEntity.created(URI.create("/users/" + "/logements/" + refLgt + "/apparts")).body(appartDto);
    }

    @GetMapping("/{refAppart}")
    public ResponseEntity<AppartDto> getAppartLogementByRef(
            @NotBlank @PathVariable("refLgt") String refLgt,
            @NotBlank @PathVariable("refAppart") String refAppart) throws BusinessException {
        return ResponseEntity.ok(appartAppService.getLogementApprtByRef(refLgt, refAppart));
    }

    @PutMapping("/{refAppart}")
    public ResponseEntity<AppartDto> updateAppartLogementByRef(
            @RequestBody AppartDto appartDto, Errors erros,
            @NotBlank @PathVariable("refAppart") String refAppart) throws ValidationException, BusinessException {
        ResponseHelper.handle(erros);
        return ResponseEntity.ok(appartAppService.updateLogementByRef(appartDto, refAppart));
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
        return ResponseEntity.ok(appartAppService.updateAppartAssigneLocataire(refAppart, referenceLocataire));
    }

    @PatchMapping("/{refAppart}/sortir-locataire/{referenceLocataire}")
    public ResponseEntity<AppartDto> updateAppartSortLocataire(
            @NotBlank @PathVariable("refAppart") String refAppart) throws ValidationException, BusinessException {
        return ResponseEntity.ok(appartAppService.updateAppartSortirLocataire(refAppart));
    }


}
