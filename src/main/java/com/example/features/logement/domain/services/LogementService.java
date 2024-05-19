package com.example.features.logement.domain.services;

import com.example.exceptions.BusinessException;
import com.example.features.logement.application.appService.LogementAppService;
import com.example.features.logement.application.mapper.LogementDto;
import com.example.features.logement.application.mapper.LogementMapper;
import com.example.features.logement.domain.entities.Logement;
import com.example.features.logement.infra.LogementRepository;
import com.example.features.user.domain.entities.Client;
import com.example.utils.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.example.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;

@Service
@Slf4j
@Transactional
public class LogementService implements LogementAppService {

    private LogementRepository logementRepository;

    @Autowired
    public LogementService(LogementRepository logementRepository) {
        this.logementRepository = logementRepository;
    }

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
