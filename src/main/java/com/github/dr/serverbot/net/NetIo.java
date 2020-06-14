package com.github.dr.serverbot.net;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author Dr
 */
public class NetIo {
	public static Net.Host readServerData(String hostAddress, ByteBuffer buffer){
        String host = readString(buffer);
        String map = readString(buffer);
        int players = buffer.getInt();
        int wave = buffer.getInt();
        int version = buffer.getInt();
        String vertype = readString(buffer);
        Object gamemode = buffer.get();
        int limit = buffer.getInt();
        String description = readString(buffer);

        return new Net.Host(host, hostAddress, map, wave, players, version, vertype, gamemode, limit, description);
    }

    private static String readString(ByteBuffer buffer){
        short length = (short)(buffer.get() & 0xff);
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}