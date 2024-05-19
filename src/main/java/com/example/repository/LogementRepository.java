package com.example.repository;

import com.example.domain.entities.Client;
import com.example.domain.entities.Logement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LogementRepository extends JpaRepository<Logement, Long> {

    List<Logement> findByClient(Client client);

    Optional<Logement> findByReference(String reference);

    Optional<Logement> findByClientAndReference(Client bailleur, String refLgt);

    void deleteByReference(String reference);

}
