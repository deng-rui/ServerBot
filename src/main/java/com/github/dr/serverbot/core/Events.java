package com.github.dr.serverbot.core;

import com.github.dr.serverbot.Main;
import com.github.dr.serverbot.core.command.ex.Friend;
import com.github.dr.serverbot.core.command.ex.Groups;
import com.github.dr.serverbot.data.global.Data;
import com.github.dr.serverbot.data.global.Maps;
import com.github.dr.serverbot.data.global.cache.Runnablex;
import net.mamoe.mirai.console.plugins.Config;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
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
		registers();
	}

	public void registers() {

		this.main.getEventListener().subscribeAlways(NewFriendRequestEvent.class, (event) -> {
			event.accept();
		});

		this.main.getEventListener().subscribeAlways(FriendMessageEvent.class, (event) -> {
			String arr = toString(event.getMessage()).split("\\s+")[0];
			final int maxlg = 10;
			if (arr.length() < maxlg) {
				String msgs = arr.substring(1,arr.length()).toUpperCase();
				if (contains(msgs)) {
					Friend strategy = Friend.valueOf(msgs);
					strategy.run(event);
				}
			}
		});

		this.main.getEventListener().subscribeAlways(GroupMessageEvent.class, (GroupMessageEvent event) -> {
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
			final int maxlg = 10;
			if (arr.length() < maxlg) {
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