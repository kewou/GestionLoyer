package com.example.domain.mapper;

import com.example.domain.dto.UserDto;
import com.example.domain.entities.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper( nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public interface UserMapper {

    public static UserMapper getMapper(){
        return Mappers.getMapper( UserMapper.class );
    }

    UserDto userToUserDto(User user);

    User dtoToUser(UserDto userDto);

    @Mapping( target = "authorities", ignore = true )
    void update(@MappingTarget User entity, User updateEntity);
}
