package com.example.features.logement.application.mapper;

import com.example.features.logement.domain.entities.Logement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LogementMapper {

    public static LogementMapper getMapper() {
        return Mappers.getMapper(LogementMapper.class);
    }

    Logement entitie(LogementDto logementDto);

    LogementDto dto(Logement logement);

    void update(@MappingTarget Logement entity, Logement updateEntity);
}
