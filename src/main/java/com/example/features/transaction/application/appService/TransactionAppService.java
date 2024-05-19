package com.example.features.transaction.application.appService;

import com.example.exceptions.BusinessException;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.transaction.domain.entities.Transaction;
import com.example.features.user.domain.entities.Client;

public interface TransactionAppService {

    public Transaction register(Client bailleur, Transaction transaction, Appart appart) throws BusinessException;

}
