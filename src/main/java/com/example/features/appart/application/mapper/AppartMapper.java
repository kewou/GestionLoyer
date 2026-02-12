package com.example.features.appart.application.mapper;

import com.example.features.appart.domain.entities.Appart;
import com.example.features.bail.BailMapper;
import com.example.features.user.application.mapper.ClientMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ClientMapper.class, BailMapper.class}
)
public interface AppartMapper {

    Appart entitie(AppartDto appartDto);

    AppartDto dto(Appart appart);

    void update(@MappingTarget Appart entity, Appart updateEntity);

}
