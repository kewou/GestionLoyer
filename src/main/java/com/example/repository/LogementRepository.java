package com.example.repository;

import com.example.entities.Logement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogementRepository extends CrudRepository<Logement, Long> {
}
