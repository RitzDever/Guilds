package me.ritzdever.Guilds.schematics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BlockInfo {
    Location loc;
    byte data;
    Material type;
    int level;

    public BlockInfo(Location loc, byte data, Material type, int level) {
	this.loc = loc;
	this.data = data;
	this.type = type;
	this.level = level;
    }

    public void setblock() {
	for (Player p : Bukkit.getOnlinePlayers()) {
	    p.sendBlockChange(loc, type, data);
	}
    }
}
