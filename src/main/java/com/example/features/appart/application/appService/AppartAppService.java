package com.example.features.appart.application.appService;

import com.example.exceptions.BusinessException;
import com.example.features.appart.application.mapper.AppartDto;
import com.example.features.appart.domain.entities.Appart;

import java.util.List;

public interface AppartAppService {

    public List<AppartDto> getAllAppartByLogement(String refLgt) throws BusinessException;

    public AppartDto register(String refLgt, AppartDto appartDto) throws BusinessException;

    public AppartDto getLogementApprtByRef(String refLgt, String refAppart) throws BusinessException;

    public AppartDto getAppartByRef(String refAppart) throws BusinessException;

    public AppartDto updateLogementByRef(AppartDto appartDto, String refAppart) throws BusinessException;

    public void deleteByRef(String refAppart) throws BusinessException;

    public Appart getAppartFromDatabase(String refAppart) throws BusinessException;

    public AppartDto updateAppartAssigneLocataire(String refAppart, String refLgt) throws BusinessException;

    public AppartDto updateAppartSortirLocataire(String refAppart) throws BusinessException;
}
