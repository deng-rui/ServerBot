package com.github.dr.serverbot.data.global;

import com.github.dr.serverbot.data.global.cache.Runnablex;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Maps {
	// 你不支持线性消息连续处理:P
	private static final Map<String, Runnablex> QQRunnable = new ConcurrentHashMap<String, Runnablex>();

	final public static void addQQRunnable(String str,Runnablex run) {
		QQRunnable.put(str,run);
	}

	final public static Runnablex getQQRunnable(String str) {
		return QQRunnable.get(str);
	}

	final public static void removeQQRunnable(String str) {
		QQRunnable.remove(str);
	}

	final public static boolean isQQRunnable(String str) {
		return QQRunnable.containsKey(str);
	}
}

