package com.example.features.user.application.mapper;

import com.example.features.user.domain.entities.Client;
import org.mapstruct.*;

import java.util.HashSet;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = "spring")
public interface ClientMapper {

    @AfterMapping
    default void initializeCollections(@MappingTarget Client client) {
        if (client != null) {
            if (client.getRoles() == null) {
                client.setRoles(new HashSet<>());
            }
            if (client.getLogements() == null) {
                client.setLogements(new HashSet<>());
            }
            if (client.getBaux() == null) {
                client.setBaux(new HashSet<>());
            }
        }
    }

    Client entitie(ClientDto clientDto);

    @Mapping(target = "password", ignore = true)
    ClientDto dto(Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "logements", ignore = true)
    @Mapping(target = "baux", ignore = true)
    @Mapping(target = "bauxActifs", ignore = true)
    @Mapping(target = "isEnabled", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "verificationToken", ignore = true)
    void update(@MappingTarget Client entity, Client updateEntity);
}
