package com.github.dr.serverbot.data.db;

public class PlayerData {
	// 奇怪的public
	public String UUID;
	public String User;
	public String NAME;
	public String Mail;
	//
	public long IP;
	public int GMT;
	public String Country;
	public byte Time_format;
	public String Language;
	public long LastLogin;
	public int Kickcount;
	//int Sensitive;
	public boolean Translate;
	public int Level;
	// MAX = 32767
	public short Exp;
	public long Reqexp;
	public long Reqtotalexp;
	public long Playtime;
	public int Pvpwincount;
	public int Pvplosecount;
	public int Authority;
	public long Authority_effective_time;
	public long Lastchat;
	public int Deadcount;
	public int Killcount;
	public int Joincount;
	public int Breakcount;
	//
	public int Buildcount;
	public int Dismantledcount;
	public int Cumulative_build;
	public int Pipe_build;
	/* */
	public boolean Online;
	public String PasswordHash;
	public String CSPRNG;
	//
	public boolean Login;
	public long Jointime;
	public long Backtime;
	public Object Info;
}
	