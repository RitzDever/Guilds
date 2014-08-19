package me.ritzdever.Guilds.commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


import me.ritzdever.Guilds.mechanics.Guild;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MySQLGuilds {
	private Connection conn;
	private String user;
	private String pass;
	private String ip;
	
	public static void main(String args[]) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		
		Connection c = DriverManager.getConnection("jdbc:mysql://66.85.150.13" + "/test_12310", "test_12310", "member445");
		
		if(!c.isClosed()){
			System.out.print(false);
		}
		
		
	}
	
	public MySQLGuilds(String ip, String user, String pass){
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		
		this.ip = ip;
		this.user = user;
		this.pass = pass;
		
		Bukkit.getLogger().info("Connecting to database");
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + ip, user, pass);
			Statement s = conn.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS Player_Data(uuid TEXT, CurrentGuild TEXT)";
			s.executeUpdate(sql);
			String $sql = "CREATE TABLE IF NOT EXISTS Guilds(name TEXT, serialized TEXT)";
			s.executeUpdate($sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void newPlayer(final Player p){
		new Thread(){
			public void run(){
				try{
					PreparedStatement s = conn.prepareStatement("SELECT count(*) FROM `Player_Data` WHERE uuid=?");
					s.setString(1, p.getUniqueId().toString());
					ResultSet rs = s.executeQuery();
					if(rs.next()){
						if(!(rs.getInt(1) > 0)){
							PreparedStatement ps = conn.prepareStatement("INSERT INTO `Player_Data` (uuid) VALUES(?)");
							ps.setString(1, p.getUniqueId().toString());
							ps.executeUpdate();
						}else{
							return;
						}
					}
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void updatePlayerGuild(final Player p, final String g){
		new Thread(){
			public void run(){
				try{
					PreparedStatement s = conn.prepareStatement("UPDATE `Player_Data` SET CurrentGuild=? WHERE uuid=?");
					s.setString(1, g);
					s.setString(2, p.getUniqueId().toString());
					s.executeUpdate();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void createGuild(final Guild g){
		final String ser = g.serialize();
		new Thread(){
			public void run(){
				try{
					PreparedStatement s = conn.prepareStatement("INSERT INTO `Guilds` (name) VALUES(?)");
					s.setString(1, g.getName());
					s.executeUpdate();
					PreparedStatement $s = conn.prepareStatement("UPDATE `Guilds` SET serialized=? WHERE name=?");
					$s.setString(1, ser);
					$s.setString(2, g.getName());
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void updateGuild(final Guild g){
		final String ser = g.serialize();
		
		new Thread(){
			public void run(){
				try{
					PreparedStatement s = conn.prepareStatement("SELECT count(*) FROM `Guilds` WHERE name=?");
					s.setString(1, g.getName());
					
					ResultSet rs = s.executeQuery();
					
					if(rs.next()){
						if(!(rs.getInt(1) > 0)){
							createGuild(g); 
							return;
						}
					}
					
					conn.createStatement().executeUpdate("UPDATE `Guilds` SET serialized='" + g.serialize() + "' " + "WHERE name=" + "'" + g.getName() + "'");
					
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void deleteGuild(final Guild g){
		
		new Thread(){
			public void run(){
				try{
					PreparedStatement s = conn.prepareStatement("DELETE FROM `Guilds` WHERE name=?");
					
					s.setString(1, g.getName());
					s.executeUpdate();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public Collection<String> getAllGuilds(){
		final Collection<String> str = Collections.synchronizedCollection(new ArrayList<String>());
		
		try{
			Thread  t = new Thread(){
				public void run(){
					try{
						Statement s = conn.createStatement();
						String sql = "SELECT serialized FROM `Guilds`";
						ResultSet rs = s.executeQuery(sql);
						
						while(rs.next()){
							str.add(rs.getString(rs.findColumn("serialized")));
						}
					}catch(SQLException e){
						e.printStackTrace();
					}
				}
			};
			t.start();
			t.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		
		Bukkit.getLogger().info(str.toString());
		
		return str;
	}
	
	public void safeClose(){
		if(conn != null){
			try{
				conn.close();
			}catch(SQLException e){
				e.printStackTrace(System.err);
			}
			
			conn = null;
		}
	}
}
