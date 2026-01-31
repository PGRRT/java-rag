package com.example.user.mapper;



import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "email", qualifiedByName = "toLowerCase")
    User toEntity(RegisterUserRequest userDTO);

    @Mapping(source = "role.name", target = "role")
    UserResponse toDto(User user);

    @Named("toLowerCase")
    default String toLowerCase(String value) {
        return value != null ? value.toLowerCase().trim() : null;
    }
}
