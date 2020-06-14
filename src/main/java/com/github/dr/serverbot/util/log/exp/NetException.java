package com.github.dr.serverbot.util.log.exp;

public class NetException extends Exception {
    public NetException(String type) {
		super(com.github.dr.serverbot.util.log.ErrorCode.valueOf(type).getError());
	}
}