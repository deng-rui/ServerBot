package com.github.dr.serverbot.util.log.exp;

public class VariableException extends RuntimeException {
    public VariableException(String type) {
        super(com.github.dr.serverbot.util.log.ErrorCode.valueOf(type).getError());
    }
}