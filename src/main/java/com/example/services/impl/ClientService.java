/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services.impl;

import com.example.domain.dto.ClientDto;
import com.example.domain.entities.Client;
import com.example.domain.exceptions.NoUserFoundProblem;
import com.example.domain.mapper.ClientMapper;
import com.example.repository.ClientRepository;
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


    public Client getClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NoUserFoundProblem(id));
        return client;
    }


    public Client getClientByReference(String reference) {
        Client client = clientRepository.findByReference(reference)
                .orElseThrow(() -> new NoUserFoundProblem(reference));
        return client;
    }


    public Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }


    public void delete(String reference) {
        clientRepository.deleteByReference(reference);
    }


    public Client update(ClientDto clientDto, String reference) {
        Client client = getClientByReference(reference);
        Client clientUpdate = ClientMapper.getMapper().dtoToClient(clientDto);
        ClientMapper.getMapper().update(client, clientUpdate);
        clientRepository.save(client);
        return client;
    }


    public ClientDto register(ClientDto dto) throws Exception {
        if (!checkIfClientExist(dto.getEmail())) {
            Client client = ClientMapper.getMapper().dtoToClient(dto);
            if (client.getReference() == null) {
                client.setReference(generateReference());
            }
            client.setPassword(encoder.encode(dto.getPassword()));
            Set<String> roles = new HashSet<>();
            roles.add("LOCATAIRE");
            client.setRoles(roles);
            clientRepository.save(client);
            return ClientMapper.getMapper().clientToClientDto(client);
        } else {
            throw new Exception("Client is already exist on database");
        }
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

}
