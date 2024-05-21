package com.example.domain.exceptions;

public class BusinessException extends Exception {

    private final BusinessErrorType type;

    public BusinessException(String message, BusinessErrorType type) {
        super(message);
        this.type = type;
    }

    public BusinessException(String message) {
        this(message, BusinessErrorType.OTHER);
    }

    public enum BusinessErrorType {


        NOT_FOUND("not found"),
        OTHER("other"),
        INTERNAL_SERVER_ERROR("internal error");

        private final String code;

        BusinessErrorType(String code) {

            this.code = code;
        }
    }

    public boolean isNotFoundType() {
        return type == BusinessErrorType.NOT_FOUND;
    }

    public boolean isOtherType() {
        return type == BusinessErrorType.OTHER;
    }

    public boolean isInternalErrorType() {
        return type == BusinessErrorType.INTERNAL_SERVER_ERROR;
    }
}
