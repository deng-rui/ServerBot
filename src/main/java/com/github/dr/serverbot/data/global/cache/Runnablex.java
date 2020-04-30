package com.github.dr.serverbot.data.global.cache;

import static com.github.dr.serverbot.util.DateUtil.getLocalTimeFromU;

public class Runnablex {
	public String id;
	public long endtime;
	public Object data;
	public Runnable run;

	public Runnablex(String id) {
		this.id = id;
		// 延长20S
		this.endtime = getLocalTimeFromU()+20;
	}
}
//\{.{8}-(.{4}-){3}.{12}\}\.mirai