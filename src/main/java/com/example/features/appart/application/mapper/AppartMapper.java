package com.example.features.appart.application.mapper;

import com.example.features.appart.domain.entities.Appart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AppartMapper {

    public static AppartMapper getMapper() {
        return Mappers.getMapper(AppartMapper.class);
    }

    Appart entitie(AppartDto appartDto);

    @Mapping(target = "locataire.password", ignore = true)
    AppartDto dto(Appart appart);

    @Mapping(target = "loyers", ignore = true)
    void update(@MappingTarget Appart entity, Appart updateEntity);
}
