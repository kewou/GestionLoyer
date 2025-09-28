package com.example.features.transaction;

import com.example.features.bail.BailMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = "spring", uses = {BailMapper.class})
public interface TransactionMapper {

    Transaction entitie(TransactionDto transactionDto);

    TransactionDto dto(Transaction transaction);

    void update(@MappingTarget Transaction entity, Transaction updateEntity);
}
