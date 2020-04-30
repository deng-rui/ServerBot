package com.github.dr.serverbot.dependent;

import com.github.dr.serverbot.util.file.FileUtil;
import com.github.dr.serverbot.util.log.Log;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import static com.github.dr.serverbot.net.HttpRequest.Url302;
import static com.github.dr.serverbot.net.HttpRequest.downUrl;

public class Librarydependency implements Driver {

	private static String url;
	private static Driver driver;

	Librarydependency(Driver d) {
		driver = d;
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
		if (!filepath.exists()) {
            filepath.mkdirs();
        }
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

	@Override
	public Connection connect(String u, Properties p) throws SQLException {
		return driver.connect(u, p);
	}

	@Override
	public boolean acceptsURL(String u) throws SQLException {
		return driver.acceptsURL(u);
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
		return driver.getPropertyInfo(u, p);
	}

	@Override
	public int getMajorVersion() {
		return driver.getMajorVersion();
	}

	@Override
	public int getMinorVersion() {
		return driver.getMinorVersion();
	}

	@Override
	public boolean jdbcCompliant() {
		return driver.jdbcCompliant();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return driver.getParentLogger();
	}
}