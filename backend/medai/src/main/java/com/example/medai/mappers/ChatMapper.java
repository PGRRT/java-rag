package com.example.medai.mappers;


import com.example.medai.domain.dto.chat.request.CreateChatRequest;
import com.example.medai.domain.dto.chat.response.CreateChatResponse;
import com.example.medai.domain.entities.Chat;
import com.example.medai.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatMapper {
    Chat toEntity(CreateChatRequest chatDTO);

    CreateChatResponse toResponse(Chat chat);
}
