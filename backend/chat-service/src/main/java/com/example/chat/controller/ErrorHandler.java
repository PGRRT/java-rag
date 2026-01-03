package com.example.chat.controller;

import com.example.common.exception.GlobalErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends GlobalErrorHandler {

}
