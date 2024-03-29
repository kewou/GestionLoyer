package com.example.domain.mapper;

import com.example.domain.dto.ClientDto;
import com.example.domain.entities.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientMapper {

    public static ClientMapper getMapper() {
        return Mappers.getMapper(ClientMapper.class);
    }

    ClientDto clientToClientDto(Client client);

    Client dtoToClient(ClientDto clientDto);

    @Mapping(target = "authorities", ignore = true)
    void update(@MappingTarget Client entity, Client updateEntity);
}
