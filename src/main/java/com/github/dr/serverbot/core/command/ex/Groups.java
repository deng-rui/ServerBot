package com.github.dr.serverbot.core.command.ex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.dr.serverbot.Main;
import com.github.dr.serverbot.data.global.Config;
import com.github.dr.serverbot.data.global.Data;
import com.github.dr.serverbot.data.global.Maps;
import com.github.dr.serverbot.data.global.cache.Runnablex;
import com.github.dr.serverbot.net.Net;
import com.github.dr.serverbot.util.ReExp;
import com.github.dr.serverbot.util.alone.translation.Baidu;
import com.github.dr.serverbot.util.alone.translation.Bing;
import com.github.dr.serverbot.util.alone.translation.Google;
import com.github.dr.serverbot.util.log.Log;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static com.github.dr.serverbot.net.HttpRequest.doGet;
import static com.github.dr.serverbot.net.HttpRequest.doPost;
import static com.github.dr.serverbot.net.Net.pingServer;
import static com.github.dr.serverbot.util.DateUtil.getLocalTimeFromU;
import static com.github.dr.serverbot.util.IsUtil.*;
import static com.github.dr.serverbot.util.encryption.Topt.newTotp;
import static com.github.dr.serverbot.util.file.LoadConfig.customLoad;


/**
 * @author Dr
 */
public enum Groups {
	/**
	 * 敷衍注释
	 */
	BOT {
		@Override
		public void run(GroupMessageEvent event) {
			event.getGroup().sendMessage(new QuoteReply(event.getSource()).plus(customLoad("bot.version")));
		}
	},

	STATUS {
		@Override
		public void run(GroupMessageEvent event) {
			main.getScheduler().async(() -> {
				StringBuffer response = new StringBuffer();
				response.append(Config.Server_Url)
				.append(Data.Get)
				.append("status");
				//.append("&user="+Player.getSQLite(QQ).get("USER"));
				Log.info(response.toString());
				String result = doGet(response.toString(),newTotp(Config.TOPT_KEY));
				if(result == null) {
                    result = doGet(response.toString(),newTotp(Config.TOPT_KEY));
                }
				if(result == null) {
                    result = doGet(response.toString(),newTotp(Config.TOPT_KEY));
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
		public void run(GroupMessageEvent event) {
			main.getScheduler().async(() -> {
				String [] arr = toString(event.getMessage()).split("\\s+");
				final int len = 2;
				if (arr.length >= len) {
					Consumer<Net.Host> listener = result -> {
						if (result.name != null) {
							String mode = "Survival";
							if (result.mode.contains("3")) {
								mode = "PVP";
							} else if (result.mode.contains("2")) {
								mode = "Attack";
							} else if (result.mode.contains("1")) {
								mode = "Sandbox";
							}
							event.getGroup().sendMessage(customLoad("ping.yes", new Object[]{result.name, result.players, result.playerLimit, result.mapname, mode, result.wave, result.version, result.ping}));
						} else {
							event.getGroup().sendMessage(customLoad("ping.err"));
						}
					};
					String ip = arr[1];
					int port = 6567;
					if (arr[1].contains(":") && isNumeric(arr[1].split(":")[1])) {
						ip = arr[1].split(":")[0];
						port = Integer.parseInt(arr[1].split(":")[1]);
					}
					pingServer(listener,ip,port);
				} else {
					return;
				}
			});
		}
	},

	PINGS {
		@Override
		public void run(GroupMessageEvent event) {
			if(!spings) {
				event.getGroup().sendMessage(customLoad("sleep.pings"));
				return;
			}
			main.getScheduler().async(() -> {
				String [] arr = toString(event.getMessage()).split("\\s+");
				final int len = 2;
				if(arr.length < len) {
                    return;
                }
				StringBuffer response = new StringBuffer();
				Consumer<Net.Host> listener = result -> {
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
						sport = Integer.parseInt(arr[1].split(":")[1].split("-")[0]);
						eport = Integer.parseInt(arr[1].split(":")[1].split("-")[1]);
					}
				}catch(Exception e){
					return;
				}
				final int maxport = 65535;
				final int smlport = 1;
				if (sport < smlport || eport < smlport || sport > maxport || eport > maxport || eport < sport) return;
				spings = false;
				eport++;
				event.getGroup().sendMessage(customLoad("sleep.60"));
				for (int i = sport; i < eport; i++) {
					final int port = i;
					final String ip = resultIP;
					Data.Thred_service.execute(() -> {
						pingServer(listener,ip,port);
					});
				}
				try {
					Thread.sleep(60000);
				} catch (Exception e) {
				}
				event.getGroup().sendMessage((response.length() > 0) ? response.toString() : customLoad("no.data"));
				spings = true;
			});
		}
	},

	/*
	 * 个人发疯作品 自行取舍 AGUN3!
	 */
	BTR {
		@Override
		public void run(GroupMessageEvent event) {
			main.getScheduler().async(() -> {
				String [] arr = toString(event.getMessage()).split("\\s+");
				final int len = 3;
				if(arr.length < len) {
                    return;
                }
				StringBuffer response = new StringBuffer();
				for(int i=2,lens=arr.length;i<lens;i++) {
                    response.append(" "+arr[i]);
                }
				event.getGroup().sendMessage(new QuoteReply(event.getSource()).plus(new Baidu().translate(response.toString(),arr[1])));
			});
		}
	},

	GTR {
		@Override
		public void run(GroupMessageEvent event) {
			main.getScheduler().async(() -> {
				String [] arr = toString(event.getMessage()).split("\\s+");
				final int len = 3;
				if(arr.length < len) {
                    return;
                }
				StringBuffer response = new StringBuffer();
				for(int i=2,lens=arr.length;i<lens;i++) {
                    response.append(arr[i]);
                }
				event.getGroup().sendMessage(new QuoteReply(event.getSource()).plus(new Google().translate(response.toString(),arr[1])));
			});
		}
	},

	WTR {
		@Override
		public void run(GroupMessageEvent event) {
			main.getScheduler().async(() -> {
				String [] arr = toString(event.getMessage()).split("\\s+");
				final int len = 3;
				if(arr.length < len) {
                    return;
                }
				StringBuffer response = new StringBuffer();
				for(int i=2,lens=arr.length;i<lens;i++) {
                    response.append(arr[i]);
                }
				event.getGroup().sendMessage(new QuoteReply(event.getSource()).plus(new Bing().translate(response.toString(),arr[1])));
			});
		}
	},

	CSF {
		@Override
		public void run(GroupMessageEvent event) {
				//aaa = (() -> event.getGroup().sendMessage(toString(event.getMessage())));
		}
	},

	BOCR {
		@Override
		public void run(GroupMessageEvent event) {
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
					Object result = new ReExp() {
						@Override
				        protected Object runs() throws Exception {
				        	String r = doPost("https://aip.baidubce.com/oauth/2.0/token",post.toString());			           
				            if (r == null) {
				                throw new Exception();
				            }
				            return r;
				        }
					}.setSleepTime(10).setRetryFreq(3).execute();
					if (isBlank(result)) {
						return;
					}
					JSONObject date = JSONObject.parseObject(result.toString());
					Config.BAIDU_OCR_ACT = date.get("access_token").toString();
					Config.BAIDU_OCR_ACT_TIME = getLocalTimeFromU(Long.valueOf(date.get("expires_in").toString()));
				}
				String url = event.getBot().queryImageUrl(fromId);
				Object rt = new ReExp() {
					@Override
			        protected Object runs() throws Exception {
			        	String r = doPost("https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic?access_token="+Config.BAIDU_OCR_ACT,"url="+url);			           
			            if (r == null) {
			                throw new Exception();
			            }
			            return r;
			        }
				}.setSleepTime(10).setRetryFreq(3).execute();
				if (isBlank(rt)) {
					return;
				}
				StringBuffer sb = new StringBuffer();
				String text;
				JSONObject json = JSON.parseObject(rt.toString());
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
		public void run(GroupMessageEvent event) {
			String QQ = String.valueOf(event.getSender().getId());
			Runnablex rx = new Runnablex(QQ+"OCR");
			rx.run = (() -> {
				Image fromId = MessageUtils.newImage(rx.data.toString());
				event.getGroup().sendMessage(event.getBot().queryImageUrl(fromId));
			});
			Maps.addQQRunnable(QQ+"OCR",rx);
		}
	},

	给我 {
		@Override
		public void run(GroupMessageEvent event) {
			event.getSender().mute(5);
			event.getGroup().sendMessage("5S");
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

	public abstract void run(GroupMessageEvent event);

	static String toString(MessageChain chain) {
		return chain.contentToString();
	}
}