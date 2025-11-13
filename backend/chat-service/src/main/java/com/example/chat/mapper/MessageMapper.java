package com.example.chat.mapper;

import com.example.chat.domain.dto.message.request.CreateMessageRequest;
import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.domain.entities.Message;
import com.example.chat.domain.enums.Sender;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {
    @Mapping(source = "sender", target = "sender", qualifiedByName = "mapStringToSender")
    Message toEntity(CreateMessageRequest createMessageRequest);

    MessageResponse toResponse(Message message);

    @Named("mapStringToSender")
    default Sender mapStringToSender(String sender) {
        try {
            return Sender.valueOf(sender.toUpperCase());
        } catch (Exception e) {
            return Sender.USER;
        }
    }
}

