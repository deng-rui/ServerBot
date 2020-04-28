package com.github.dr.serverbot.core;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

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

public class ClientCommands {

    private Main main;

    public ClientCommands(Main main) {
        this.main = main;
        registers();
    }


	public void registers() {
		JCommandManager.getInstance().register(this.main, new BlockingCommand(
                "ccc", new ArrayList<>(),"XXXXX","/ccc add"
        ) {
            @Override
            public boolean onCommandBlocking(CommandSender commandSender, List<String> list) {
                if(list.size() < 1){
                    return false;
                }
                switch (list.get(0)){
                    case "add":
                        if(list.size() < 4){
                            commandSender.sendMessageBlocking("XXXX");
                            return true;
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
	}
}