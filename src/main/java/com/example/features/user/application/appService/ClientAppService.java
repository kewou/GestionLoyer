package com.example.features.user.application.appService;

import com.example.exceptions.BusinessException;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.domain.entities.Client;

import java.util.List;

public interface ClientAppService {

    public List<Client> getAllClient();

    public Client register(Client client) throws BusinessException;

    public Client getClient(Long id) throws BusinessException;

    public Client getClientByReference(String reference) throws BusinessException;

    public Client getClientByEmail(String email);

    public Client update(ClientDto clientDto, String reference) throws BusinessException;

    public Client delete(String reference) throws BusinessException;

}
