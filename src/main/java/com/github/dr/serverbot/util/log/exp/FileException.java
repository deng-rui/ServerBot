package com.github.dr.serverbot.util.log.exp;

public class FileException extends Exception {
    public FileException(String type) {
        super(com.github.dr.serverbot.util.log.ErrorCode.valueOf(type).getError());
    }
}