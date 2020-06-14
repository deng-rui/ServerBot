package com.github.dr.serverbot.data.global;

import static com.github.dr.serverbot.util.file.LoadConfig.loadstring;

public class Config {
	public static String Server_Url;

	public static String BAIDU_TR_ID;
	public static String BAIDU_TR_KEY;
	public static String BAIDU_OCR_ID;
	public static String BAIDU_OCR_KEY;
	public static String BAIDU_OCR_ACT;
	public static long BAIDU_OCR_ACT_TIME;
	public static String TOPT_KEY = null;
	public static String DB_IP = null;
	public static String DB_PORT = null;
	public static String DB_NAME = null;
	public static String DB_USER = null;
	public static String DB_PASSWD = null;

    public static void laodConfig() {
    	BAIDU_TR_ID  = loadstring("BAIDU_TR_ID");
    	BAIDU_TR_KEY = loadstring("BAIDU_TR_KEY");
    	BAIDU_OCR_ID = loadstring("BAIDU_OCR_ID");
    	BAIDU_OCR_KEY= loadstring("BAIDU_OCR_KEY");
    	TOPT_KEY     = loadstring("TOPT_KEY");
		DB_IP 		 = loadstring("DB_Mariadb.IP");
		DB_PORT 	 = loadstring("DB_Mariadb.Port");
		DB_NAME 	 = loadstring("DB_Mariadb.Name");
		DB_USER 	 = loadstring("DB_Mariadb.User");
		DB_PASSWD 	 = loadstring("DB_Mariadb.Passwd");
	}

}