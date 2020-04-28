package com.github.dr.serverbot.data.db;

import java.util.*;
import java.sql.*;

import com.github.dr.serverbot.data.global.Data;
import com.github.dr.serverbot.util.file.FileUtil;

public class Player {

	private static Connection c;

	static {
		try {
			c = DriverManager.getConnection("jdbc:sqlite:"+FileUtil.File(Data.Plugin_Data_Path).getPath("Data.db"));
			c.setAutoCommit(false);
		} catch (Exception e) {
		}
	}

	public static void InitializationPlayersSQLite(String User, long QQ) {
		PreparedStatement bind = null;
		try {	
			bind = c.prepareStatement("INSERT INTO UserBind VALUES (?,?)");
			bind.setString(1,User);
			bind.setLong(2,QQ);
			bind.execute();
			c.commit();
		} catch (Exception e) {
		} finally {
			close(bind);
		}
	}

	public static void savePlayer(String User, long QQ) {
		PreparedStatement bind = null;
		try {
			bind = c.prepareStatement("UPDATE UserBind SET User=?,QQ=? WHERE QQ=?");
			bind.setString(1,User);
			bind.setLong(2,QQ);
			bind.setLong(3,QQ);
			bind.execute();
			c.commit();
		} catch (SQLException e) {
		} finally {
			close(bind);
		}
	}

	public static Map<String,Object> getSQLite(long QQ) {
		PreparedStatement bind = null;
		Map<String,Object> result = new HashMap<String,Object>();
		ResultSet rs = null;
		try {
			bind = c.prepareStatement("SELECT * FROM UserBind WHERE QQ=?");
			bind.setLong(1,QQ);
			rs = bind.executeQuery();
			while (rs.next()) {
				result.put("USER",rs.getString("User"));
				result.put("QQ",rs.getLong("QQ"));
			}
		} catch (SQLException e) {
		} finally {
			close(rs,bind);
		}
		return result;
	}

	public static boolean isSQLite_User(long QQ) {
		boolean result = true;
		PreparedStatement bind = null;
		ResultSet rs = null;
		try {
			bind = c.prepareStatement("SELECT COUNT(QQ) FROM UserBind where QQ=?");
			// 真的奇怪?
			bind.setLong(1,QQ);
			rs = bind.executeQuery();
			rs.next();
			if(rs.getInt(1)>0)result = false;
			//用户名存在 避免冲突
		} catch (SQLException e) {
		} finally {
			close(rs,bind);
		}
		return result;
	}

	private static void close(Statement stmt) {
		close(null,stmt,null);
	}

	private static void close(ResultSet rs,Statement stmt) {
		close(rs,stmt,null);
	}

	private static void close(Statement stmt,Connection conn) {
		close(null,stmt,conn);
	}

	private static void close(ResultSet rs,Statement stmt,Connection conn) {
		try {
			if (rs != null) rs.close();
		} catch (Exception e) {  
			rs = null;  
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (Exception e) {  
				stmt = null;  
			} finally {
				try {
					if (conn != null) conn.close();
				} catch (Exception e) {  
					conn = null;  
				}
			}
		}
	}
}