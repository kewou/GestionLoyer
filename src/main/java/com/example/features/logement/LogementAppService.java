package com.example.features.logement;

import com.example.exceptions.BusinessException;

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
