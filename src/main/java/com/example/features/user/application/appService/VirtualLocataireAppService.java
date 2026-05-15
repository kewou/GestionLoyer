package com.example.features.user.application.appService;

import com.example.exceptions.BusinessException;
import com.example.features.user.application.mapper.ClaimAccountDto;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.application.mapper.CreateVirtualLocataireDto;
import com.example.features.user.application.mapper.GenerateInvitationResponseDto;

import java.util.List;

public interface VirtualLocataireAppService {

    ClientDto createVirtual(CreateVirtualLocataireDto dto, String bailleurEmail) throws BusinessException;

    GenerateInvitationResponseDto generateInvitation(String refLocataire, String bailleurEmail) throws BusinessException;

    List<ClientDto> listMyVirtualLocataires(String bailleurEmail) throws BusinessException;

    ClientDto claim(ClaimAccountDto dto) throws BusinessException;
}
