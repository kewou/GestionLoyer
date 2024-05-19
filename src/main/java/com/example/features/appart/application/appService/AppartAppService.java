package com.example.features.appart.application.appService;

import com.example.exceptions.BusinessException;
import com.example.features.appart.application.mapper.AppartDto;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.logement.domain.entities.Logement;
import com.example.features.user.domain.entities.Client;

import java.util.List;

public interface AppartAppService {

    public Appart register(Appart appart);

    public List<Appart> getAllAppartByLogement(Logement logement);

    public Appart getLogementApprtByRef(Logement logement, String refAppart) throws BusinessException;

    public Appart getAppartByRef(String refAppart) throws BusinessException;

    public Appart updateLogementByRef(AppartDto appartDto, String refAppart) throws BusinessException;

    public void deleteByRef(String refAppart) throws BusinessException;

    public List<AppartDto> rechercherSuggestionsParNom(String term);

    public Appart updateAppartAssigneLocataire(String refAppart, Client locataire) throws BusinessException;

    public Appart updateAppartSortirLocataire(String refAppart) throws BusinessException;
}
