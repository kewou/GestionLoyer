package com.example.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonFormat
class ErrorCode {

    private final String rule;
    private final String field;
    private final String defaultMessage;
}
