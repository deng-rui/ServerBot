package com.github.dr.serverbot.core.command.ex;

import com.github.dr.serverbot.Main;
import com.github.dr.serverbot.data.db.PlayerData;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.github.dr.serverbot.data.db.Player.*;
import static com.github.dr.serverbot.util.DateUtil.simp;
import static com.github.dr.serverbot.util.ExtractUtil.longToIp;
import static com.github.dr.serverbot.util.ExtractUtil.secToTime;
import static com.github.dr.serverbot.util.alone.Password.isPasswdVerify;
import static com.github.dr.serverbot.util.file.LoadConfig.customLoad;

public enum Friend {

	/**
	 * NO
	 */
	HELP {
		@Override
		public void run(FriendMessageEvent event) {
			event.getSender().sendMessageAsync(customLoad("help.user"));
		}
	},

	BIND {
		@Override
		public void run(FriendMessageEvent event) {
			if (!isSqliteUser(event.getSender().getId())) {
				event.getSender().sendMessageAsync(customLoad("bind.re"));
				return;
			}
			String [] arr = toString(event.getMessage()).split("\\s+");
			final int len = 3;
			if (arr.length >= len) {
				PlayerData data = new PlayerData(arr[2]);
				if (isSqliteUser(arr[1])) {
					event.getSender().sendMessageAsync(customLoad("no.user"));
				} else {
					try {
						if (isPasswdVerify(arr[2],data.passwordHash,data.csprng)) {
							initPlayersSqlite(arr[1],event.getSender().getId());
							event.getSender().sendMessageAsync(customLoad("bind.yesr"));
						} else {
							event.getSender().sendMessageAsync(customLoad("bind.passwd.err"));
						}
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} catch (InvalidKeySpecException e) {
						e.printStackTrace();
					}
				}
			}
		}
	},

	INFO {
		@Override
		public void run(FriendMessageEvent event) {
			long QQ = event.getSender().getId();
			if(!isSqliteUser(QQ)) {
				main.getScheduler().async(() -> {
					PlayerData data = new PlayerData("00");
					getSqlite(data,getSqlite(QQ).get("USER").toString());
					Object[] params = {data.name,data.uuid,longToIp(data.ip),data.country,data.language,data.level,data.exp,data.reqexp,data.reqtotalExp,data.buildCount,data.cumulativeBuild,data.pipeBuild,data.dismantledCount,data.pvpwinCount,data.pvploseCount,data.authority,simp(data.authorityEffectiveTime*1000L,data.timeFormat),secToTime(data.playTime),simp(data.lastLogin*1000L,data.timeFormat),simp(data.lastChat*1000L,data.timeFormat),data.killCount,data.deadCount,data.joinCount,data.breakCount,data.online};
					event.getSender().sendMessageAsync(customLoad("player.info",params));
				});
			}
		}
	};

	public static Main main;
	private static boolean spings = true;
	private static List<String> list = new CopyOnWriteArrayList<String>();

	static {
		Friend[] season = values();        
		for (Friend s : season) {
            list.add(s.name());
        }
	}

	public static boolean contains(String name) {
		return list.contains(name);
	}

	public abstract void run(FriendMessageEvent event);

	static String toString(MessageChain chain) {
		return chain.contentToString();
	}
}