package com.github.dr.serverbot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.dr.serverbot.net.HttpRequest.doGet;

import static com.github.dr.serverbot.util.IsUtil.notisBlank;
import static com.github.dr.serverbot.util.StringFilteringUtil.removeAllisBlank;
import static com.github.dr.serverbot.util.StringFilteringUtil.trim;

public class ExtractUtil {

	final static Pattern PATTERN = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");

	public static String extractByStartAndEnd(String str, String startStr, String endStr) {
		String regEx = startStr + ".*?"+endStr;
		String group = findMatchString(str, regEx);
		String trim = group.replace(startStr, "").replace(endStr, "").trim();
		return trim(trim);
	}

	public static String findMatchString(String str, String regEx) {
		try {
			Pattern pattern = Pattern.compile(regEx);
			Matcher matcher = pattern.matcher(str);
			return findFristGroup(matcher);
		} catch (Exception e) {
			e.printStackTrace();
		return null;
		}
	}

	private static String findFristGroup(Matcher matcher) {
		matcher.find();
		return matcher.group(0);
	}

	public static String getkeys(String url,String keys,int numbero,int numbert) {
		String tkk = "";
		String result = doGet(url);
		// 去除返回数据空格
		String text = removeAllisBlank(result);
		if (notisBlank(result)) {
			String matchString = findMatchString(text, keys);
			// 提取目标
			tkk = matchString.substring(numbero, matchString.length() - numbert);
		}
		return tkk;
	}

	public static long ipToLong(String strIp) {
		String[]ip = strIp.split("\\.");
		return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);
	}


    public static String longToIp(long longIp) {
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf((longIp >>> 24)))
		.append(".")
		.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16))
		.append(".")
		.append(String.valueOf((longIp & 0x0000FFFF) >>> 8))
		.append(".")
		.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}

	public static String unicodeEncode(String string) {
        char[] utfBytes = string.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    public static String unicodeDecode(String string) {
        Matcher matcher = PATTERN.matcher(string);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            string = string.replace(matcher.group(1), ch + "");
        }
        return string;
    }

	public static String secToTime(long time) {
		String timeStr = null;
		long hour = 0;
		long minute = 0;
		long second = 0;
		if(time <= 0) {
			return "00:00";
		} else {
			minute = time / 60;
			hour = minute / 60;
			minute = minute % 60;
			second = time - hour * 3600 - minute * 60;
			timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
		}
		return timeStr;
	}

	private static String unitFormat(long i) {
		String retStr = null;
		if(i >= 0 && i < 10) {
			retStr = "0" + i;
		} else {
			retStr = "" + i;
		}
		return retStr;
	}

}