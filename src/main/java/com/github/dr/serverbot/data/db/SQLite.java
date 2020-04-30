package com.github.dr.serverbot.data.db;

import java.util.*;
import java.sql.*;

import com.github.dr.serverbot.data.global.Data;
import com.github.dr.serverbot.util.file.FileUtil;

public class SQLite {

	public static void InitializationSQLite() {
		try {
			String sql;
			Connection c = DriverManager.getConnection("jdbc:sqlite:"+FileUtil.File(Data.Plugin_Data_Path).getPath("Data.db"));
			Statement stmt = c.createStatement();
			// 时间可以改成BUGINT
			sql = "CREATE TABLE UserBind (" +
				  "User 					TEXT,"+
				  "QQ 						BIGINT)";
			//Cryptographically Secure Pseudo-Random Number Generator
			//安全系列3
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch (Exception e ) {
		}
	}

}