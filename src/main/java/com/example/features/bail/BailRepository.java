package com.example.features.bail;

import com.example.features.appart.domain.entities.Appart;
import com.example.features.user.domain.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BailRepository extends JpaRepository<Bail, Long> {

    Optional<Bail> findByAppartAndDateSortieIsNull(Appart appart);

    List<Bail> findByAppartOrderByDateEntreeDesc(Appart appart);

    List<Bail> findByLocataire(Client locataire);

}
