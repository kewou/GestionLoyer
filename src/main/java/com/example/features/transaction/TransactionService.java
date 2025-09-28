package com.example.features.transaction;

import com.example.exceptions.BusinessException;
import com.example.features.appart.application.appService.AppartAppService;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.user.application.appService.ClientAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class TransactionService {

    private final AppartAppService appartAppService;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionService(ClientAppService clientAppService, TransactionRepository transactionRepository,
                              AppartAppService appartAppService, TransactionMapper transactionMapper) {
        this.appartAppService = appartAppService;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    public List<TransactionDto> getAllTransactionByAppart(String refAppart) throws BusinessException {
        Appart appart = appartAppService.getAppartFromDatabase(refAppart);
        return transactionRepository.findByBail_Appart(appart).stream()
                .map(transactionMapper::dto)
                .collect(Collectors.toList());
    }
    
}
