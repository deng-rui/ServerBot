package com.github.dr.serverbot.core;

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
import com.github.dr.serverbot.data.global.Data;
import com.github.dr.serverbot.data.db.PlayerData;
import com.github.dr.serverbot.util.log.Log;
import com.github.dr.serverbot.util.translation.Google;
import com.github.dr.serverbot.util.translation.Baidu;

import static com.github.dr.serverbot.net.HttpRequest.doGet;
import static com.github.dr.serverbot.util.file.LoadConfig.CustomLoad;
import static com.github.dr.serverbot.util.ExtractUtil.*;
import static com.github.dr.serverbot.util.IsUtil.*;
import static com.github.dr.serverbot.util.DateUtil.simp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Events {

	private Main main;
	private String API;
	private Config setting;

	public Events(Main main) {
		this.main = main;
		this.setting = this.main.loadConfig("Setting.yml");
		this.setting.setIfAbsent("API","http://");
		this.API = this.setting.getString("API");
		this.setting.save();
		registers();
	}

	public Events(Main main,String a) {
		this.setting.set("API", API);
		this.setting.save();
	}

	public void registers() {
		this.main.getEventListener().subscribeAlways(FriendMessage.class, (event) -> {
			if(toString(event.getMessage()).contains(".help")) {
				final Future<MessageReceipt<Contact>> future = event.getSender().sendMessageAsync(CustomLoad("help.user"));
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			} else if (toString(event.getMessage()).contains(".bind")) {
				long QQ = event.getSender().getId();
				if(Player.isSQLite_User(QQ)) {
					this.main.getScheduler().async(() -> {
						String [] arr = toString(event.getMessage()).split("\\s+");
						if(arr.length < 3) {
							event.getSender().sendMessage(CustomLoad("type.err",new Object[]{".bind 用户名 密码"}));
							return;
						}
						StringBuffer response = new StringBuffer();
						response.append(API)
						.append(Data.Get)
						.append("type=bind")
						.append("&user="+arr[1])
						.append("&passwd="+arr[2]);
						Log.info(response.toString());
						String result = doGet(response.toString());
						if(result == null) {
							event.getSender().sendMessage(CustomLoad("net.err"));
							return;
						}
						switch(result){
							case "0" :
								//正常
								Player.InitializationPlayersSQLite(arr[1],QQ);
								event.getSender().sendMessage(CustomLoad("bind.yes",new Object[]{arr[0]}));
								break;
							case "1" :
								event.getSender().sendMessage(CustomLoad("bind.epas"));
								//密码错
								break;
							case "2" :
								event.getSender().sendMessage(CustomLoad("bind.nusr"));
								//用户不在
								break;
							default :
								event.getSender().sendMessage(CustomLoad("bind.err"));
								//我不知道
								break;
						}
					});
				} else 
					event.getSender().sendMessage(CustomLoad("bind.aly"));
			}
		});

		this.main.getEventListener().subscribeAlways(TempMessage.class, (event) -> {
			if(toString(event.getMessage()).contains(".help")) {
				final Future<MessageReceipt<Contact>> future = event.getSender().sendMessageAsync(CustomLoad("help.user"));
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			} else if (toString(event.getMessage()).contains(".bind")) {
				long QQ = event.getSender().getId();
				if(Player.isSQLite_User(QQ)) {
					this.main.getScheduler().async(() -> {
						String [] arr = toString(event.getMessage()).split("\\s+");
						if(arr.length < 3) {
							event.getSender().sendMessage(CustomLoad("type.err",new Object[]{".bind 用户名 密码"}));
							return;
						}
						StringBuffer response = new StringBuffer();
						response.append(API)
						.append(Data.Get)
						.append("type=bind")
						.append("&user="+arr[1])
						.append("&passwd="+arr[2]);
						Log.info(response.toString());
						String result = doGet(response.toString());
						if(result == null) {
							event.getSender().sendMessage(CustomLoad("net.err"));
							return;
						}
						switch(result){
							case "0" :
								//正常
								Player.InitializationPlayersSQLite(arr[1],QQ);
								event.getSender().sendMessage(CustomLoad("bind.yes",new Object[]{arr[0]}));
								break;
							case "1" :
								event.getSender().sendMessage(CustomLoad("bind.epas"));
								//密码错
								break;
							case "2" :
								event.getSender().sendMessage(CustomLoad("bind.nusr"));
								//用户不在
								break;
							default :
								event.getSender().sendMessage(CustomLoad("bind.err"));
								//我不知道
								break;
						}
					});
				} else 
					event.getSender().sendMessage(CustomLoad("bind.aly"));
			}
		});

		this.main.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event) -> {
			if(!Data.QunData.contains(event.getGroup().getId()))
				return;
			if (toString(event.getMessage()).contains(".bot")) {
				event.getGroup().sendMessage(MessageUtils.quote(event.getMessage()).plus(CustomLoad("bot.version")));
			} else if (toString(event.getMessage()).contains(".info")) {
				long QQ = event.getSender().getId();
				if(!Player.isSQLite_User(QQ)) {
					this.main.getScheduler().async(() -> {
						StringBuffer response = new StringBuffer();
						response.append(API)
						.append(Data.Get)
						.append("type=info")
						.append("&user="+Player.getSQLite(QQ).get("USER"));
						Log.info(response.toString());
						String result = doGet(response.toString());
						if(result == null) {
							event.getGroup().sendMessage(CustomLoad("net.err"));
							return;
						}
						JSONObject playerdata = JSONObject.parseObject(new String(Base64.getDecoder().decode(result)));
						PlayerData data = JSON.toJavaObject(playerdata,PlayerData.class);
						Object[] params = {data.NAME,data.UUID,longToIP(data.IP),data.Country,data.Language,data.Level,data.Exp,data.Reqexp,data.Reqtotalexp,data.Buildcount,data.Cumulative_build,data.Pipe_build,data.Dismantledcount,data.Pvpwincount,data.Pvplosecount,data.Authority,simp(data.Authority_effective_time*1000L,data.Time_format),secToTime(data.Playtime),simp(data.LastLogin*1000L,data.Time_format),simp(data.Lastchat*1000L,data.Time_format),data.Killcount,data.Deadcount,data.Joincount,data.Breakcount,data.Online};
						event.getGroup().sendMessage(CustomLoad("player.info",params));
					});
				}
			} else if (toString(event.getMessage()).contains(".btr")) {
				this.main.getScheduler().async(() -> {
					String [] arr = toString(event.getMessage()).split("\\s+");
					if(arr.length < 3)
						return;
					StringBuffer response = new StringBuffer();
					for(int i=2,len=arr.length;i<len;i++) 
						response.append(arr[i]);
					final QuoteReply quote = MessageUtils.quote(event.getMessage());
					event.getGroup().sendMessage(quote.plus(new Baidu().translate(response.toString(),arr[1])));
				});

			} else if (toString(event.getMessage()).contains(".gtr")) {
				this.main.getScheduler().async(() -> {
					String [] arr = toString(event.getMessage()).split("\\s+");
					if(arr.length < 3)
						return;
					StringBuffer response = new StringBuffer();
					for(int i=2,len=arr.length;i<len;i++) 
						response.append(arr[i]);
					final QuoteReply quote = MessageUtils.quote(event.getMessage());
					event.getGroup().sendMessage(quote.plus(new Google().translate(response.toString(),arr[1])));
				});

			} else if (toString(event.getMessage()).contains(".status")) {
				this.main.getScheduler().async(() -> {
					StringBuffer response = new StringBuffer();
					response.append(API)
					.append(Data.Get)
					.append("type=status");
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
					JSONObject data = JSONObject.parseObject(new String(Base64.getDecoder().decode(result)));
					event.getGroup().sendMessage(CustomLoad("status.info",new Object[] {data.get("player"),data.get("map"),data.get("fps"),data.get("mob")}));
				});
			} else if (toString(event.getMessage()).contains(".ping")) {
				this.main.getScheduler().async(() -> {
					String [] arr = toString(event.getMessage()).split("\\s+");
					if(arr.length < 2)
						return;
					Consumer<Host> listener = result -> {
		                if(result.name != null) 
		                	event.getGroup().sendMessage(CustomLoad("ping.yes",new Object[] {result.name, result.players, result.mapname, result.wave, result.version, result.ping}));
		                 else 
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
		});
	}

	public static String toString(MessageChain chain) {
		return chain.contentToString();
	}
}