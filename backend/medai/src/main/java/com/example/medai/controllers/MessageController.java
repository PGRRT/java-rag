//package com.example.medai.controllers;
//
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/api/v1/chats/{chatId}/messages")
//public class MessageController {
//    @GetMapping
//    public ResponseEntity<List<MessageResponse>> getAllMessages(
//            @PathVariable("chatId") String chatId
//    ) {
//        List<MessageResponse> messages = messageService.getAllMessages(chatId);
//        return ResponseEntity.ok(messages);
//    }
//
//    @GetMapping("/{messageId}")
//    public ResponseEntity<MessageResponse> getMessageById(
//            @PathVariable("chatId") String chatId,
//            @PathVariable("messageId") String messageId
//    ) {
//        MessageResponse message = messageService.getMessageById(chatId, messageId);
//        return ResponseEntity.ok(message);
//    }
//
//    @PostMapping
//    public ResponseEntity<MessageResponse> createMessage(
//            @PathVariable("chatId") String chatId,
//            @Valid @RequestBody MessageRequest request
//    ) {
//        MessageResponse created = messageService.createMessage(chatId, request);
//        return ResponseEntity.ok(created);
//    }
//
//    @DeleteMapping("/{messageId}")
//    public ResponseEntity<Void> deleteMessage(
//            @PathVariable("chatId") String chatId,
//            @PathVariable("messageId") String messageId
//    ) {
//        messageService.deleteMessage(chatId, messageId);
//        return ResponseEntity.noContent().build();
//    }
//}
