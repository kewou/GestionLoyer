package com.example.features.transaction.application;

import com.example.exceptions.BusinessException;
import com.example.features.transaction.application.appService.TransactionAppService;
import com.example.features.transaction.application.mapper.TransactionDto;
import com.example.helper.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;

@RestController
@RequestMapping("/users/{reference}/apparts")
public class TransactionController {

    private TransactionAppService transactionAppService;

    @Autowired
    public TransactionController(TransactionAppService transactionAppService) {
        this.transactionAppService = transactionAppService;
    }

    @PostMapping("/{refAppart}/nouvelle-transaction")
    public ResponseEntity<TransactionDto> addNewTransaction(@Valid @RequestBody TransactionDto transactionDto, Errors erros,
                                                            @NotBlank @PathVariable("reference") String reference,
                                                            @NotBlank @PathVariable("refAppart") String refAppart) throws BusinessException {
        URI uri = URI.create("/users/" + "/ref/" + "/apparts");
        ResponseHelper.handle(erros);
        return ResponseEntity.created(uri).body(transactionAppService.register(reference, transactionDto, refAppart));
    }


}
