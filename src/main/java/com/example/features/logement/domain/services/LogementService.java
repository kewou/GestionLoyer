package com.example.features.logement.domain.services;

import com.example.exceptions.BusinessException;
import com.example.features.logement.application.appService.LogementAppService;
import com.example.features.logement.application.mapper.LogementDto;
import com.example.features.logement.application.mapper.LogementMapper;
import com.example.features.logement.domain.entities.Logement;
import com.example.features.logement.infra.LogementRepository;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.domain.entities.Client;
import com.example.utils.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;

@Service
@Slf4j
@Transactional
public class LogementService implements LogementAppService {

    private final ClientAppService clientAppService;
    private final LogementRepository logementRepository;

    @Autowired
    public LogementService(LogementRepository logementRepository, ClientAppService clientAppService) {
        this.clientAppService = clientAppService;
        this.logementRepository = logementRepository;
    }

    public List<LogementDto> getAllLogementByUser(String reference) throws BusinessException {
        Client bailleur = clientAppService.getClientFromDatabase(reference);
        return logementRepository.findByClient(bailleur).stream()
                .map(LogementMapper.getMapper()::dto)
                .collect(Collectors.toList());
    }


    public LogementDto register(String reference, LogementDto logementDto) throws BusinessException {
        Client client = clientAppService.getClientFromDatabase(reference);
        Logement logement = LogementMapper.getMapper().entitie(logementDto);
        if (logement.getReference() == null) {
            logement.setReference(GeneralUtils.generateReference());
        }
        logement.setClient(client);
        logementRepository.save(logement);
        log.info(LOGEMENT_LOG + logement.getReference() + " is created");
        return logementDto;
    }


    public LogementDto getUserLogementByRef(String refUser, String refLgt) throws BusinessException {
        Client bailleur = clientAppService.getClientFromDatabase(refUser);
        Logement lgt = logementRepository.findByClientAndReference(bailleur, refLgt)
                .orElseThrow(() -> new BusinessException(String.format("No logement found with this ref %s", refLgt), NOT_FOUND));
        return LogementMapper.getMapper().dto(lgt);
    }

    public LogementDto getLogementByReference(String refLgt) throws BusinessException {
        Logement lgt = this.getLogementFromDatabase(refLgt);
        return LogementMapper.getMapper().dto(lgt);
    }

    public LogementDto updateLogementByReference(LogementDto logementDto, String refLgt) throws BusinessException {
        Logement logement = this.getLogementFromDatabase(refLgt);
        Logement logementUpdate = LogementMapper.getMapper().entitie(logementDto);
        LogementMapper.getMapper().update(logement, logementUpdate);
        logementRepository.save(logement);
        log.info(LOGEMENT_LOG + logement.getReference() + " is saved");
        return LogementMapper.getMapper().dto(logement);
    }

    public void deleteByReference(String refLgt) throws BusinessException {
        Logement logement = getLogementFromDatabase(refLgt);
        logementRepository.deleteByReference(refLgt);
        log.info(LOGEMENT_LOG + logement.getReference() + " is deleted");
    }

    public Logement getLogementFromDatabase(String refLgt) throws BusinessException {
        Logement lgt = logementRepository.findByReference(refLgt).
                orElseThrow(() -> new BusinessException(String.format("No logement found with this ref %s", refLgt), NOT_FOUND));
        log.info(LOGEMENT_LOG + lgt.getReference() + " is found");
        return lgt;
    }


    private static final String LOGEMENT_LOG = "Logement ref = ";

}
