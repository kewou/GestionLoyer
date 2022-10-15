package com.example.services;

import com.example.domain.entities.Logement;

import java.util.List;

public interface LogementService {

    Logement getLogement(Long id);

    List<Logement> getAllLogement();

    void delete(Long id);

    void addOrUpdate(Logement logement);
}
