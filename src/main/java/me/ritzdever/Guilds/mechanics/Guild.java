package me.ritzdever.Guilds.mechanics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;


import me.ritzdever.Guilds.util.MySQLSerializable;
import org.bukkit.entity.Player;

public class Guild implements MySQLSerializable {
	private String name;
	private Collection<UUID> cacheMembers;
	private Collection<UUID> cacheBanMembers;
	private UUID leader;
	
	public Guild(String n, Collection<UUID> mem, Collection<UUID> banMem, UUID leader){
		this.name = n;
		
		this.cacheMembers = mem;
		
		this.cacheMembers = banMem;
		
		this.leader = leader;
	}
	
	public Guild(String n, UUID leader){
		this.name = n;
		this.leader = leader;
		this.cacheMembers = new ArrayList<UUID>();
		this.cacheBanMembers = new ArrayList<UUID>();
	}
	
	public Guild(String s){
		String[] $ = s.split(":");
		
		this.name = $[0];
		
		this.cacheMembers = new ArrayList<UUID>();
		this.cacheBanMembers = new ArrayList<UUID>();
		
		if(!$[1].equalsIgnoreCase("none")){
			for(String m : $[1].split(","))
				cacheMembers.add(UUID.fromString(m));
		}
		
		if(!$[2].equalsIgnoreCase("none")){
			for(String m : $[2].split(","))
				cacheBanMembers.add(UUID.fromString(m));
		}
		
		
		
		this.leader = UUID.fromString($[3]);
	}
	
	public UUID getLeader(){
		return leader;
	}
	
	public Collection<UUID> getMembers(boolean leaderInc){
		Collection<UUID> t = new ArrayList<UUID>();
		t.addAll(cacheMembers);
		
		if(leaderInc) t.add(leader);
		return t;
	}
	
	public Collection<UUID> getBanMembers(){
		return cacheBanMembers;
	}
	
	public String getName(){
		return name;
	}
	
	public void addMember(Player p){
		if(isMember(p) || isBanned(p))
			return;
		
		cacheMembers.add(p.getUniqueId());
	}
	
	public boolean isMember(Player p){
		return cacheMembers.contains(p.getUniqueId()) || leader.equals(p.getUniqueId());
	}
	
	public boolean isBanned(Player p){
		return cacheBanMembers.contains(p.getUniqueId());
	}
	
	public void removeMember(Player p){
		if(!isMember(p)) return;
		
		cacheMembers.remove(p.getUniqueId());
	}
	
	public void removeBanMember(Player p){
		if(!cacheBanMembers.contains(p.getUniqueId())) return;
		
		cacheBanMembers.remove(p.getUniqueId());
		
	}
	
	public void banMember(Player p){
		
		if(isBanned(p)) return;
		
		cacheBanMembers.add(p.getUniqueId());
	}
	
	public void unbanMember(Player p){
		if(!isBanned(p)) return;
		
		cacheBanMembers.remove(p.getUniqueId());
	}
	
	public String serialize() {
		String to = "";
		String sep = ":";
		
		StringBuilder sb = new StringBuilder();
		StringBuilder $b = new StringBuilder();
		
		if(cacheMembers.size() == 0){
			sb.append("none");
		}else{
			for(UUID s : cacheMembers)
				sb.append(s).append(",");
		}
		
		if(cacheBanMembers.size() == 0){
			$b.append("none");
		}else{
			for(UUID s : cacheBanMembers)
				$b.append(s).append(",");
		}
		
		to = (name + sep + sb.toString().trim() + sep + $b.toString().trim() + sep + leader.toString());
		
		return to;
	}
	
	public static Guild deserialize(String s){
		return new Guild(s);
	}
}
