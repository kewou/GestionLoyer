package com.example.features.appart.infra;

import com.example.features.appart.application.mapper.AppartDto;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.logement.domain.entities.Logement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Sbeezy
 */
@Repository
public interface AppartRepository extends JpaRepository<Appart, Long> {

    List<Appart> findByLogement(Logement logement);

    Optional<Appart> findByLogementAndReference(Logement logement, String refAppart);

    List<AppartDto> findSuggestionsByNomStartingWithIgnoreCase(String term);

    Optional<Appart> findByReference(String reference);

    void deleteByReference(String reference);
}
