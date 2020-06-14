package com.github.dr.serverbot.data.db;

public class PlayerData {
    public String uuid;
    public String user;
    public String name;
    public String mail;
	//

    public long ip;
    public int gmt;
    public String country;
    public byte timeFormat;
    public String language;
    public long lastLogin;
    public int kickCount;
	//int Sensitive;

    public boolean translate;
    public int level;
	// MAX = 32767

    public short exp;
    public long reqexp;
    public long reqtotalExp;
    public long playTime;
    public int pvpwinCount;
    public int pvploseCount;
    public int authority;
    public long authorityEffectiveTime;
    public long lastChat;
    public int deadCount;
    public int killCount;
    public int joinCount;
    public int breakCount;
	//

    public int buildCount;
    public int dismantledCount;
    public int cumulativeBuild;
    public int pipeBuild;
	/* */

    public boolean online;
    public String passwordHash;
    public String csprng;

    public PlayerData(String a) {
    	uuid = a;
    }
}
	