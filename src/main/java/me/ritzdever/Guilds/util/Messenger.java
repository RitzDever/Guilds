package me.ritzdever.Guilds.util;

import org.bukkit.command.CommandSender;

public class Messenger {
	private Messenger(){}
	
	public static void msg(CommandSender s, String str){
		s.sendMessage(str);
	}
}
