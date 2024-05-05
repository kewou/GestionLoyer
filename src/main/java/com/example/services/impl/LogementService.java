package com.example.services.impl;

import com.example.domain.dto.LogementDto;
import com.example.domain.entities.Client;
import com.example.domain.entities.Logement;
import com.example.domain.exceptions.NoLogementFoundException;
import com.example.domain.mapper.LogementMapper;
import com.example.repository.LogementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class LogementService {

    @Autowired
    LogementRepository logementRepository;

    public List<Logement> getAllLogementByUser(Client bailleur) {
        return logementRepository.findByClient(bailleur);
    }

    public Logement register(Logement logement) throws Exception {
        logementRepository.save(logement);
        return logement;
    }

    public Logement getUserLogementById(Client bailleur, Long id) throws NoLogementFoundException {
        Logement logement = logementRepository.findByClientAndId(bailleur, id)
                .orElseThrow(() -> new NoLogementFoundException(id));
        return logement;
    }

    public Logement getLogementById(Long id) throws NoLogementFoundException {
        return logementRepository.findById(id).
                orElseThrow(() -> new NoLogementFoundException(id));
    }

    public Logement updateLogementById(LogementDto logementDto, Long id) throws NoLogementFoundException {
        Logement logement = getLogementById(id);
        Logement logementUpdate = LogementMapper.getMapper().entitie(logementDto);
        LogementMapper.getMapper().update(logement, logementUpdate);
        logementRepository.save(logement);
        return logement;
    }

    public void deleteById(Long id) throws NoLogementFoundException {
        Logement logement = getLogementById(id);
        logger.info("Logement id = " + logement.getId() + " is found");
        logementRepository.deleteById(id);
    }


    private Logger logger = LoggerFactory.getLogger(LogementService.class);


}
