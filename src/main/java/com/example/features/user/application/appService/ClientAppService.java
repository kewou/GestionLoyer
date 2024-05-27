package com.example.features.user.application.appService;

import com.example.exceptions.BusinessException;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.domain.entities.Client;

import java.util.List;

public interface ClientAppService {

    public List<ClientDto> getAllClient();

    public ClientDto register(ClientDto clientDto, String clientRole) throws BusinessException;

    public ClientDto getClientByReference(String reference) throws BusinessException;

    public Client getClientByEmail(String email);

    public ClientDto update(ClientDto clientDto, String reference) throws BusinessException;

    public void delete(String reference) throws BusinessException;

    public Client getClientFromDatabase(String reference) throws BusinessException;

}
