package com.github.dr.serverbot.core.command.ex;

import arc.util.Strings;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.dr.serverbot.Main;
import com.github.dr.serverbot.data.db.PlayerData;
import com.github.dr.serverbot.data.global.Config;
import com.github.dr.serverbot.data.global.Data;
import com.github.dr.serverbot.data.global.Maps;
import com.github.dr.serverbot.data.global.cache.Runnablex;
import com.github.dr.serverbot.util.alone.translation.Baidu;
import com.github.dr.serverbot.util.alone.translation.Bing;
import com.github.dr.serverbot.util.alone.translation.Google;
import com.github.dr.serverbot.util.log.Log;
import mindustry.net.Host;
import mindustry.net.NetworkIO;
import net.mamoe.mirai.message.GroupMessage;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static com.github.dr.serverbot.data.db.Player.getSqlite;
import static com.github.dr.serverbot.data.db.Player.isSqliteUser;
import static com.github.dr.serverbot.net.HttpRequest.doGet;
import static com.github.dr.serverbot.net.HttpRequest.doPost;
import static com.github.dr.serverbot.util.DateUtil.getLocalTimeFromU;
import static com.github.dr.serverbot.util.DateUtil.simp;
import static com.github.dr.serverbot.util.ExtractUtil.longToIp;
import static com.github.dr.serverbot.util.ExtractUtil.secToTime;
import static com.github.dr.serverbot.util.IsUtil.*;
import static com.github.dr.serverbot.util.file.LoadConfig.customLoad;


public enum Groups {
	/**
	 * 敷衍注释
	 */
	BOT {
		@Override
		public void run(GroupMessage event) {
			event.getGroup().sendMessage(MessageUtils.quote(event.getMessage()).plus(customLoad("bot.version")));
		}
	},

	INFO {
		@Override
		public void run(GroupMessage event) {
			long QQ = event.getSender().getId();
			if(!isSqliteUser(QQ)) {
				main.getScheduler().async(() -> {
					StringBuffer response = new StringBuffer();
					response.append(Config.Server_Url)
						.append(Data.Get)
						.append("info")
						.append("?user="+getSqlite(QQ).get("USER"));
					String result = doGet(response.toString());
					if(result == null) {
                        result = doGet(response.toString());
                    }
					if(result == null) {
                        result = doGet(response.toString());
                    }
					if(result == null) {
						event.getGroup().sendMessage(customLoad("net.err"));
						return;
					}
					JSONObject playerdata = JSONObject.parseObject(new String(Base64.getDecoder().decode(JSONObject.parseObject(result).get("result").toString())));
					PlayerData data = JSON.toJavaObject(playerdata,PlayerData.class);
					Object[] params = {data.NAME,data.UUID,longToIp(data.IP),data.Country,data.Language,data.Level,data.Exp,data.Reqexp,data.Reqtotalexp,data.Buildcount,data.Cumulative_build,data.Pipe_build,data.Dismantledcount,data.Pvpwincount,data.Pvplosecount,data.Authority,simp(data.Authority_effective_time*1000L,data.Time_format),secToTime(data.Playtime),simp(data.LastLogin*1000L,data.Time_format),simp(data.Lastchat*1000L,data.Time_format),data.Killcount,data.Deadcount,data.Joincount,data.Breakcount,data.Online};
					event.getGroup().sendMessage(customLoad("player.info",params));
				});
			}
		}
	},

	STATUS {
		@Override
		public void run(GroupMessage event) {
			main.getScheduler().async(() -> {
				StringBuffer response = new StringBuffer();
				response.append(Config.Server_Url)
				.append(Data.Get)
				.append("status");
				//.append("&user="+Player.getSQLite(QQ).get("USER"));
				Log.info(response.toString());
				String result = doGet(response.toString());
				if(result == null) {
                    result = doGet(response.toString());
                }
				if(result == null) {
                    result = doGet(response.toString());
                }
				if(result == null) {
					event.getGroup().sendMessage(customLoad("net.err"));
					return;
				}
				String stat = JSONObject.parseObject(result).get("state").toString();
				final int err = 20003;
				if (Integer.parseInt(stat) == err) {
					event.getGroup().sendMessage(customLoad("status.close"));
					return;
				}
				JSONObject data = JSONObject.parseObject(new String(Base64.getDecoder().decode(JSONObject.parseObject(result).get("result").toString())));
				event.getGroup().sendMessage(customLoad("status.info",new Object[] {data.get("player"),data.get("map"),data.get("fps"),data.get("mob")}));
			});
		}
	},

	PING {
		@Override
		public void run(GroupMessage event) {
			main.getScheduler().async(() -> {
				String [] arr = toSg(event.getMessage()).split("\\s+");
				final int len = 2;
				if (arr.length >= len) {
					Consumer<Host> listener = result -> {
						if (result.name != null) {
							String mode = "Survival";
							if (result.mode.toString().contains("pvp")) {
								mode = "PVP";
							}
							if (result.mode.toString().contains("attack")) {
								mode = "Attack";
							}
							event.getGroup().sendMessage(customLoad("ping.yes", new Object[]{result.name, result.players, result.playerLimit, result.mapname, mode, result.wave, result.version, result.ping}));
						} else {
							event.getGroup().sendMessage(customLoad("ping.err"));
						}
					};
					try {
						String resultIP = arr[1];
						int port = 6567;
						if (arr[1].contains(":") && isNumeric(arr[1].split(":")[1])) {
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
						host.ping = (int) (System.currentTimeMillis() - start);
						listener.accept(host);
						socket.disconnect();
					} catch (Exception e) {
						listener.accept(new Host(null, arr[1], null, 0, 0, 0, null, null, 0, null));
					}
				} else {
					return;
				}
			});
		}
	},

	PINGS {
		@Override
		public void run(GroupMessage event) {
			if(!spings) {
				event.getGroup().sendMessage(customLoad("sleep.pings"));
				return;
			}
			main.getScheduler().async(() -> {
				String [] arr = toSg(event.getMessage()).split("\\s+");
				final int len = 2;
				if(arr.length < len) {
                    return;
                }
				StringBuffer response = new StringBuffer();
				Consumer<Host> listener = result -> {
					if(result.name != null) {
                        response.append(result.address.split(":")[1]+",");
                    }
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
				final int maxport = 65535;
				final int smlport = 1;
				if(sport < smlport || eport < smlport || sport > maxport || eport > maxport || eport < sport) {
                    return;
                }
				final int maxrange = 300;
				if((eport - sport) > maxrange) {
					event.getGroup().sendMessage(customLoad("range.max"));
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
					event.getGroup().sendMessage(customLoad("sleep.10"));
					Thread.sleep(10000);
				}catch(Exception e){
				}
				event.getGroup().sendMessage((response.length() > 0)?response.toString():customLoad("no.data"));
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
			main.getScheduler().async(() -> {
				String [] arr = toSg(event.getMessage()).split("\\s+");
				final int len = 3;
				if(arr.length < len) {
                    return;
                }
				StringBuffer response = new StringBuffer();
				for(int i=2,lens=arr.length;i<lens;i++) {
                    response.append(arr[i]);
                }
				event.getGroup().sendMessage(MessageUtils.quote(event.getMessage()).plus(new Baidu().translate(response.toString(),arr[1])));
			});
		}
	},

	GTR {
		@Override
		public void run(GroupMessage event) {
			main.getScheduler().async(() -> {
				String [] arr = toSg(event.getMessage()).split("\\s+");
				final int len = 3;
				if(arr.length < len) {
                    return;
                }
				StringBuffer response = new StringBuffer();
				for(int i=2,lens=arr.length;i<lens;i++) {
                    response.append(arr[i]);
                }
				event.getGroup().sendMessage(MessageUtils.quote(event.getMessage()).plus(new Google().translate(response.toString(),arr[1])));
			});
		}
	},

	WTR {
		@Override
		public void run(GroupMessage event) {
			main.getScheduler().async(() -> {
				String [] arr = toSg(event.getMessage()).split("\\s+");
				final int len = 3;
				if(arr.length < len) {
                    return;
                }
				StringBuffer response = new StringBuffer();
				for(int i=2,lens=arr.length;i<lens;i++) {
                    response.append(arr[i]);
                }
				event.getGroup().sendMessage(MessageUtils.quote(event.getMessage()).plus(new Bing().translate(response.toString(),arr[1])));
			});
		}
	},

	CSF {
		@Override
		public void run(GroupMessage event) {
				//aaa = (() -> event.getGroup().sendMessage(toSg(event.getMessage())));
		}
	},

	BOCR {
		@Override
		public void run(GroupMessage event) {
			String QQ = String.valueOf(event.getSender().getId());
			event.getGroup().sendMessage(customLoad("ocr.start"));
			Runnablex rx = new Runnablex(QQ+"OCR");
			rx.run = (() -> {
				Image fromId = MessageUtils.newImage(rx.data.toString());
				if(Config.BAIDU_OCR_ACT_TIME < getLocalTimeFromU()) {
					StringBuffer post = new StringBuffer();
					post.append("grant_type=client_credentials")
						.append("&client_id="+Config.BAIDU_OCR_ID)
						.append("&client_secret="+Config.BAIDU_OCR_KEY);
					String result = doPost("https://aip.baidubce.com/oauth/2.0/token",post.toString());
					if (isBlank(result)) {
						result = doPost("https://aip.baidubce.com/oauth/2.0/token",post.toString());
					}
					if (isBlank(result)) {
						result = doPost("https://aip.baidubce.com/oauth/2.0/token",post.toString());
					}
					if (isBlank(result)) {
						return;
					}
					JSONObject date = JSONObject.parseObject(result);
					Config.BAIDU_OCR_ACT = date.get("access_token").toString();
					Config.BAIDU_OCR_ACT_TIME = getLocalTimeFromU(Long.valueOf(date.get("expires_in").toString()));
				}
				String url = event.getBot().queryImageUrl(fromId);
				String rt = doPost("https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic?access_token="+Config.BAIDU_OCR_ACT,"url="+url);
				if (isBlank(rt)) {
					rt = doPost("https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic?access_token="+Config.BAIDU_OCR_ACT,"url="+url);
				}
				if (isBlank(rt)) {
					rt = doPost("https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic?access_token="+Config.BAIDU_OCR_ACT,"url="+url);
				}
				if (isBlank(rt)) {
					return;
				}
				StringBuffer sb = new StringBuffer();
				String text;
				JSONObject json = JSON.parseObject(rt);
				JSONArray rArray = json.getJSONArray("words_result");
				for (int i = 0; i < rArray.size(); i++) {
					JSONObject r = (JSONObject)rArray.get(i);
					text = r.get("words").toString();
					if (notisBlank(text)) {
		                sb.append(text);
		            }
				}
				event.getGroup().sendMessage(sb.toString());
			});
			Maps.addQQRunnable(QQ+"OCR",rx);
		}
	},

	BTOCR {
		@Override
		public void run(GroupMessage event) {
			String QQ = String.valueOf(event.getSender().getId());
			Runnablex rx = new Runnablex(QQ+"OCR");
			rx.run = (() -> {
				Image fromId = MessageUtils.newImage(rx.data.toString());
				event.getGroup().sendMessage(event.getBot().queryImageUrl(fromId));
			});
			Maps.addQQRunnable(QQ+"OCR",rx);
		}
	};

	public static Main main;
	private static boolean spings = true;
	private static List<String> list = new CopyOnWriteArrayList<String>();

	static {
		Groups[] season = values();        
		for (Groups s : season) {
            list.add(s.name());
        }
	}

	public static boolean contains(String name) {
		return list.contains(name);
	}

	public abstract void run(GroupMessage event);

	private static String toSg(MessageChain chain) {
		return chain.contentToString();
	}
}