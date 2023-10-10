/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.repository;

import com.example.domain.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author frup73532
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByEmail(String email);

    Optional<Client> findByReference(String reference);

    void deleteByReference(String reference);

}
