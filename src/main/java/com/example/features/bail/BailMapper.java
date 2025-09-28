package com.example.features.bail;

import com.example.features.bail.dto.BailDto;
import com.example.features.user.application.mapper.ClientMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {ClientMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BailMapper {


    BailDto dto(Bail bail);

    @InheritInverseConfiguration
    @Mapping(target = "appart", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    Bail toEntity(BailDto bailDto);
}
