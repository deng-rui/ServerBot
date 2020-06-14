package com.github.dr.serverbot.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static com.github.dr.serverbot.net.NetIo.readServerData;

public class Net {

	public static void pingServer(Consumer<Host> listener, String ip, int port) {
		try {
            DatagramSocket socket = new DatagramSocket();
            socket.send(new DatagramPacket(new byte[]{-2, 1}, 2, InetAddress.getByName(ip), port));
            socket.setSoTimeout(2000);
            DatagramPacket packet = new DatagramPacket(new byte[256], 256);
            long start = System.currentTimeMillis();
            socket.receive(packet);
            ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
            Host host = readServerData(ip+":"+port, buffer);
            host.ping = (int) (System.currentTimeMillis() - start);
            listener.accept(host);
            socket.disconnect();
        } catch (Exception e) {
            listener.accept(new Host(null,null,null,0,0,0,null,null,0,null));
        }
	}

	

	public static class Host {
		public final String name;
		public final String address;
		public final String mapname, description;
		public final int wave;
		public final int players, playerLimit;
		public final int version;
		public final String versionType;
		public final String mode;
		public int ping, port;

	    public Host(String name, String address, String mapname, int wave, int players, int version, String versionType, Object mode, int playerLimit, String description){
	        this.name = name;
	        this.address = address;
	        this.players = players;
	        this.mapname = mapname;
	        this.wave = wave;
	        this.version = version;
	        this.versionType = versionType;
	        this.playerLimit = playerLimit;
	        // sur 0 pvp 3 sandbox 1 attack 2
			String temp = mode.toString();
	        this.mode = temp.contains("0") ? "0" : temp.contains("1") ? "1" : temp.contains("2") ? "2" : "3";
	        this.description = description;
	    }
	}

}