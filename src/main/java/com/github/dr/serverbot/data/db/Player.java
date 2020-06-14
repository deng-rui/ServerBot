package com.github.dr.serverbot.data.db;

import com.github.dr.serverbot.util.log.Log;
import com.github.dr.serverbot.util.pool.db.Jdbc;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dr
 */
public class Player {

	public static void initPlayersSqlite(String user, long qq) {
		PreparedStatement bind = null;
        Connection c = null;
		try {	
			c = Jdbc.getConnection();
			bind = c.prepareStatement("INSERT INTO UserBind VALUES (?,?)");
			bind.setString(1,user);
			bind.setLong(2,qq);
			bind.execute();
			c.commit();
		} catch (Exception e) {
		} finally {
			close(bind);
			close(c);
		}
	}

	public static void savePlayer(String user, long qq) {
		PreparedStatement bind = null;
		Connection c = null;
		try {
			c = Jdbc.getConnection();
			bind = c.prepareStatement("UPDATE UserBind SET User=?,QQ=? WHERE QQ=?");
			bind.setString(1,user);
			bind.setLong(2,qq);
			bind.setLong(3,qq);
			bind.execute();
			c.commit();
		} catch (SQLException e) {
		} finally {
			close(bind);
			close(c);
		}
	}

	public static Map<String,Object> getSqlite(long qq) {
		PreparedStatement bind = null;
		Map<String,Object> result = new HashMap<String,Object>();
		ResultSet rs = null;
        Connection c = null;
		try {
			c = Jdbc.getConnection();
			bind = c.prepareStatement("SELECT * FROM UserBind WHERE QQ=?");
			bind.setLong(1,qq);
			rs = bind.executeQuery();
			while (rs.next()) {
				result.put("USER",rs.getString("User"));
				result.put("QQ",rs.getLong("QQ"));
			}
		} catch (SQLException e) {
		} finally {
			close(rs,bind);
			close(c);
		}
		return result;
	}

	public static boolean isSqliteUser(long qq) {
		boolean result = true;
		PreparedStatement bind = null;
		ResultSet rs = null;
        Connection c = null;
		try {
			c = Jdbc.getConnection();
			bind = c.prepareStatement("SELECT COUNT(QQ) FROM UserBind where QQ=?");
			// 真的奇怪?
			bind.setLong(1,qq);
			rs = bind.executeQuery();
			rs.next();
			if(rs.getInt(1)>0) {
                result = false;
            }
			//用户名存在 避免冲突
		} catch (SQLException e) {
		} finally {
			close(rs,bind);
			close(c);
		}
		return result;
	}

	public static void getSqlite(PlayerData data, String user) {
        PreparedStatement playerdata = null;
        PreparedStatement playerpriv = null;
        ResultSet rs = null;
        ResultSet rss = null;
        Connection c = null;
        try {
            c = Jdbc.getConnection();
            playerdata = c.prepareStatement("SELECT * FROM PlayerData WHERE User=? LOCK IN SHARE MODE");
            playerpriv = c.prepareStatement("SELECT * FROM PlayerPrivate WHERE User=? LOCK IN SHARE MODE");
            playerdata.setString(1,user);
            playerpriv.setString(1,user);   
            rs = playerdata.executeQuery();
            rss = playerpriv.executeQuery();
            while (rs.next()) {
                // 防止游戏玩一半登录 导致数据飞天
                data.user                       = rs.getString("User");
                data.uuid                       = rs.getString("UUID");
                data.ip                         = rs.getLong("IP");
                data.gmt                        = rs.getInt("GMT");
                data.country                    = rs.getString("Country");
                data.timeFormat                 = rs.getByte("Time_format");
                data.language                   = rs.getString("Language");
                data.lastLogin                  = rs.getLong("LastLogin");
                data.buildCount                 +=rs.getInt("Buildcount");
                data.dismantledCount            +=rs.getInt("Dismantledcount");
                data.cumulativeBuild            +=rs.getInt("Cumulative_build");
                data.pipeBuild                  +=rs.getInt("Pipe_build");
                data.kickCount                  +=rs.getInt("Kickcount");
                data.level                      = rs.getInt("Level");
                data.exp                        = rs.getShort("Exp");
                data.reqexp                     = rs.getLong("Reqexp");
                data.reqtotalExp                = rs.getLong("Reqtotalexp");
                data.playTime                   +=rs.getLong("Playtime");
                data.pvpwinCount                +=rs.getInt("Pvpwincount");
                data.pvploseCount               +=rs.getInt("Pvplosecount");
                data.authority                  = rs.getInt("Authority");
                data.authorityEffectiveTime     = rs.getLong("Authority_effective_time");
                data.lastChat                   = rs.getLong("Lastchat");
                data.deadCount                  += rs.getInt("Deadcount");
                data.killCount                  += rs.getInt("Killcount");
                data.joinCount                  += rs.getInt("Joincount");
                data.breakCount                 += rs.getInt("Breakcount");
            }
            while (rss.next()) {
                data.mail                       = rss.getString("Mail");
                data.passwordHash               = rss.getString("PasswordHash");
                data.csprng                     = rss.getString("CSPRNG");
            }
            c.commit();
        } catch (SQLException e) {
            Log.error(e);
        } finally {
            close(rs,playerdata);
            close(rss,playerpriv);
            close(c);
        }
    }

    public static boolean isSqliteUser(String user) {
        boolean result = true;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection c = null;
        try {
            c = Jdbc.getConnection();
            stmt = c.prepareStatement("SELECT COUNT(User) FROM PlayerPrivate where User=? LOCK IN SHARE MODE");
            // 真的奇怪?
            stmt.setString(1,user);
            rs = stmt.executeQuery();
            rs.next();
            if(rs.getInt(1)>0) {
                result = false;
            }
            //用户名存在 避免冲突
            c.commit();
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            close(rs,stmt);
            close(c);
        }
        return result;
    }

	private static void close(Connection conn) {
		close(null,null,conn);
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
			if (rs != null) {
                rs.close();
            }
		} catch (Exception e) {  
			rs = null;  
		} finally {
			try {
				if (stmt != null) {
                    stmt.close();
                }
			} catch (Exception e) {  
				stmt = null;  
			} finally {
				try {
					if (conn != null) {
                        conn.close();
                    }
				} catch (Exception e) {  
					conn = null;  
				}
			}
		}
	}
}