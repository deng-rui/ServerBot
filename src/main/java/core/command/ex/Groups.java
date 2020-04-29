package com.github.dr.serverbot.core.command.ex;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import arc.util.serialization.*;
import mindustry.net.*;

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

import com.github.dr.serverbot.Main;
import com.github.dr.serverbot.data.db.Player;
import com.github.dr.serverbot.data.global.Config;
import com.github.dr.serverbot.data.global.Data;
import com.github.dr.serverbot.data.global.cache.Runnablex;
import com.github.dr.serverbot.data.db.PlayerData;
import com.github.dr.serverbot.util.log.Log;
import com.github.dr.serverbot.util.translation.Google;
import com.github.dr.serverbot.util.translation.Baidu;
import com.github.dr.serverbot.util.translation.Bing;

import static com.github.dr.serverbot.net.HttpRequest.doGet;
import static com.github.dr.serverbot.util.file.LoadConfig.CustomLoad;
import static com.github.dr.serverbot.util.ExtractUtil.*;
import static com.github.dr.serverbot.util.IsUtil.*;
import static com.github.dr.serverbot.util.DateUtil.simp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;



public enum Groups {

	BOT {
		@Override
		public void run(GroupMessage event) {
			event.getGroup().sendMessage(MessageUtils.quote(event.getMessage()).plus(CustomLoad("bot.version")));
		}
	},

	INFO {
		@Override
		public void run(GroupMessage event) {
			long QQ = event.getSender().getId();
			if(!Player.isSQLite_User(QQ)) {
				this.main.getScheduler().async(() -> {
					StringBuffer response = new StringBuffer();
					response.append(Config.Server_Url)
						.append(Data.Get)
						.append("info")
						.append("?user="+Player.getSQLite(QQ).get("USER"));
					String result = doGet(response.toString());
					if(result == null) 
						result = doGet(response.toString());
					if(result == null) 
						result = doGet(response.toString());
					if(result == null) {
						event.getGroup().sendMessage(CustomLoad("net.err"));
						return;
					}
					JSONObject playerdata = JSONObject.parseObject(new String(Base64.getDecoder().decode(JSONObject.parseObject(result).get("result").toString())));
					PlayerData data = JSON.toJavaObject(playerdata,PlayerData.class);
					Object[] params = {data.NAME,data.UUID,longToIP(data.IP),data.Country,data.Language,data.Level,data.Exp,data.Reqexp,data.Reqtotalexp,data.Buildcount,data.Cumulative_build,data.Pipe_build,data.Dismantledcount,data.Pvpwincount,data.Pvplosecount,data.Authority,simp(data.Authority_effective_time*1000L,data.Time_format),secToTime(data.Playtime),simp(data.LastLogin*1000L,data.Time_format),simp(data.Lastchat*1000L,data.Time_format),data.Killcount,data.Deadcount,data.Joincount,data.Breakcount,data.Online};
					event.getGroup().sendMessage(CustomLoad("player.info",params));
				});
			}
		}
	},

	STATUS {
		@Override
		public void run(GroupMessage event) {
			this.main.getScheduler().async(() -> {
				StringBuffer response = new StringBuffer();
				response.append(Config.Server_Url)
				.append(Data.Get)
				.append("status");
				//.append("&user="+Player.getSQLite(QQ).get("USER"));
				Log.info(response.toString());
				String result = doGet(response.toString());
				if(result == null) 	
					result = doGet(response.toString());
				if(result == null) 
					result = doGet(response.toString());
				if(result == null) {
					event.getGroup().sendMessage(CustomLoad("net.err"));
					return;
				}
				if (Integer.parseInt(JSONObject.parseObject(result).get("state").toString()) == 20003) {
					event.getGroup().sendMessage(CustomLoad("status.close"));
					return;
				}
				JSONObject data = JSONObject.parseObject(new String(Base64.getDecoder().decode(JSONObject.parseObject(result).get("result").toString())));
				event.getGroup().sendMessage(CustomLoad("status.info",new Object[] {data.get("player"),data.get("map"),data.get("fps"),data.get("mob")}));
			});
		}
	},

	PING {
		@Override
		public void run(GroupMessage event) {
			this.main.getScheduler().async(() -> {
				String [] arr = toSg(event.getMessage()).split("\\s+");
				if(arr.length < 2)
					return;
				Consumer<Host> listener = result -> {
					if(result.name != null) {
						String mode = "Survival";
						if(result.mode.toString().contains("pvp"))
							mode = "PVP";
						if(result.mode.toString().contains("attack"))
							mode = "Attack";
						event.getGroup().sendMessage(CustomLoad("ping.yes",new Object[] {result.name,result.players,result.playerLimit,result.mapname,mode,result.wave,result.version,result.ping}));
					} else 
						event.getGroup().sendMessage(CustomLoad("ping.err"));
				};
				try{
					String resultIP = arr[1];
					int port = 6567;
					if(arr[1].contains(":") && isNumeric(arr[1].split(":")[1])){
						resultIP = arr[1].split(":")[0];
						port = Strings.parseInt(arr[1].split(":")[1]);
					}
					DatagramSocket socket = new DatagramSocket();
					socket.send(new DatagramPacket(new byte[]{-2, 1}, 2, InetAddress.getByName(resultIP), port));
					socket.setSoTimeout(2000);
					DatagramPacket packet = new DatagramPacket(new byte[256], 256);
					long start = System.currentTimeMillis();
					socket.receive(packet);
					ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
					Host host = NetworkIO.readServerData(arr[1], buffer);
					host.ping = (int)(System.currentTimeMillis() - start);
					listener.accept(host);
					socket.disconnect();
				}catch(Exception e){
					listener.accept(new Host(null, arr[1], null, 0, 0, 0, null, null, 0, null));
				}
			});
		}
	},

	PINGS {
		@Override
		public void run(GroupMessage event) {
			if(!spings) {
				event.getGroup().sendMessage(CustomLoad("sleep.pings"));
				return;
			}
			this.main.getScheduler().async(() -> {
				String [] arr = toSg(event.getMessage()).split("\\s+");
				if(arr.length < 2)
					return;
				StringBuffer response = new StringBuffer();
				Consumer<Host> listener = result -> {
					if(result.name != null) 
						response.append(result.address.split(":")[1]+",");
				};
				String resultIP = arr[1];
				int sport = 6567;
				int eport = 6567;
				try {
					if(arr[1].contains(":") && isNumeric(arr[1].split(":")[1].split("-")[0]) && isNumeric(arr[1].split(":")[1].split("-")[1])) {
						resultIP = arr[1].split(":")[0];
						sport = Strings.parseInt(arr[1].split(":")[1].split("-")[0]);
						eport = Strings.parseInt(arr[1].split(":")[1].split("-")[1]);
					}
				}catch(Exception e){
					return;
				}	
				Log.info(sport);
				Log.info(eport);
				if(sport < 1 || eport < 1 || sport > 65535 || eport > 65535 || eport < sport)
					return;
				if((eport - sport) > 200) {
					event.getGroup().sendMessage(CustomLoad("range.max"));
					return;
				}
				spings = false;
				eport++;
				for(int i=sport;i<eport;i++) {
					final int ii = i;
					final String ip = resultIP;
					Data.Thred_service.execute(() -> {
						try{
							DatagramSocket socket = new DatagramSocket();
							socket.send(new DatagramPacket(new byte[]{-2, 1}, 2, InetAddress.getByName(ip), ii));
							socket.setSoTimeout(2000);
							DatagramPacket packet = new DatagramPacket(new byte[256], 256);
							long start = System.currentTimeMillis();
							socket.receive(packet);
							ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
							Host host = NetworkIO.readServerData(ip+":"+ii, buffer);
							host.ping = (int)(System.currentTimeMillis() - start);
							listener.accept(host);
							socket.disconnect();
						}catch(Exception e){
							listener.accept(new Host(null, arr[1], null, 0, 0, 0, null, null, 0, null));
						}
					});
				}
				try{
					event.getGroup().sendMessage(CustomLoad("sleep.10"));
					Thread.sleep(10000);
				}catch(Exception e){
				}
				event.getGroup().sendMessage((response.length() > 0)?response.toString():CustomLoad("no.data"));
				spings = true;
			});
		}
	},

	/*
	 * 个人发疯作品 自行取舍 AGUN3!
	 */
	BTR {
		@Override
		public void run(GroupMessage event) {
			this.main.getScheduler().async(() -> {
				String [] arr = toSg(event.getMessage()).split("\\s+");
				if(arr.length < 3)
					return;
				StringBuffer response = new StringBuffer();
				for(int i=2,len=arr.length;i<len;i++) 
					response.append(arr[i]);
				event.getGroup().sendMessage(MessageUtils.quote(event.getMessage()).plus(new Baidu().translate(response.toString(),arr[1])));
			});
		}
	},

	GTR {
		@Override
		public void run(GroupMessage event) {
			this.main.getScheduler().async(() -> {
				String [] arr = toSg(event.getMessage()).split("\\s+");
				if(arr.length < 3)
					return;
				StringBuffer response = new StringBuffer();
				for(int i=2,len=arr.length;i<len;i++) 
					response.append(arr[i]);
				event.getGroup().sendMessage(MessageUtils.quote(event.getMessage()).plus(new Google().translate(response.toString(),arr[1])));
			});
		}
	},

	WTR {
		@Override
		public void run(GroupMessage event) {
			this.main.getScheduler().async(() -> {
				String [] arr = toSg(event.getMessage()).split("\\s+");
				if(arr.length < 3)
					return;
				StringBuffer response = new StringBuffer();
				for(int i=2,len=arr.length;i<len;i++) 
					response.append(arr[i]);
				event.getGroup().sendMessage(MessageUtils.quote(event.getMessage()).plus(new Bing().translate(response.toString(),arr[1])));
			});
		}
	},

	CSF {
		@Override
		public void run(GroupMessage event) {
				aaa = (() -> event.getGroup().sendMessage(toSg(event.getMessage())));
		}
	},

	AAA {
		@Override
		public void run(GroupMessage event) {
			/*
		File file = new File("/mnt/l/elite_color.png");
			if (file.exists()) {
				final Image image = event.getGroup().uploadImage(file);
				// 上传一个图片并得到 Image 类型的 Message
				final String imageId = image.getImageId(); // 可以拿到 ID
				 // ID 转换得到 Image

				event.getGroup().sendMessage(image); // 发送图片
				event.getGroup().sendMessage(imageId);
				//event.getGroup().sendMessage(Bot.queryImageUrl(fromId));
			event.getGroup().sendMessage(event.getBot().queryImageUrl(fromId));
			}
			*/
			event.getGroup().sendMessage("START");
			event.getGroup().sendMessage(toSg(event.getMessage()));
		}
	};

	public static Main main;
	private static boolean spings = true;
	private static List<String> list = new CopyOnWriteArrayList<String>();

	static {
		Groups[] season = values();        
		for (Groups s : season) 
			 list.add("."+s.name());
	}

	public static boolean contains(String name) {
		return list.contains(name);
	}

	public abstract void run(GroupMessage event);

	private static String toSg(MessageChain chain) {
		return chain.contentToString();
	}
}