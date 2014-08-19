package me.ritzdever.Guilds;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.HoloManager;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;


import me.ritzdever.Guilds.commands.CommandGuild;
import me.ritzdever.Guilds.commands.MySQLGuilds;
import me.ritzdever.Guilds.mechanics.GuildInviteManager;
import me.ritzdever.Guilds.mechanics.GuildManager;
import me.ritzdever.Guilds.schematics.CastleAPI;
import me.ritzdever.Guilds.schematics.Schematic;
import net.minecraft.server.v1_7_R3.BlockObsidian;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.party.HeroParty;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Guilds extends JavaPlugin implements Listener, CommandExecutor {
    HashMap<Player, String> gchat;
    ArrayList<Player> confirmers;
    ArrayList<String> agl;
    HashMap<Player, String> c;
    int localradius;
    HashMap<Player, String> tb;
    HashMap<Player, Integer> tbc;
    HashMap<Player, Boolean> enderfirst;
    String fullMsg;
    ArrayList<Player> epeople;
    Heroes h = Heroes.getPlugin(Heroes.class);
    Set<Hero> partyMembers;
    MySQLGuilds g;
    GuildManager gm;
    ArrayList<Player> left;
    HashMap<Player, String> cpen;
    ArrayList<Player> freeze;
    CastleAPI castleAPI;

    @Override
    public void onEnable() {

     castleAPI = new CastleAPI(this);
        castleAPI.createWorldIfNotexists();
     cpen = new HashMap<Player, String>();
        freeze = new ArrayList<Player>();
    	saveDefaultConfig();
        left = new ArrayList<Player>();
        epeople= new ArrayList<Player>();
        gchat = new HashMap<Player, String>();
        tbc = new HashMap<Player, Integer>();
        tb = new HashMap<Player, String>();
        this.setupFiles();
        c = new HashMap<Player, String>();
        Bukkit.getPluginManager().registerEvents(this, this);
        confirmers = new ArrayList<Player>();
        agl = new ArrayList<String>();
        
        g = new MySQLGuilds(getConfig().getString("database.ip"), getConfig().getString("database.user"), getConfig().getString("database.pass"));
        gm = new GuildManager(this);
        GuildInviteManager.INSTANCE.init(gm);
        getCommand("guild").setExecutor(new CommandGuild(this));













    }
    @Override
    public void onDisable() {
    	gm.save();




    }


    public void setupFiles() {
        File pluginFolder = this.getDataFolder();
        File worldsFolder = new File(pluginFolder, "Schematics");
        if (!worldsFolder.exists()) {
            worldsFolder.mkdir();
        }
    }
    public HoloManager myCoolFunction() {
        return HoloAPI.getManager();

    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        if (freeze.contains(evt.getPlayer().getName())) {
            Location back = new Location(evt.getFrom().getWorld(), evt.getFrom().getX(), evt.getFrom().getY(), evt.getFrom().getZ());
            evt.getPlayer().teleport(back);
        }
    }



@EventHandler
public void onQuite(PlayerQuitEvent event) {
    if (epeople.contains(event.getPlayer())) {
        Bukkit.broadcastMessage("quite");
        if(event.getPlayer().getInventory().contains(Material.EYE_OF_ENDER)){
            event.getPlayer().getInventory().remove(Material.EYE_OF_ENDER);
        }

        epeople.remove(event.getPlayer());

        left.add(event.getPlayer());
        event.getPlayer().performCommand("guild disband");
        Bukkit.getScheduler().cancelTask(CommandGuild.TaskID);
        return;
    }
}

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getPlayer().getItemInHand().getType() != Material.EYE_OF_ENDER){
            return;
        }
        if(epeople.contains(event.getPlayer())){
                if(event.getAction() == Action.RIGHT_CLICK_BLOCK ||event.getAction() == Action.LEFT_CLICK_BLOCK){








                    if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Portal Maker-" + ChatColor.YELLOW + event.getPlayer().getName())){

                        for (int x = -25; x <= 25; x++) {
                            for (int y = -25; y <= 25; y++) {
                                for (int z = -25; z <= 25; z++) {
                                    if(event.getPlayer().getWorld().getBlockAt(event.getPlayer().getLocation()).getRelative(x, y, z).getType() == Material.OBSIDIAN && (x != 0 || y != 0 || z != 0)) {

                                        event.getPlayer().sendMessage(ChatColor.RED + "Another portal detected. Choose another location!");
                                        event.setCancelled(true);
                                        return;
                                    }
                                }
                            }
                        }





                            event.getPlayer().sendMessage(ChatColor.GREEN + "Portal Set!");


//Check if another portal is there




                        Schematic schematic = null;
                        try {
                            schematic = Schematic.loadSchematic(new File(getDataFolder(), "portal.schematic"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        getConfig().set("guilds." + gm.getGuild(event.getPlayer()).getName() + ".x", event.getPlayer().getLocation().getX());
                        getConfig().set("guilds." +gm.getGuild(event.getPlayer()).getName() + ".y", event.getPlayer().getLocation().getY());
                        getConfig().set("guilds." +gm.getGuild(event.getPlayer()).getName()+ ".z", event.getPlayer().getLocation().getZ());
                        saveConfig();


                        Schematic.pasteSchematic(event.getPlayer().getWorld(), event.getPlayer().getLocation(), schematic);

                        Hologram hologram = new HologramFactory(this)    // Replace "myPlugin" with your plugin instance
                                .withLocation(new org.bukkit.util.Vector(event.getPlayer().getLocation().getX(),event.getPlayer().getLocation().getY()+5,event.getPlayer().getLocation().getZ()), event.getPlayer().getWorld().getName())
                                .withText(ChatColor.AQUA + "Guild Portal: " + ChatColor.YELLOW + gm.getGuild(event.getPlayer()).getName())
                                .build();


                    event.getPlayer().getInventory().remove(Material.EYE_OF_ENDER);
                        Bukkit.getScheduler().cancelTask(CommandGuild.TaskID);
                        epeople.remove(event.getPlayer());
                        return;
                    }
                }
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws ExecutionException, InterruptedException {
        final Player p = event.getPlayer();

        tb.put(p, "Local");
        p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
        g.newPlayer(p);
    }
    /* @EventHandler
    @Deprecated
    public void onSpeak(AsyncPlayerChatEvent event){
        Player p = event.getPlayer();
        String m = event.getMessage();
        if(confirmers.contains(p)) {
            if (!m.equalsIgnoreCase("No") && !m.equalsIgnoreCase("Yes")) {
                p.sendMessage(ChatColor.RED + "You need to confirm, say yes or no!");
                event.setCancelled(true);
                return;
            }
            if (m.equalsIgnoreCase("No")) {
                p.sendMessage(ChatColor.GREEN + "Guild not made!");
                event.setCancelled(true);
                confirmers.remove(p);
                return;
            }
            if (m.equalsIgnoreCase("yes")) {
                p.sendMessage(ChatColor.GREEN + "Making guild:" + ChatColor.YELLOW + " " + c.get(p));
                event.setCancelled(true);
                confirmers.remove(p);
                return;
            }
            return;
        }
        if(tb.get(p).equalsIgnoreCase("None")){
            for (Player players : Bukkit.getOnlinePlayers()) {
                Player player = event.getPlayer();
                if (player.getWorld().equals(players.getWorld())) {
                    if (player.getLocation().distanceSquared(players.getLocation()) < 5*5) {
        for(String list : getConfig().getStringList("admins")){
            for(String mlist : getConfig().getStringList("mods")) {
                if (list.contains(p.getName())) {
                    fullMsg = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "LOCAL> " + ChatColor.RED + p.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + event.getMessage();
                }
                if (mlist.contains(p.getName())) {
                    fullMsg = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "LOCAL> " + ChatColor.DARK_PURPLE + p.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + event.getMessage();
                } else {
                    fullMsg = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "LOCAL> " + ChatColor.YELLOW + p.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + event.getMessage();
                }
            }
        }
                        players.sendMessage(fullMsg);
                        event.setCancelled(true);
                    }

                }
            }
            return;
        }
        if(tb.get(p).equalsIgnoreCase("Local") || tb.get(p).equalsIgnoreCase("not")){
            for (Player players : Bukkit.getOnlinePlayers()) {
                Player player = event.getPlayer();
                if (player.getWorld().equals(players.getWorld())) {
                    if (player.getLocation().distanceSquared(players.getLocation()) < 5*5) {
                        for(String list : getConfig().getStringList("admins")){
                            for(String mlist : getConfig().getStringList("mods")) {
                                if (list.contains(p.getName())) {
                                    fullMsg = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "LOCAL> " + ChatColor.RED + p.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + event.getMessage();
                                }
                                if (mlist.contains(p.getName())) {
                                    fullMsg = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "LOCAL> " + ChatColor.DARK_PURPLE + p.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + event.getMessage();
                                } else {
                                    fullMsg = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "LOCAL> " + ChatColor.YELLOW + p.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + event.getMessage();
                                }
                            }
                        }
                        players.sendMessage(fullMsg);
                        event.setCancelled(true);
                    }
                }
           }
            return;
        }
        if(tb.get(p).equalsIgnoreCase("Global")){
            for(Player ph : Bukkit.getOnlinePlayers()){
                if(getConfig().getStringList("admins").contains(p.getName())){
                    fullMsg = ChatColor.GREEN + "" + ChatColor.BOLD + "GLOBAL> " +  ChatColor.RED + p.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + event.getMessage();
                }
                if(getConfig().getStringList("mods").contains(p.getName())){
                    fullMsg = ChatColor.GREEN + "" + ChatColor.BOLD + "GLOBAL> " +  ChatColor.DARK_PURPLE + p.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + event.getMessage();
                }
                else{
                    fullMsg = ChatColor.GREEN + "" + ChatColor.BOLD + "GLOBAL> " + ChatColor.YELLOW + p.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + event.getMessage();
                }
                ph.sendMessage(fullMsg);
                event.setCancelled(true);
            }
            return;
        }
        if(tb.get(p).equalsIgnoreCase("Party")) {
            Hero hero = h.getCharacterManager().getHero(p);
            HeroParty party = hero.getParty();
            if(party != null){
                partyMembers = party.getMembers();
            }
            else{
                partyMembers = null;
                p.sendMessage(ChatColor.RED + "You are not in a party.");
                event.setCancelled(true);
                tb.remove(p);
                tb.put(p, "not");
               return;
            }
            if (party == null)
            {
                p.sendMessage(ChatColor.RED + "You are not in a party.");
                event.setCancelled(true);
                tb.remove(p);
                tb.put(p, "not");
                return;
            }
            if (partyMembers.size() <= 1)
                {
                p.sendMessage(ChatColor.RED + "Your party is empty.");
                event.setCancelled(true);
                tb.remove(p);
                tb.put(p, "not");
                return;
            }

            else{
                fullMsg = ChatColor.BLUE + "" + ChatColor.BOLD + "PARTY> " + ChatColor.YELLOW + p.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + event.getMessage();
            }
            for (Hero partyMember : partyMembers) {
                partyMember.getPlayer().sendMessage(fullMsg);
                event.setCancelled(true);
            }
            return;
        }
        /* if(tb.get(p).equalsIgnoreCase("Guild")){
            for(Player pss : Bukkit.getOnlinePlayers()){
                try {
                    if(ga.getCurrentGuild(pss.getName()).equalsIgnoreCase(ga.getCurrentGuild(p.getName()))){
                        if(getConfig().getStringList("admins").contains(p.getName())){
                            fullMsg = ChatColor.GOLD + "" + ChatColor.BOLD + "GUILD> " +  ChatColor.RED + p.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + event.getMessage();
                        }
                        if(getConfig().getStringList("mods").contains(p.getName())){
                            fullMsg = ChatColor.GOLD + "" + ChatColor.BOLD + "GUILD> " +  ChatColor.DARK_PURPLE + p.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + event.getMessage();
                        }
                        else{
                            fullMsg = ChatColor.GOLD + "" + ChatColor.BOLD + "GUILD> " + ChatColor.YELLOW + p.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + event.getMessage();
                        }
                        pss.sendMessage(fullMsg);
                        event.setCancelled(true);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } */
    @EventHandler
    public void onCommand(ServerCommandEvent e){
    	if(e.getCommand().equalsIgnoreCase("/stop") || e.getCommand().equalsIgnoreCase("stop")){
    		gm.save();
    	}
    	if(e.getCommand().equalsIgnoreCase("/reload") || e.getCommand().equalsIgnoreCase("reload")){
    		gm.save();
    	}
    	if(e.getCommand().equalsIgnoreCase("/rl") || e.getCommand().equalsIgnoreCase("rl")){
    		gm.save();
    	}
    }
    
    @EventHandler
    public void onpCommand(PlayerCommandPreprocessEvent e){
    	if(e.getMessage().equalsIgnoreCase("/stop")){
    		e.setCancelled(true);
    		
    		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
    	}
    	if(e.getMessage().equalsIgnoreCase("/reload")){
    		e.setCancelled(true);
    		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload");
    	}
    	if(e.getMessage().equalsIgnoreCase("/rl")){
    		e.setCancelled(true);
    		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rl");
    	}
    }
    @EventHandler
    public void onChatTab(PlayerChatTabCompleteEvent event){
        final Player p = event.getPlayer();
        final String s =  event.getChatMessage();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!confirmers.contains(p)) {
                    if (tb.get(p).equalsIgnoreCase("None")) {//Global
                        p.sendMessage(ChatColor.AQUA + "Chat: " + ChatColor.GREEN + "" + ChatColor.BOLD  + "Global ");
                        p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                        tb.remove(p);
                        tb.put(p, "Global");
                        return;
                    }
                    if (tb.get(p).equalsIgnoreCase("Global")) {//Local
                        Hero hero = h.getCharacterManager().getHero(p);
                        HeroParty party = hero.getParty();
                        if (party == null)
                        {
                            p.sendMessage(ChatColor.RED + "You are not in a party.");
                            p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                            tb.remove(p);
                            tb.put(p, "not");
                            return;
                        }
                        Set<Hero> partyMembers = party.getMembers();
                        if(  partyMembers == null){
                            p.sendMessage(ChatColor.RED + "Your party is empty.");
                            p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                            tb.remove(p);
                            tb.put(p, "not");
                            return;
                        }
                        if (partyMembers.size() <= 1)
                        {
                            p.sendMessage(ChatColor.RED + "Your party is empty.");
                            p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                            tb.remove(p);
                            tb.put(p, "not");
                            return;
                        }
                        p.sendMessage(ChatColor.AQUA + "Chat: " + ChatColor.BLUE + "" + ChatColor.BOLD + "Party ");
                        p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                        tb.remove(p);
                        tb.put(p, "Party");
                        return;
                    }
                    if (tb.get(p).equalsIgnoreCase("Party")) {
                        p.sendMessage(ChatColor.AQUA + "Chat: "  + ChatColor.BOLD + "" + ChatColor.DARK_GRAY + "Local");
                        p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                        tb.remove(p);
                        tb.put(p, "Local");
                        return;
                    }
                    if (tb.get(p).equalsIgnoreCase("not")) {
                        p.sendMessage(ChatColor.AQUA + "Chat: " + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Local");
                        p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                        tb.remove(p);
                        tb.put(p, "Local");
                        return;
                    }
                    /* if (tb.get(p).equalsIgnoreCase("Local")) {
                        try {
                            if (ga.getCurrentGuild(p.getName()).equalsIgnoreCase("None")) {
                                p.sendMessage(ChatColor.RED + "No Guild Chat ");
                                p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                                tb.remove(p);
                                tb.put(p, "None");
                                return;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        try {
                            p.sendMessage((ChatColor.AQUA + "Chat: " + ChatColor.GOLD + "" + ChatColor.BOLD + "Guild(" + ChatColor.AQUA + ga.getCurrentGuild(p.getName()) + ChatColor.YELLOW + ")"));
                            p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        tb.remove(p);
                        tb.put(p, "Guild");
                        return;
                    } */
                    if (tb.get(p).equalsIgnoreCase("Guild")) {
                        p.sendMessage(ChatColor.AQUA + "Chat: " + ChatColor.GREEN + "" + ChatColor.BOLD + "Global");
                        p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                        tb.remove(p);
                        tb.put(p, "Global");
                        return;
                    }

                }
            }
        }).start();
    }
    public void setConfirmers(ArrayList<Player> confirmers) {
        this.confirmers = confirmers;
    }
    public ArrayList<Player> getConfirmers() {
        return confirmers;
    }
    public void setC(HashMap<Player, String> c) {
        this.c = c;
    }
    public HashMap<Player, String> getC() {
        return c;
    }
    public void setAgl(ArrayList<String> agl) {
        this.agl = agl;
    }
    public ArrayList<String> getAgl() {
        return agl;
    }
    public MySQLGuilds getMySQLGuilds(){
    	return g;
    }
    public GuildManager getGuildManager(){
    	return gm;
    }

    public void setEpeople(ArrayList<Player> epeople) {
        this.epeople = epeople;
    }

    public ArrayList<Player> getEpeople() {
        return epeople;
    }

    public void setCpen(HashMap<Player, String> cpen) {
        this.cpen = cpen;
    }

    public HashMap<Player, String> getCpen() {
        return cpen;
    }

    public void setFreeze(ArrayList<Player> freeze) {
        this.freeze = freeze;
    }

    public ArrayList<Player> getFreeze() {
        return freeze;
    }

    public CastleAPI getCastleAPI() {
        return castleAPI;
    }
}