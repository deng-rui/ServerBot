package com.github.dr.serverbot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.dr.serverbot.core.Commands;
import com.github.dr.serverbot.core.Events;
import com.github.dr.serverbot.data.global.Config;
import com.github.dr.serverbot.data.global.Data;
import com.github.dr.serverbot.util.file.FileUtil;
import com.github.dr.serverbot.util.log.Log;
import net.mamoe.mirai.console.plugins.PluginBase;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import static com.github.dr.serverbot.data.db.SQLite.InitializationSQLite;
import static com.github.dr.serverbot.data.json.Json.InitializationJson;
import static com.github.dr.serverbot.data.json.Json.getData;
import static com.github.dr.serverbot.dependent.Librarydependency.importLib;

/**
 * @author Dr
 */
public class Main extends PluginBase {
/*
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
*/

	@Override
	public void onLoad(){
		super.onLoad();
		/* 注册驱动 */
		Log.set("ALL");
		//importLib("org.xerial","sqlite-jdbc","3.30.1",Data.Plugin_Lib_Path);
        importLib("org.mariadb.jdbc", "mariadb-java-client", "2.6.0", Data.Plugin_Lib_Path);


		/* 初始化数据 */
		/* CalssLoad-Java8 :( Java8+则ERROR */
		if (FileUtil.File(Data.Plugin_Data_Path).toPath("Data.db").exists()) {
		} else {
			InitializationSQLite();
		}
		if (FileUtil.File(Data.Plugin_Data_Path).toPath("Data.json").exists()) {
		} else {
            InitializationJson();
        }
		/* 加载数据 */
		JSONObject date = getData();
		JSONArray array = (JSONArray) JSONArray.parse(date.get("qun").toString());
		for (int i = 0; i < array.size(); i++) {
			Data.QunData.add(Long.valueOf(array.getString(i)));
		}
		Config.Server_Url = date.get("Server_Url").toString();
		try {
            if (!FileUtil.File(Data.Plugin_Data_Path).toPath("/Config.ini").exists()) {
                String data = (String) FileUtil.readfile(false, new InputStreamReader(Main.class.getResourceAsStream("/Config.ini"), "UTF-8"));
                FileUtil.writefile(data, false);
                Log.info("Defect : Start creating write external Config File", Data.Plugin_Data_Path + "/Config.ini");
            }
        } catch (UnsupportedEncodingException e) {
            Log.fatal("File write error", e);
        }
        Config.laodConfig();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@Override
	public void onEnable(){
		new Commands(this);
		new Events(this);
		this.getLogger().info("Mindustry Server QQ-Bot onEnable");
	}
}
