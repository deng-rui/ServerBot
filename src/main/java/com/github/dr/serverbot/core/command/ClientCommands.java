package com.github.dr.serverbot.core.command;

import com.github.dr.serverbot.Main;
import com.github.dr.serverbot.core.command.ex.Friend;
import com.github.dr.serverbot.core.command.ex.Groups;
//import com.github.dr.serverbot.core.command.ex.ClientCommands;

public class ClientCommands {

    public ClientCommands(Main main) {
        Groups.main = main;
        Friend.main = main;
    }
}
