package com.github.dr.serverbot.dependent;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.List;
import java.util.Properties;

import com.github.dr.serverbot.util.file.FileUtil;
import com.github.dr.serverbot.util.log.Log;

import static com.github.dr.serverbot.net.HttpRequest.Url302;
import static com.github.dr.serverbot.net.HttpRequest.downUrl;

public class Librarydependency implements Driver {

	private static String url;
	private static Driver driver;

	Librarydependency(Driver d) {
		this.driver = d;
	}

	private static void downLoadFromUrl(String str, String name, String version, String country, String savePath) {
		String[] temp=str.split("\\.");
		url = "/";
		for (int i = 0; i < temp.length; i++) {
			url = url+temp[i]+"/";
		}
		Log.info(url);
		if("China".equalsIgnoreCase(country)) {
			url = "https://maven.aliyun.com/nexus/content/groups/public"+url+name+"/"+version+"/"+name+"-"+version+".jar";
			// 解决aliyun 302跳转
			Url302(url,savePath);
		}else{
			url = "https://repo1.maven.org/maven2"+url+name+"/"+version+"/"+name+"-"+version+".jar";
			downUrl(url,savePath);
		}
	}

	public static void importLib(String str, String name, String version, String savePath) {
		Log.info(savePath);
		File filepath=new File(FileUtil.File(savePath).getPath());
		if (!filepath.exists())filepath.mkdirs();
		List<File> FilePathList = FileUtil.File(savePath).getFileList();
		for(int i=0;i<FilePathList.size();i++){
			if((name+"_"+version).equals(FilePathList.get(i).getName().replace(".jar",""))) {
				notWork(name,version,savePath);
				return;
			}
		}
		downLoadFromUrl(str,name,version,"China",FileUtil.File(savePath).getPath(name+"_"+version+".jar"));
		notWork(name,version,savePath);
	}

	private static void notWork(String name, String version, String savePath) {
		try {
			URLClassLoader classLoader = new URLClassLoader(new URL[] {new File(FileUtil.File(savePath).getPath(name+"_"+version+".jar")).toURI().toURL()});
			Driver driver = (Driver) Class.forName("org.sqlite.JDBC", true, classLoader).getDeclaredConstructor().newInstance();
			// 加壳
			DriverManager.registerDriver(new Librarydependency(driver));
		} catch (Exception e){
		}
	}

	public Connection connect(String u, Properties p) throws SQLException {
		return this.driver.connect(u, p);
	}

	public boolean acceptsURL(String u) throws SQLException {
		return this.driver.acceptsURL(u);
	}

	public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
		return this.driver.getPropertyInfo(u, p);
	}

	public int getMajorVersion() {
		return this.driver.getMajorVersion();
	}

	public int getMinorVersion() {
		return this.driver.getMinorVersion();
	}

	public boolean jdbcCompliant() {
		return this.driver.jdbcCompliant();
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.driver.getParentLogger();
	}
}