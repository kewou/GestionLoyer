package com.example.repository;

import com.example.domain.entities.Visiteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "visiteurs", path = "visiteurs")
public interface VisiteurRepository extends JpaRepository<Visiteur, Long>, JpaSpecificationExecutor<Visiteur> {

}
