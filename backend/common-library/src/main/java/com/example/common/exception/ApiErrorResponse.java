package com.example.common.exception;

import com.example.common.exception.FieldError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiErrorResponse {
    private String message;
    private int status;
    private List<FieldError> fieldErrors;
}
