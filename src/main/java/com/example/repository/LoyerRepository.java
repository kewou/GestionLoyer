package com.example.repository;

import com.example.domain.entities.Appart;
import com.example.domain.entities.Loyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoyerRepository extends JpaRepository<Loyer, Long> {

    @Query("SELECT l FROM Loyer l WHERE MONTH(l.dateLoyer) = :month AND l.appart = :appart")
    Optional<Loyer> findByMonthAndAppart(@Param("month") int month, @Param("appart") Appart appart);

    @Query("SELECT l FROM Loyer l WHERE l.appart = :appart")
    List<Loyer> findByAppart(@Param("appart") Appart appart);

    @Query("SELECT l FROM Loyer l WHERE l.isOk=false AND l.appart = :appart")
    List<Loyer> findByIsKo(@Param("appart") Appart appart);

}
