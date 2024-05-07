/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services.impl;

import com.example.domain.dto.ClientDto;
import com.example.domain.entities.Client;
import com.example.domain.exceptions.NoClientFoundException;
import com.example.domain.mapper.ClientMapper;
import com.example.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author frup73532
 */
@Service
@Transactional
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public List<Client> getAllClient() {
        return clientRepository.findAll();
    }

    public Client register(Client client) throws Exception {
        if (!checkIfClientExist(client.getEmail())) {
            if (client.getReference() == null) {
                client.setReference(generateReference());
            }
            client.setPassword(encoder.encode(client.getPassword()));
            Set<String> roles = new HashSet<>();
            roles.add("LOCATAIRE");
            client.setRoles(roles);
            clientRepository.save(client);
            return client;
        } else {
            throw new Exception("Client with email " + client.getEmail() + " is already exist on database");
        }
    }


    public Client getClient(Long id) throws NoClientFoundException {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NoClientFoundException(id));
        logger.info("Client " + client.getEmail() + " is found");
        return client;
    }


    public Client getClientByReference(String reference) throws NoClientFoundException {
        Client client = clientRepository.findByReference(reference)
                .orElseThrow(() -> new NoClientFoundException(reference));
        logger.info("Client " + client.getEmail() + " is found");
        return client;
    }

    public Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }


    public Client update(ClientDto clientDto, String reference) throws NoClientFoundException {
        Client client = getClientByReference(reference);
        Client clientUpdate = ClientMapper.getMapper().entitie(clientDto);
        ClientMapper.getMapper().update(client, clientUpdate);
        clientRepository.save(client);
        return client;
    }

    public Client delete(String reference) throws NoClientFoundException {
        Client client = getClientByReference(reference);
        logger.info("Client " + client.getEmail() + " is found");
        clientRepository.delete(client);
        return client;
    }


    public boolean checkIfClientExist(String email) {
        return clientRepository.findByEmail(email) != null ? true : false;
    }

    private String generateReference() {
        Random r = new Random();
        String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        String ref = alphabet[r.nextInt(25)] + r.nextInt(9) + alphabet[r.nextInt(25)] + r.nextInt(9) + alphabet[r.nextInt(25)];
        return ref;
    }

    private Logger logger = LoggerFactory.getLogger(ClientService.class);

}
