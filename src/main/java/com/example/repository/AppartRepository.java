package com.example.repository;

import com.example.domain.dto.AppartDto;
import com.example.domain.entities.Appart;
import com.example.domain.entities.Logement;
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

    Optional<Appart> findByLogementAndId(Logement logement, Long idAppart);

    List<AppartDto> findSuggestionsByNomStartingWithIgnoreCase(String term);
}
