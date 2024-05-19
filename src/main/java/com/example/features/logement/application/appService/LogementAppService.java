package com.example.features.logement.application.appService;

import com.example.exceptions.BusinessException;
import com.example.features.logement.application.mapper.LogementDto;
import com.example.features.logement.domain.entities.Logement;
import com.example.features.user.domain.entities.Client;

import java.util.List;

public interface LogementAppService {

    public List<Logement> getAllLogementByUser(Client bailleur);

    public Logement register(Logement logement);

    public Logement getUserLogementByRef(Client bailleur, String refLgt) throws BusinessException;

    public Logement getLogementByReference(String refLgt) throws BusinessException;

    public Logement updateLogementByReference(LogementDto logementDto, String refLgt) throws BusinessException;

    public void deleteByReference(String refLgt) throws BusinessException;

}
