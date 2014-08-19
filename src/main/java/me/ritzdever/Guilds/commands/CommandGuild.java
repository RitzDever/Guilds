package me.ritzdever.Guilds.commands;


import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;


import me.ritzdever.Guilds.Guilds;
import me.ritzdever.Guilds.mechanics.Guild;
import me.ritzdever.Guilds.mechanics.GuildInviteManager;
import me.ritzdever.Guilds.mechanics.GuildManager;
import me.ritzdever.Guilds.schematics.Schematic;
import me.ritzdever.Guilds.util.Messenger;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CommandGuild
        implements CommandExecutor
{
    Guilds plugin;
    GuildManager gm;
    GuildInviteManager gi;

    public static int TaskID;

    public CommandGuild(Guilds plugin)
    {
        this.plugin = plugin;
        this.gm = plugin.getGuildManager();
        gi = GuildInviteManager.INSTANCE;
    }
    public static void msg(CommandSender s, String str){
        s.sendMessage(str);
    }
    public boolean isSpawnable(Location loc){
        for (int x = -20; x <= 20; x++) {
            for (int y = -20; y <= 20; y++) {
                for (int z = -20; z <= 20; z++) {
                    if(loc.getWorld().getBlockAt(loc).getRelative(x, y, z).getType() == Material.OBSIDIAN && (x != 0 || y != 0 || z != 0)) {

                       return false;
                    }

                }
            }
        }

        return true;

    }

    public boolean onCommand(CommandSender s, Command c, String l,final  String[] a){
    	if(!(s instanceof Player)){
    		msg(s, RED + "Only players can use this command!");
    		return true;
    	}
    	
    	final Player p = (Player) s;
    	
    	if(a.length == 0){
    		msg(p, RED + "Too few arguments!");
    		return true;
    	}
        if(a[0].equalsIgnoreCase("castles"))
        {


            Vector vec = p.getLocation().toVector(); // get player's location vector
            Vector dir = p.getLocation().getDirection(); // get player's facing
            vec = vec.add(dir.multiply(8)); // add player's direction * 3 to the location (getting ~3 blocks in front of player)
            Location locationtime = vec.toLocation(p.getWorld()).subtract(0, 4, 0); // convert back to location



            plugin.getCastleAPI().spawnRandomFloatingIsland(locationtime);



            return true;
        }

        if(a[0].equalsIgnoreCase("move")){
            if(!p.hasPermission("guild.move")){
                msg(p, RED + "No permission!");

                return true;
            }
            if(a.length>=2){
                p.sendMessage(ChatColor.RED + "Too many agruements!");
                return true;
            }

            if(!gm.playerIsInGuild(p)){
                msg(p, RED + "You are not in a guild!");
                return true;
            }

            if(gm.getGuild(p) == null){
                msg(p, RED + "You are not in a guild!");
                return true;
            }
            if(!isSpawnable(p.getLocation())){
                p.sendMessage(ChatColor.RED + "Portal not spawnable at your current location!");
                return true;

            }
            p.sendMessage(ChatColor.GREEN + "Removed and moved to your new location!!");

            for (int x = -20; x <= 20; x++) {
                for (int y = -20; y <= 20; y++) {
                    for (int z = -20; z <= 20; z++) {
                        double xx = 1 + plugin.getConfig().getDouble("guilds." + gm.getGuild(p).getName()+ ".x"  );
                        double yy = 1 +plugin.getConfig().getDouble("guilds." + gm.getGuild(p).getName() + ".y" );
                        double zz = 1 + plugin.getConfig().getDouble("guilds." + gm.getGuild(p).getName() + ".z" );
                        Location loc = new Location(p.getWorld(), xx, yy, zz);
                        if(p.getWorld().getBlockAt(loc).getRelative(x, y, z).getType() == Material.OBSIDIAN && (x != 0 || y != 0 || z != 0)) {
                            p.getWorld().getBlockAt(loc).getRelative(x, y, z).setType(Material.AIR);



                            //delete hologram
                            //fix config
                     //logout events
                            //null in config

                        }

                    }
                }
            }


                Schematic schematic = null;
                try {
                    schematic = Schematic.loadSchematic(new File(plugin.getDataFolder(), "portal.schematic"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Schematic.pasteSchematic(p.getWorld(), p.getLocation(), schematic);
                plugin.getConfig().set("guilds." + gm.getGuild(p).getName()+ ".x", p.getLocation().getX());
                plugin.getConfig().set("guilds." +gm.getGuild(p).getName() + ".y", p.getLocation().getY());
                plugin.getConfig().set("guilds." + gm.getGuild(p).getName() + ".z", p.getLocation().getZ());
                plugin.saveConfig();

            for(Hologram hologram: HoloAPI.getManager().getHologramsFor(plugin)){
                for(String da : hologram.getLines()){
                   if(da.equalsIgnoreCase(ChatColor.AQUA + "Guild Portal: " + ChatColor.YELLOW + gm.getGuild(p).getName())){
                       hologram.move(p.getLocation().add(0, 6, 0));
                       return true;
                   }
                }

            }


        }
    	
    	if(a[0].equalsIgnoreCase("create")){
    		if(!p.hasPermission("guild.create")){
    			msg(p, RED + "No permission!");
    			return true;
    		}
    		
    		if(a.length < 2){
    			msg(p, RED + "Must specify a name!");
    			return true;
    		}
    		
    		if(a.length > 2){
    			msg(p, RED + "Guild names cannot be more than 1 word!");
    			return true;
    		}
    		
    		if(!a[1].matches("[A-Za-z0-9]+")){
    			msg(p, RED + "Name needs to be alphanumeric!");
    			return true;
    		}
    		
    		if(a[1].equalsIgnoreCase("none") || a[1].contains("none")){
    			msg(p, RED + "Not valid name!");
    			return true;
    		}
    		
    		if(gm.guildExist(a[1])){
    			msg(p, RED + "That guild already exists! Please sepcify a different name!");
    			return true;
    		}
    		
    		if(gm.playerIsInGuild(p)){
    			msg(p, RED + "You must leave your current guild first!");
    			return true;
    		}
    		
    		if(gm.getGuild(p) != null){
    			msg(p, RED + "You must disband your guild before creating another one!");
    			return true;
    		}
            plugin.getCpen().put(p, a[1]);
    		
    		gm.createGuild(p, new Guild(a[1], p.getUniqueId()));
            gm.save();

    		
    		msg(p, YELLOW + "You have successfully created a guild!");
            msg(p, GREEN + "You have received an eye of the ender, you have 21 seconds to right click a block underneath you for where your guild portal to be. Logging out a portal will result in a disbanded guild!");
            ItemStack im = new ItemStack(Material.EYE_OF_ENDER);
            ItemMeta itemMeta = im.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GREEN + "Portal Maker-" + ChatColor.YELLOW + p.getName());
            plugin.getEpeople().add(p);
            im.setItemMeta(itemMeta);
            p.getInventory().addItem(im);
           TaskID =  Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                int counter=6;
                @Override
                public void run()  {
                    if(counter<=0){

                    if(isSpawnable(p.getLocation())==true){
                        p.sendMessage(ChatColor.GREEN + "You didn't select a portal area, making one for you atm :D");







                        Schematic schematic = null;
                        try {
                            schematic = Schematic.loadSchematic(new File(plugin.getDataFolder(), "portal.schematic"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Schematic.pasteSchematic(p.getWorld(),p.getLocation(), schematic);



                        plugin.getConfig().set("guilds." + gm.getGuild(p).getName()+ ".x", p.getLocation().getX());
                        plugin.getConfig().set("guilds." +gm.getGuild(p).getName() + ".y", p.getLocation().getY());
                        plugin.getConfig().set("guilds." + gm.getGuild(p).getName() + ".z", p.getLocation().getZ());
                        plugin.saveConfig();



                       Hologram hologram = new HologramFactory(plugin)    // Replace "myPlugin" with your plugin instance
                               .withLocation(new Vector(p.getLocation().getX(),p.getLocation().getY()+6,p.getLocation().getZ()), p.getWorld().getName())
                               .withText(ChatColor.AQUA + "Guild Portal: " + ChatColor.YELLOW + gm.getGuild(p).getName())
                               .build();

                        p.getInventory().remove(Material.EYE_OF_ENDER);
                        Bukkit.getScheduler().cancelTask(TaskID);
                        plugin.getEpeople().remove(p);

                        return;
                    }



                        if(isSpawnable(p.getLocation())==false){
                            p.sendMessage(ChatColor.RED + "Another portal detected. Moving Portal to a different location!");
                            for (int x = (int) p.getLocation().getX(); x <= 1000000000; x++) {
                                for (int y = (int) p.getLocation().getY(); y <= 1000000000; y++) {
                                    for (int z = (int) p.getLocation().getZ(); z <= 1000000000; z++) {

                                        double xx = plugin.getConfig().getDouble("guilds." + gm.getGuild(p).getName()+ ".x");
                                        double yy = plugin.getConfig().getDouble("guilds." + gm.getGuild(p).getName() + ".y");
                                        double zz = plugin.getConfig().getDouble("guilds." + gm.getGuild(p).getName() + ".z");
                                        Location location = new Location(p.getWorld(), xx, yy, zz);
                                        if(isSpawnable(location)==true){
                                            p.sendMessage(ChatColor.GREEN + "Spawned portal at location(x,y,z):" + location.getX() + " " + location.getY() + " " + location.getZ());
                                            Schematic schematic = null;
                                            try {
                                                schematic = Schematic.loadSchematic(new File(plugin.getDataFolder(), "portal.schematic"));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }


                                            Schematic.pasteSchematic(p.getWorld(),location, schematic);

                                            Hologram hologram = new HologramFactory(plugin)    // Replace "myPlugin" with your plugin instance
                                                    .withLocation(new Vector(p.getLocation().getX(),p.getLocation().getY()+6,p.getLocation().getZ()), p.getWorld().getName())
                                                    .withText(ChatColor.AQUA + "Guild Portal: " + ChatColor.YELLOW + gm.getGuild(p).getName())
                                                    .build();

                                            Bukkit.getScheduler().cancelTask(TaskID);
                                            plugin.getEpeople().remove(p);
                                            return;

                                        }


                                    }
                                }
                            }

                            return;
                        }
































                        return;
                    }
                    counter--;
                    p.sendMessage(ChatColor.AQUA + "Time Left: " + ChatColor.YELLOW + counter);
                }
            }, 0, 20);


    		return true;
    	}
    	
    	if(a[0].equalsIgnoreCase("invite")){
    		if(!p.hasPermission("guild.invite")){
    			msg(p, RED + "No permission!");
    			return true;
    		}
    		if(a.length < 2){
    			msg(p, RED + "Please specify invitee!");
    			return true;
    		}
    		

/*

            if(!plugin.isOnline(a[1])) {
                p.sendMessage(ChatColor.RED + "Player isnt online!");
                return true;
            }
*/

    		if(!a[1].matches("[A-Za-z0-9]+")){
    			msg(p, RED + "Name needs to be alphanumeric!");
    			return true;
    		}
    		
    		if(a[1].equalsIgnoreCase("none")){
    			msg(p, RED + "Invalid name!");
    			return true;
    		}
    		
    		if(a[1].equalsIgnoreCase(p.getName())){
    			msg(p, RED + "Cannot invite yourself!");
    			return true;
    		}
    		
    		if(gm.getGuild(p) == null){
    			msg(p, RED + "You do not have a guild!");
    			return true;
    		}
            /*
            GamePlayer gamePlayer = new GamePlayer(a[1], plugin);


    		if(gm.getGuild(p).getBanMembers().contains(      gamePlayer.getUUID())){
    			msg(p, RED + "The player is banned from your guild!");
    			return true;
    		}
    		
    		if(gm.getGuild(p).getMembers(false).contains(      gamePlayer.getUUID())){
    			msg(p, RED + "The player is already in your guild!");
    			return true;
    		}



    		if(gi.isInvited(gm.getGuild(p), tar)){
    			msg(p, RED + "The player is already invited!");
    			return true;
    		}


    		gi.invite(gm.getGuild(p), tar);


*/
    		msg(p, GREEN + "Successfully invited: " + YELLOW + a[1]);

            //gamePlayer.sendMessage(ChatColor.RED + "Does this work? PM me - equinox" + ChatColor.GREEN + "You have been invited to: " + ChatColor.YELLOW + gm.getGuild(p).getName() + ChatColor.GREEN + " Type " + ChatColor.YELLOW + "/guild accept " + gm.getGuild(p).getName() + ChatColor.GREEN + " to join!");
    		return true;
    	}
    	
    	if(a[0].equalsIgnoreCase("accept")){
    		if(!p.hasPermission("guild.accept")){
    			msg(p, RED + "No permission!");
    			return true;
    		}
    		
    		if(a.length < 2){
    			msg(p, RED + "Specify which guild to accept!");
    			return true;
    		}
    		
    		if(!gm.guildExist(a[1])){
    			msg(p, RED + "Guild does not exist!");
    			return true;
    		}
    		
    		if(gm.playerIsInGuild(p)){
    			msg(p, RED + "You are already in a guild. Please leave or disband your guild!");
    			return true;
    		}
    		
    		if(!gi.isInvited(gm.getGuild(a[1]), p)){
    			msg(p, RED + "You are not invited to that guild!");
    			return true;
    		}
    		
    		gi.accept(gm.getGuild(a[1]), p);
    		msg(p, GREEN + "You have successfully join guild: " + YELLOW + a[1]);
    		
    		for(UUID u : gm.getGuild(a[1]).getMembers(true)){
    			if(Bukkit.getPlayer(u) == null) continue;
    			msg(Bukkit.getPlayer(u), YELLOW + p.getName() + " has join your guild!");
    		}
    		return true;
    	}
    	
    	if(a[0].equalsIgnoreCase("kick")){
    		if(!p.hasPermission("guild.kick")){
    			msg(p, RED + "No permission!");
    			return true;
    		}
    		
    		if(a.length < 2){
    			msg(p, RED + "Specify someone to kick!");
    			return true;
    		}
    		
    		Player tar = Bukkit.getPlayerExact(a[1]);
    		
    		if(tar == null){
    			msg(p, RED + "Player must be online!");
    			return true;
    		}
    		
    		if(!gm.isLeaderOfAnyGuild(p)){
    			msg(p, RED + "Only leaders can perform this command for your guild!");
    			return true;
    		}
    		
    		if(gm.getGuild(p) == null){
    			msg(p, RED + "You do not have a guild!");
    			return true;
    		}
    		
    		if(a[1].equalsIgnoreCase(p.getName())){
    			msg(p, RED + "Cannot kick yourself.");
    			return true;
    		}
    		
    		if(!gm.getGuild(p).getMembers(false).contains(tar.getUniqueId())){
    			msg(p, RED + "Player is not part of your guild!");
    			return true;
    		}
    		
    		gm.getGuild(p).removeMember(tar);
    		
    		msg(tar, YELLOW + "You have been kicked from " + AQUA + gm.getGuild(p).getName());
    		
    		for(UUID u : gm.getGuild(p).getMembers(true)){
    			if(Bukkit.getPlayer(u) == null) continue;
    			msg(Bukkit.getPlayer(u), GREEN + tar.getName() + YELLOW + " has been kicked from your guild!");
    		}
    		
    		return true;
    		
    	}
    	
    	if(a[0].equalsIgnoreCase("ban")){
    		
    		if(!p.hasPermission("guild.ban")){
    			msg(p, RED + "No permission");
    			return true;
    		}
    		
    		if(a.length < 2){
    			msg(p, RED + "Please specify who to ban.");
    			return true;
    		}
    		
    		Player tar = Bukkit.getPlayerExact(a[1]);
    		
    		if(tar == null){
    			msg(p, RED + "Player must be online!");
    			return true;
    		}
    		
    		if(!gm.isLeaderOfAnyGuild(p)){
    			msg(p, RED + "Only leaders can perform this command for your guild!");
    			return true;
    		}
    		
    		if(gm.getGuild(p) == null){
    			msg(p, RED + "You do not have a guild!");
    			return true;
    		}
    		
    		if(a[1].equalsIgnoreCase(p.getName())){
    			msg(p, RED + "Cannot ban yourself!");
    			return true;
    		}
    		
    		if(!gm.getGuild(p).getMembers(false).contains(tar.getUniqueId())){
    			msg(p, RED + "Player must be from your guild!");
    			return true;
    		}
    		
    		if(gm.getGuild(p).getMembers(false).contains(tar.getUniqueId())){
    			gm.getGuild(p).removeMember(tar);
    		}
    		
    		if(gm.getGuild(p).isBanned(tar)){
    			msg(p, RED + "Player is already banned!");
    			return true;
    		}
    		
    		gm.getGuild(p).banMember(tar);
    		
    		msg(tar, RED + "You have been banned from " + gm.getGuild(p).getName());
    		
    		for(UUID u : gm.getGuild(p).getMembers(true)){
    			if(Bukkit.getPlayer(u) == null) continue;
    			msg(Bukkit.getPlayer(u), YELLOW + tar.getName() + GREEN + " has been banned from your guild!");
    		}
    	}
    	
    	if(a[0].equalsIgnoreCase("unban")){
    		
    		if(!p.hasPermission("guild.unban")){
    			msg(p, RED + "No permission");
    			return true;
    		}
    		
    		if(a.length < 2){
    			msg(p, RED + "Specify player to unban");
    			return true;
    		}
    		
    		Player tar = Bukkit.getPlayerExact(a[1]);
    		
    		if(tar == null){
    			msg(p, RED + "Player must be online!");
    			return true;
    		}
    		
    		if(a[1].equalsIgnoreCase(p.getName())){
    			msg(p, RED + "You are not banned!");
    			return true;
    		}
    		
    		if(!gm.getGuild(p).isBanned(tar)){
    			msg(p, RED + "That player is not banned from your guild!");
    			return true;
    		}
    		
    		gm.getGuild(p).removeBanMember(tar);
    		
    		msg(tar, GREEN + "You have been unbanned from " + YELLOW + gm.getGuild(p).getName());
    		
    		for(UUID u : gm.getGuild(p).getMembers(true)){
    			if(Bukkit.getPlayer(u) == null) continue;
    			msg(Bukkit.getPlayer(u), YELLOW + tar.getName() + GREEN + " has been unbanned from your guild.");
    		}
    		
    		return true;
    	}
    	
    	if(a[0].equalsIgnoreCase("leave")){
    		
    		if(!p.hasPermission("guild.leave")){
    			msg(p, RED + "No permission");
    			return true;
    		}
    		
    		if(!gm.playerIsInGuild(p)){
    			msg(p, RED + "You are not in a guild currently!");
    			return true;
    		}
    		
    		if(gm.getGuild(p) != null){
    			p.performCommand("guild disband");
    			return true;
    		}
    		
    		Guild g = gm.getPartOfGuild(p);
    		
    		msg(p, YELLOW + "You have left " + g.getName());
    		
    		g.removeMember(p);
    		
    		for(UUID u : g.getMembers(true)){
    			if(Bukkit.getPlayer(u) == null) continue;
    			msg(Bukkit.getPlayer(u), YELLOW + p.getName() + GREEN + " has left your guild!");
    		}
    	}
    	
    	if(a[0].equalsIgnoreCase("disband")){

            if(a.length >=2){
                msg(p, RED + "Too many arguements!");
                return true;
            }
    		if(!p.hasPermission("guild.disband")){
    			msg(p, RED + "No permission");
    			return true;
    		}




            if(gm.getGuild(p) == null){
    			msg(p, RED + "You do not have a guild!");
    			return true;
    		}
    		
    		for(UUID u : gm.getGuild(p).getMembers(false)){
    			if(Bukkit.getPlayer(u) == null) continue;
    			
    			msg(Bukkit.getPlayer(u), GREEN + "Your guild has been disbanded!");
    		}

            if(gm.isLeaderOfAnyGuild(p)) {

                for(Hologram hologram: HoloAPI.getManager().getHologramsFor(plugin)){
                    for(String da : hologram.getLines()){
                        if(da.equalsIgnoreCase(ChatColor.AQUA + "Guild Portal: " + ChatColor.YELLOW + gm.getGuild(p).getName())){
                            HoloAPI.getManager().stopTracking(hologram);

                        }
                    }

                }


                p.sendMessage(ChatColor.GREEN + "Removed your portal!");
                for (int x = -20; x <= 20; x++) {
                    for (int y = -20; y <= 20; y++) {
                        for (int z = -20; z <= 20; z++) {
                            double xx = 1 + plugin.getConfig().getDouble("guilds." + gm.getGuild(p).getName()+ ".x"  );
                            double yy = 1 +plugin.getConfig().getDouble("guilds." + gm.getGuild(p).getName() + ".y" );
                            double zz = 1 + plugin.getConfig().getDouble("guilds." + gm.getGuild(p).getName() + ".z" );
                            Location loc = new Location(p.getWorld(), xx, yy, zz);
                            if(p.getWorld().getBlockAt(loc).getRelative(x, y, z).getType() == Material.OBSIDIAN && (x != 0 || y != 0 || z != 0)) {
                                p.getWorld().getBlockAt(loc).getRelative(x, y, z).setType(Material.AIR);


                            }

                        }
                    }
                }

                if (plugin.getConfig().get("guilds." + gm.getGuild(p).getName()) != null) {
                    plugin.getConfig().set("guilds." + gm.getGuild(p).getName(), null);


                    plugin.saveConfig();

                    gm.deleteGuild(p);

                    p.sendMessage(GREEN + "Deleted your guild!");

                    gm.save();
                }

                return true;

            }
    		gm.deleteGuild(p);
    		
    		p.sendMessage(GREEN + "Deleted your guild!");


    		return true;
    	}
    	
    	return true;
    }
    
    
    /* public boolean INVALID_onCommand(CommandSender cs, Command cmd, String label, String[] args)
    {
        Player p = (Player)cs;
        if (!(cs instanceof Player)) {
            return true;
        }

        if (label.equalsIgnoreCase("guild"))
        {
            if (args.length == 0)
            {
                p.sendMessage(ChatColor.RED + "Specify another command!");

                return true;
            }
            if (args[0].equalsIgnoreCase("create"))
            {
                if (!p.hasPermission("guilds.create"))
                {
                    p.sendMessage(ChatColor.RED + "No permission!");
                    return true;
                }
                if (args.length < 2)
                {
                    p.sendMessage(ChatColor.RED + "Specify the guild name!");
                    return true;
                }
                if (args.length >= 3)
                {
                    p.sendMessage(ChatColor.RED + "The Guild name can only be one word!");
                    return true;
                }
                if ((args[1].equalsIgnoreCase("None")) || (args[1].contains(",")) || (args[1].contains(".")) || (args[1].contains("None")) || (args[1].contains("none")))
                {
                    p.sendMessage(ChatColor.RED + "Not valid!");
                    return true;
                }
                try
                {
                    if (this.ga.getAllGuilds().contains(args[1]))
                    {
                        p.sendMessage(ChatColor.RED + "Already a guild with that name!");
                        return true;
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    if (!this.ga.getCurrentGuild(p.getName()).equalsIgnoreCase("None"))
                    {
                        p.sendMessage(ChatColor.RED + "You currently have a guild say yes or no in chat to override your current guild. Members apart of it will be lost!");
                        this.plugin.getC().put(p, args[1]);
                        this.plugin.getConfirmers().add(p);
                        return true;
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                p.sendMessage(ChatColor.AQUA + "Made the guild:" + ChatColor.YELLOW + " " + args[1]);
                try
                {
                    this.ga.createGuildUpdate(p.getName(), args[1]);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("invite"))
            {
                if (!p.hasPermission("guilds.invite"))
                {
                    p.sendMessage(ChatColor.RED + "No permission!");
                    return true;
                }
                if (args.length < 2)
                {
                    p.sendMessage(ChatColor.RED + "Specify a player to invite!");
                    return true;
                }
                if ((args[1].equalsIgnoreCase("None")) || (args[1].equalsIgnoreCase(",")) || (args[1].equalsIgnoreCase(".")))
                {
                    p.sendMessage(ChatColor.RED + "Not valid!");
                    return true;
                }
                if (args[1].equalsIgnoreCase(p.getName()))
                {
                    p.sendMessage(ChatColor.RED + "You cannot invite yourself!");
                    return true;
                }

                try
                {
                    if (this.ga.getCurrentGuild(args[1]).equalsIgnoreCase(this.ga.getCurrentGuild(p.getName())))
                    {
                        p.sendMessage(ChatColor.RED + "Player already in your guild!");
                        return true;
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    ArrayList<String> wordArrayList2 = new ArrayList();
                    for (String word : this.ga.getGuildInvites(args[1]).split(" ")) {
                        wordArrayList2.add(word);
                    }
                    if (wordArrayList2.contains(this.ga.getCurrentGuild(p.getName())))
                    {
                        p.sendMessage(ChatColor.RED + "You already invited that player!");
                        return true;
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    if (this.ga.getCurrentGuild(p.getName()).equalsIgnoreCase("None"))
                    {
                        p.sendMessage(ChatColor.RED + "You have no guild!");
                        return true;
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    if (this.ga.playerthere(args[1]).equalsIgnoreCase("false"))
                    {
                        p.sendMessage(ChatColor.RED + "Invalid Player! Player Name is CASE_SENSITIVE");
                        return true;
                    }
                    p.sendMessage(ChatColor.GREEN + "Player Name is CASE_SENSITIVE- Invited:" + ChatColor.YELLOW + " " + args[1]);
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        if (pl.getName().equalsIgnoreCase(args[1])) {
                            pl.sendMessage(ChatColor.GREEN + "You have been invited by the guild:" + ChatColor.YELLOW + " " + this.ga.getCurrentGuild(p.getName()));
                        }
                    }
                    if (this.ga.getGuildInvites(args[1]).equalsIgnoreCase("None"))
                    {
                        this.ga.fixGuildInvite(args[1]);
                        this.ga.fixaddGuildInvite(args[1], this.ga.getCurrentGuild(p.getName()));
                        return true;
                    }
                    this.ga.addGuildInvite(args[1], this.ga.getCurrentGuild(p.getName()));
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
            }
            if (args[0].equalsIgnoreCase("accept"))
            {
                if (!p.hasPermission("guilds.accept"))
                {
                    p.sendMessage(ChatColor.RED + "No permission!");
                    return true;
                }
                if (args.length < 2)
                {
                    p.sendMessage(ChatColor.RED + "Specify a guild to accept!");
                    return true;
                }
                if ((args[1].equalsIgnoreCase("None")) || (args[1].equalsIgnoreCase(",")) || (args[1].equalsIgnoreCase(".")))
                {
                    p.sendMessage(ChatColor.RED + "Not valid!");
                    return true;
                }
                if (args[1].equalsIgnoreCase(p.getName()))
                {
                    p.sendMessage(ChatColor.RED + "You cannot accept yourself!");
                    return true;
                }
                ArrayList<String> wordArrayList1 = new ArrayList();
                try
                {
                    for (String word : this.ga.getGuildInvites(p.getName()).split(" ")) {
                        wordArrayList1.add(word);
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                if (!wordArrayList1.contains(args[1]))
                {
                    p.sendMessage(ChatColor.RED + "That guild did not invite you! Guild name CASE_SENSITIVE");
                    return true;
                }
                try
                {
                    if (this.ga.isplayerleader(p.getName()).equalsIgnoreCase("true"))
                    {
                        p.sendMessage(ChatColor.RED + "You were the leader of this guild! All other members were kicked out since you left the guild!");
                        this.ga.clearguildinvites(p.getName());
                        p.sendMessage(ChatColor.GREEN + "Successfully joined the guild " + ChatColor.YELLOW + args[1]);
                        this.ga.clearleaderandguild(p.getName());

                        this.ga.createGuildUpdate(p.getName(), args[1], this.ga.getGuildleaderguild(args[1]));
                        return true;
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    if (this.ga.getCurrentGuild(p.getName()).contains(args[1]))
                    {
                        p.sendMessage(ChatColor.RED + "Already in that guild! Guild name CASE_SENSITIVE");
                        return true;
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                p.sendMessage(ChatColor.GREEN + "Successfully joined the guild " + ChatColor.YELLOW + args[1]);
                try
                {
                    this.ga.clearguildinvites(p.getName());
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    this.ga.createGuildUpdate(p.getName(), args[1], this.ga.getGuildleaderguild(args[1]));
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
            }
            if (args[0].equalsIgnoreCase("kick"))
            {
                if (!p.hasPermission("guilds.kick"))
                {
                    p.sendMessage(ChatColor.RED + "No permission!");
                    return true;
                }
                if (args.length < 2)
                {
                    p.sendMessage(ChatColor.RED + "Specify a player to kick!");
                    return true;
                }
                if ((args[1].equalsIgnoreCase("None")) || (args[1].equalsIgnoreCase(",")) || (args[1].equalsIgnoreCase(".")))
                {
                    p.sendMessage(ChatColor.RED + "Not valid!");
                    return true;
                }
                if (args[1].equalsIgnoreCase(p.getName()))
                {
                    p.sendMessage(ChatColor.RED + "You cannot kick yourself!");
                    return true;
                }
                try
                {
                    if (this.ga.getCurrentGuild(p.getName()).equalsIgnoreCase("None"))
                    {
                        p.sendMessage(ChatColor.RED + "You have no guild!");
                        return true;
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    if (this.ga.isplayerleader(args[1]).equalsIgnoreCase("true"))
                    {
                        p.sendMessage(ChatColor.RED + "That player is the leader!");
                        return true;
                    }
                    if (this.ga.getCurrentGuild(args[1]).equalsIgnoreCase(this.ga.getCurrentGuild(p.getName())))
                    {
                        p.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.GREEN + " has been kicked!");
                        this.ga.clearguildinvites(args[1]);
                        this.ga.clearleaderandguildfromusername(args[1]);


                        return true;
                    }
                    if (!this.ga.getCurrentGuild(args[1]).equalsIgnoreCase(this.ga.getCurrentGuild(p.getName())))
                    {
                        p.sendMessage(ChatColor.RED + "Player not in your guild!");


                        return true;
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
            }
            if (args[0].equalsIgnoreCase("ban"))
            {












                /////////////

                if (!p.hasPermission("guilds.ban"))
                {
                    p.sendMessage(ChatColor.RED + "No permission!");
                    return true;
                }
                if (args.length < 2)
                {
                    p.sendMessage(ChatColor.RED + "Specify a player to ban!");
                    return true;
                }
                if ((args[1].equalsIgnoreCase("None")) || (args[1].equalsIgnoreCase(",")) || (args[1].equalsIgnoreCase(".")))
                {
                    p.sendMessage(ChatColor.RED + "Not valid!");
                    return true;
                }
                if (args[1].equalsIgnoreCase(p.getName()))
                {
                    p.sendMessage(ChatColor.RED + "You cannot ban yourself!");
                    return true;
                }
                try
                {
                    if (this.ga.getCurrentGuild(p.getName()).equalsIgnoreCase("None"))
                    {
                        p.sendMessage(ChatColor.RED + "You have no guild!");
                        return true;
                    }
                    if (!this.ga.getCurrentGuild(args[1]).equalsIgnoreCase(this.ga.getCurrentGuild(p.getName())))
                    {
                        p.sendMessage(ChatColor.RED + "Player not in your guild!");


                        return true;
                    }
                }

                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                try
                {

                    if (this.ga.getCurrentGuild(args[1]).equalsIgnoreCase(this.ga.getCurrentGuild(p.getName())))
                    {
                        p.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.GREEN + " has been banned!");
                        if (this.ga.getGuildBannes(args[1]).equalsIgnoreCase("None"))
                        {
                            this.ga.fixGuildBan(args[1]);
                            this.ga.fixaddGuildBannes(args[1], this.ga.getCurrentGuild(p.getName()));
                            this.ga.clearguildinvites(args[1]);
                            this.ga.clearleaderandguildfromusername(args[1]);
                            return true;
                        }
                        this.ga.addGuildBan(args[1], this.ga.getCurrentGuild(p.getName()));
                        this.ga.clearguildinvites(args[1]);
                        this.ga.clearleaderandguildfromusername(args[1]);





                        return true;
                    }

                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                return true;
            }
            else{
                p.sendMessage(ChatColor.RED + "Unknown command!");
                return true;
            }
        }
        return true;
    } */
}
