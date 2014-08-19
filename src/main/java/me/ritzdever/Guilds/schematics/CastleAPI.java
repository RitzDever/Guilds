package me.ritzdever.Guilds.schematics;



import me.ritzdever.Guilds.Guilds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CastleAPI {
        Guilds plugin;

    public CastleAPI(Guilds plugin){
        this.plugin = plugin;
    }
    public void createWorldIfNotexists(){
        if(Bukkit.getWorld("guildcastles") != null){
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv create guildcastles normal -g VoidWorld");
    }

    public void pasteDefaultCastleWithNPCS(Location loc, World world) {




    }

    public String getDirection(double rot) {
        if (0 <= rot && rot < 22.5) {
            return "North";
        } else if (22.5 <= rot && rot < 67.5) {
            return "Northeast";
        } else if (67.5 <= rot && rot < 112.5) {
            return "East";
        } else if (112.5 <= rot && rot < 157.5) {
            return "Southeast";
        } else if (157.5 <= rot && rot < 202.5) {
            return "South";
        } else if (202.5 <= rot && rot < 247.5) {
            return "Southwest";
        } else if (247.5 <= rot && rot < 292.5) {
            return "West";
        } else if (292.5 <= rot && rot < 337.5) {
            return "Northwest";
        } else if (337.5 <= rot && rot < 360.0) {
            return "North";
        } else {
            return null;
        }
    }



    public void spawnRandomFloatingIsland(Location loc){
        Schematic schematic = null;

       String schematicname = plugin.getConfig().getString("islands");

    List<String> elephantList = Arrays.asList(schematicname.split(","));

        Random rand = new Random();
        int randomNumber = rand.nextInt( elephantList.size());

        String finalname= elephantList.get(randomNumber);
        Bukkit.broadcastMessage(" " + randomNumber + finalname);
        String schematicextension=".schematic";
        String mostfinal = finalname+schematicextension;
        String t = "portal.schematic";
        try {
            schematic = Schematic.loadSchematic(new File(plugin.getDataFolder(), finalname));
        } catch (IOException e) {
            e.printStackTrace();
        }



        Schematic.pasteSchematic(loc.getWorld(), loc, schematic);

    }

    public void freezePlayerAtLocation(Player p, Location wheretoteleportbeforefreeze){
            p.teleport(wheretoteleportbeforefreeze);
            plugin.getFreeze().add(p);
    }






}
