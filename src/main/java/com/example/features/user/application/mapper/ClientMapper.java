package com.example.features.user.application.mapper;

import com.example.features.user.domain.entities.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = "spring")
public interface ClientMapper {

    public static ClientMapper getMapper() {
        return Mappers.getMapper(ClientMapper.class);
    }

    Client entitie(ClientDto clientDto);

    @Mapping(target = "password", ignore = true)
    ClientDto dto(Client client);

    @Mapping(target = "authorities", ignore = true)
    void update(@MappingTarget Client entity, Client updateEntity);
}
