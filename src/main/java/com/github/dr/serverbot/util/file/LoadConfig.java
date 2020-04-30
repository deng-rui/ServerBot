package com.github.dr.serverbot.util.file;

import java.io.*;
import java.util.*;
import java.text.MessageFormat;
import com.github.dr.serverbot.data.global.Data;
import com.github.dr.serverbot.util.log.Log;

/*
 *  Config.java
 *	Initialization.java
 */
public class LoadConfig {

	private static Object load(String input) {
		Properties properties = new Properties();
        InputStreamReader inputStream = FileUtil.File(Data.Plugin_Data_Path).toPath("/Config.ini").readconfig();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
		Object result = properties.get(input);
		//防止使读取无效 CALL..
		
		if (result != null) {
            return result;
        }
		Log.warn("NO KEY- Please check the file",input);
		return null;
	}

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

	public static String loadstring(String input) {
		Object str = load(input);
		return str.toString();
	}
}