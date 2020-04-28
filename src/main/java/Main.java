package com.github.dr.serverbot;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import net.mamoe.mirai.*;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.*;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.console.command.*;
import net.mamoe.mirai.console.plugins.*;
import net.mamoe.mirai.console.utils.*;
import net.mamoe.mirai.message.*;
import net.mamoe.mirai.message.data.*;

import com.github.dr.serverbot.core.ClientCommands;
import com.github.dr.serverbot.core.Events;
import com.github.dr.serverbot.data.global.Data;
import com.github.dr.serverbot.util.file.FileUtil;
import com.github.dr.serverbot.util.log.Log;

import static com.github.dr.serverbot.data.db.SQLite.InitializationSQLite;
import static com.github.dr.serverbot.data.json.Json.*;
import static com.github.dr.serverbot.dependent.Librarydependency.importLib;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;

import java.util.TimerTask;

/**
 * @author Dr
 */
public class Main extends PluginBase {

	public static final ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
	private static ScheduledFuture Thread_Time;

	// 可自行去掉 这个是适配Docker-Jdk(面板)的
	static {
        Runnable Atime=new Runnable() {
			@Override
			public void run() {
				copyFolder(new File("/tmp"),new File("/home/container"));
			}
		};
		Thread_Time=service.scheduleAtFixedRate(Atime,1,1,TimeUnit.MINUTES);	
	}
	
	private static void copyFolder(File src, File dest) {
		try {
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
			}
			String files[] = src.list();
			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// 递归复制
				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		}
		}catch(Exception ex){
		}
	}

	@Override
	public void onLoad(){
		super.onLoad();
		Log.Set("ALL");
		importLib("org.xerial","sqlite-jdbc","3.30.1",Data.Plugin_Lib_Path);
		if(!FileUtil.File(Data.Plugin_Data_Path).toPath("Data.db").exists())
			InitializationSQLite();
		if(!FileUtil.File(Data.Plugin_Data_Path).toPath("Data.json").exists())
			InitializationJson();
		JSONObject date = getData();
		JSONArray array = (JSONArray) JSONArray.parse(date.get("qun").toString());
		for (int i = 0; i < array.size(); i++) 
			Data.QunData.add(Long.valueOf(array.getString(i)));
	}

	@Override
	public void onDisable() {
		super.onDisable();
		new Events(this,null);
	}

	public void onEnable(){
		new ClientCommands(this);
		new Events(this);
		this.getLogger().info("PingMyMCcom.github.dr.server Enabled");
	}
}
