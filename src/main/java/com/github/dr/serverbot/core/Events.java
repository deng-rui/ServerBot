package com.github.dr.serverbot.core;

import com.github.dr.serverbot.Main;
import com.github.dr.serverbot.core.command.ex.Groups;
import com.github.dr.serverbot.data.global.Data;
import com.github.dr.serverbot.data.global.Maps;
import com.github.dr.serverbot.data.global.cache.Runnablex;
import net.mamoe.mirai.console.plugins.Config;
import net.mamoe.mirai.message.GroupMessage;
import com.github.dr.serverbot.util.log.Log;
import net.mamoe.mirai.message.data.MessageChain;

import static com.github.dr.serverbot.core.command.ex.Groups.contains;
import static com.github.dr.serverbot.util.DateUtil.getLocalTimeFromU;
import static com.github.dr.serverbot.util.ExtractUtil.findMatchString;
import static com.github.dr.serverbot.util.IsUtil.notisBlank;
import static com.github.dr.serverbot.util.file.LoadConfig.customLoad;

public class Events {

	private Main main;
	private String API;
	private Config setting;
	private boolean spings = true;

	public Events(Main main) {
		this.main = main;
		Groups.main = main;
		registers();
	}

	public void registers() {
		/*
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

		//this.main.getEventListener().subscribeAlways(TempMessage.class, (event) -> {
*/
		// 想杀自己祭天 23333
		this.main.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event) -> {
			if(!Data.QunData.contains(event.getGroup().getId())) {
                return;
            }
			String QQ = String.valueOf(event.getSender().getId());
			
			if(Maps.isQQRunnable(QQ+"OCR")) {
				String img = findMatchString(event.getMessage().toString(),"\\{.{8}-(.{4}-){3}.{12}\\}\\.mirai");
				if(Maps.getQQRunnable(QQ+"OCR").endtime < getLocalTimeFromU()) {
					Maps.removeQQRunnable(QQ+"OCR");
					return;
				}
				if(notisBlank(img)) {
					Runnablex rx = Maps.getQQRunnable(QQ+"OCR");
					rx.data = img;
					rx.run.run();
					Maps.removeQQRunnable(QQ+"OCR");
				} else {
					event.getGroup().sendMessage(customLoad("img.no"));
					return;
				}
			}
			String arr = toString(event.getMessage()).split("\\s+")[0];
			if (arr.length() < 10) {
				String msgs = arr.substring(1,arr.length()).toUpperCase();
				if (contains(msgs)) {
					Groups strategy = Groups.valueOf(msgs);
					strategy.run(event);
				}
			}
		});
	}

	public static String toString(MessageChain chain) {
		return chain.contentToString();
	}
}