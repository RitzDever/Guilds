package me.ritzdever.Guilds.mechanics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


import me.ritzdever.Guilds.Guilds;
import me.ritzdever.Guilds.commands.MySQLGuilds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GuildManager {
	
	private Collection<Guild> guilds = new ArrayList<Guild>();
	private MySQLGuilds sql;
	
	public GuildManager(Guilds g){
		this.sql = g.getMySQLGuilds();
		
		Collection<String> t = sql.getAllGuilds();
		
		for(String s : t){
			guilds.add(new Guild(s));
		}
	}
	
	public boolean guildExist(String s){
		
		Bukkit.getLogger().info("INVOKED");
		if(guilds.size() == 0){
			Bukkit.getLogger().info("INVOKED 2" + guilds.size());
			return false;
		}
		Bukkit.getLogger().info(guilds.toString());;
		for(Guild g : guilds){
			Bukkit.getLogger().info("I'M IN YR LOOP");
			if(g.getName().equalsIgnoreCase(s)) return true;
		}
		return false;
	}
	
	public void createGuild(Player p, Guild g){
		guilds.add(g);
		GuildInviteManager.INSTANCE.addGuild(g);
		sql.createGuild(g);
	}
	
	public Guild getGuild(Player lead){
		
		for(Guild g : guilds){
			if(g.getLeader().equals(lead.getUniqueId())) return g;
		}
		
		return null;
	}
	
	public boolean isLeaderOfAnyGuild(Player p){
		for(Guild g : guildList())
			if(g.getLeader().equals(p.getUniqueId())) return true;
		return false;
	}
	
	public Guild getGuild(String s){
		for(Guild g : guilds)
			if(g.getName().equalsIgnoreCase(s)) return g;
		
		return null;
	}
	
	public Guild getPartOfGuild(Player mem){
		for(Guild g : guilds){
			if(g.getMembers(true).contains(mem.getUniqueId())) return g;
		}
		
		return null;
	}
	
	public void deleteGuild(Player lead){
		if(getGuild(lead) == null) return;

		sql.deleteGuild(getGuild(lead));
		guilds.remove(getGuild(lead));
	}
	
	public boolean playerIsInGuild(Player p){
		for(Guild g : guilds)
			if(g.getMembers(true).contains(p.getUniqueId())) return true;
		return false;
	}
	
	public Collection<Guild> guildList(){
		return guilds;
	}
	
	public void save(){
		Collection<Guild> c = Collections.synchronizedCollection(guilds);
		
		for(Guild g : c){
			sql.updateGuild(g);
		}
	}
}
