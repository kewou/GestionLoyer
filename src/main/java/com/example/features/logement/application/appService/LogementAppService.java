package com.example.features.logement.application.appService;

import com.example.exceptions.BusinessException;
import com.example.features.logement.application.mapper.LogementDto;
import com.example.features.logement.domain.entities.Logement;

import java.util.List;

public interface LogementAppService {

    public List<LogementDto> getAllLogementByUser(String reference) throws BusinessException;

    public LogementDto register(String reference, LogementDto logementDto) throws BusinessException;

    public LogementDto getUserLogementByRef(String refUser, String refLgt) throws BusinessException;

    public LogementDto getLogementByReference(String refLgt) throws BusinessException;

    public LogementDto updateLogementByReference(LogementDto logementDto, String refLgt) throws BusinessException;

    public Logement getLogementFromDatabase(String refLgt) throws BusinessException;

    public void deleteByReference(String refLgt) throws BusinessException;

}
