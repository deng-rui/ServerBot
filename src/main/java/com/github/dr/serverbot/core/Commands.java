package com.github.dr.serverbot.core;

import com.github.dr.serverbot.Main;
import com.github.dr.serverbot.core.command.ClientCommands;
import com.github.dr.serverbot.core.command.ServerCommands;

public class Commands {

    private Main main;

    public Commands(Main main) {
        this.main = main;
        registers();
    }


	public void registers() {
		new ServerCommands(main);
        new ClientCommands(main);
	}
}