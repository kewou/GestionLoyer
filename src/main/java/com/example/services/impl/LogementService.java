package com.example.services.impl;

import com.example.domain.dto.LogementDto;
import com.example.domain.entities.Client;
import com.example.domain.entities.Logement;
import com.example.domain.exceptions.BusinessException;
import com.example.domain.mapper.LogementMapper;
import com.example.repository.LogementRepository;
import com.example.utils.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.example.domain.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;

@Service
@Slf4j
@Transactional
public class LogementService {

    @Autowired
    LogementRepository logementRepository;

    public List<Logement> getAllLogementByUser(Client bailleur) {
        return logementRepository.findByClient(bailleur);
    }

    public Logement register(Logement logement) {
        if (logement.getReference() == null) {
            logement.setReference(GeneralUtils.generateReference());
        }
        logementRepository.save(logement);
        return logement;
    }

    public Logement getUserLogementByRef(Client bailleur, String refLgt) throws BusinessException {
        return logementRepository.findByClientAndReference(bailleur, refLgt)
                .orElseThrow(() -> new BusinessException(String.format("No logement found with this ref %s", refLgt), NOT_FOUND));
    }

    public Logement getLogementByReference(String refLgt) throws BusinessException {
        return logementRepository.findByReference(refLgt).
                orElseThrow(() -> new BusinessException(String.format("No logement found with this ref %s", refLgt), NOT_FOUND));
    }

    public Logement updateLogementByReference(LogementDto logementDto, String refLgt) throws BusinessException {
        Logement logement = getLogementByReference(refLgt);
        Logement logementUpdate = LogementMapper.getMapper().entitie(logementDto);
        LogementMapper.getMapper().update(logement, logementUpdate);
        logementRepository.save(logement);
        return logement;
    }

    public void deleteByReference(String refLgt) throws BusinessException {
        Logement logement = getLogementByReference(refLgt);
        log.info("Logement ref = " + logement.getReference() + " is found");
        logementRepository.deleteByReference(refLgt);
    }


}
