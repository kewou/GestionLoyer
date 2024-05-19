package com.example.features.transaction.application.mapper;

import com.example.features.transaction.domain.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransactionMapper {

    public static TransactionMapper getMapper() {
        return Mappers.getMapper(TransactionMapper.class);
    }

    Transaction entitie(TransactionDto transactionDto);

    TransactionDto dto(Transaction transaction);

    void update(@MappingTarget Transaction entity, Transaction updateEntity);
}
