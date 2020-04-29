package com.github.dr.serverbot.core.command;

import java.util.ArrayList;
import java.util.List;

import net.mamoe.mirai.console.command.BlockingCommand;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.JCommandManager;

import com.github.dr.serverbot.Main;
import com.github.dr.serverbot.data.global.Config;
import com.github.dr.serverbot.data.global.Data;

import static com.github.dr.serverbot.data.json.Json.getData;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ServerCommands {
	private Main main;

    public ServerCommands(Main main) {
        this.main = main;
        registers();
    }


	public void registers() {
		JCommandManager.getInstance().register(this.main, new BlockingCommand(
                "reloadconfig", new ArrayList<>(),"重新加载Config","/reloadconfig"
        ) {
            @Override
            public boolean onCommandBlocking(CommandSender commandSender, List<String> list) {
                if(list.size() > 0) 
                    return false;
                JSONObject date = getData();
				JSONArray array = (JSONArray) JSONArray.parse(date.get("qun").toString());
				for (int i = 0; i < array.size(); i++) 
					Data.QunData.add(Long.valueOf(array.getString(i)));
				Config.Server_Url = date.get("Server_Url").toString();
                return true;
            }
        });
	}
}