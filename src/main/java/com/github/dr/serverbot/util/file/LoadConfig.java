package com.github.dr.serverbot.util.file;

import java.io.*;
import java.util.*;
import java.text.MessageFormat;
//Java

/*
 *  Config.java
 *	Initialization.java
 */
public class LoadConfig {

	public static String customLoad(String input) {
		return customLoad(input,null);
	}

	public static String customLoad(String input,Object[] params) {
		Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(LoadConfig.class.getResourceAsStream("/language.properties"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }
		try {
			return new MessageFormat(properties.get(input).toString()).format(params);
		//防止使读取无效 CALL..
		} catch (MissingResourceException e) {
			//Log.error("NO KEY- Please check the file",e);
		}
		return null;
	}
}