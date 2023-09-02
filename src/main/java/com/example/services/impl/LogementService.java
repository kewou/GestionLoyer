package com.example.services.impl;

import com.example.domain.entities.Logement;
import com.example.domain.exceptions.NoLogementFoundProblem;
import com.example.repository.LogementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class LogementService {

    @Autowired
    LogementRepository logementRepository;


    public Logement getLogement(Long id) {
        return logementRepository.findById(id).
                orElseThrow(() -> new NoLogementFoundProblem(id));
    }


    public List<Logement> getAllLogement() {
        List<Logement> logements = new ArrayList<Logement>();
        logementRepository.findAll().forEach(lgt -> logements.add(lgt));
        return logements;
    }


    public void delete(Long id) {
        Logement lgt = getLogement(id);
        logementRepository.delete(lgt);
    }


    public void addOrUpdate(Logement logement) {
        logementRepository.save(logement);
    }

}
