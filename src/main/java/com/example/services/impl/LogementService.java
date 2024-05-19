package com.example.services.impl;

import com.example.domain.dto.LogementDto;
import com.example.domain.entities.Client;
import com.example.domain.entities.Logement;
import com.example.domain.exceptions.BusinessException;
import com.example.domain.mapper.LogementMapper;
import com.example.repository.LogementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.example.domain.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;

@Service
@Transactional
public class LogementService {

    @Autowired
    LogementRepository logementRepository;

    public List<Logement> getAllLogementByUser(Client bailleur) {
        return logementRepository.findByClient(bailleur);
    }

    public Logement register(Logement logement) {
        logementRepository.save(logement);
        return logement;
    }

    public Logement getUserLogementById(Client bailleur, Long id) throws BusinessException {
        return logementRepository.findByClientAndId(bailleur, id)
                .orElseThrow(() -> new BusinessException(String.format("No logement found with this id %d", id), NOT_FOUND));
    }

    public Logement getLogementById(Long id) throws BusinessException {
        return logementRepository.findById(id).
                orElseThrow(() -> new BusinessException(String.format("No logement found with this id %d", id), NOT_FOUND));
    }

    public Logement updateLogementById(LogementDto logementDto, Long id) throws BusinessException {
        Logement logement = getLogementById(id);
        Logement logementUpdate = LogementMapper.getMapper().entitie(logementDto);
        LogementMapper.getMapper().update(logement, logementUpdate);
        logementRepository.save(logement);
        return logement;
    }

    public void deleteById(Long id) throws BusinessException {
        Logement logement = getLogementById(id);
        logger.info("Logement id = " + logement.getId() + " is found");
        logementRepository.deleteById(id);
    }


    private Logger logger = LoggerFactory.getLogger(LogementService.class);


}
