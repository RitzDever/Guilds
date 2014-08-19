package me.ritzdever.Guilds.mechanics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public enum GuildInviteManager {
	INSTANCE;
	
	private Map<Guild, List<UUID>> inv = new HashMap<>();
	
	public void init(GuildManager gm){
		for(Guild g : gm.guildList()){
			Bukkit.getLogger().info(g.getName());;
			inv.put(g, new ArrayList<UUID>());
		}
	}
	
	public boolean isInvited(Guild g, Player p){
		Bukkit.getLogger().info(inv.size() + "");
		Bukkit.getLogger().info(g.getName());
		return (inv.get(g).contains(p.getUniqueId()) || g.isMember(p));
	}
	
	public boolean isInList(Guild g){
		return inv.keySet().contains(g);
	}
	
	public void addGuild(Guild g){
		if(isInList(g)) return;
		inv.put(g, new ArrayList<UUID>());
	}
	
	public void removeGuild(Guild g){
		if(isInList(g)) inv.remove(g);
	}
	
	public void removeInvite(Guild g, Player p){
		if(isInvited(g, p)) inv.get(g).remove(p.getUniqueId());
	}
	
	public void invite(Guild g, Player p){
		if(isInvited(g, p)) return;
		
		inv.get(g).add(p.getUniqueId());
	}


	public void accept(Guild g, Player p){
		g.addMember(p);
		inv.get(g).remove(p.getUniqueId());
	}
}
