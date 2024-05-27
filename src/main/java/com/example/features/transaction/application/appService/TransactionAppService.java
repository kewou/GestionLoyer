package com.example.features.transaction.application.appService;

import com.example.exceptions.BusinessException;
import com.example.features.transaction.application.mapper.TransactionDto;

public interface TransactionAppService {

    public TransactionDto register(String refUser, TransactionDto transactionDto, String refAppart) throws BusinessException;

}
