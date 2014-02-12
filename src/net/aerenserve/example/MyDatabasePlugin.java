package net.aerenserve.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.aerenserve.minesql.MineSQL;
import net.aerenserve.minesql.Table;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MyDatabasePlugin extends JavaPlugin implements Listener {
	
	MineSQL myDatabase;
	Table myTable;
	
	@Override
	public void onEnable() {
		
		//Register your events
		getServer().getPluginManager().registerEvents(this, this);
		
		//Get the login information
		String host = getConfig().getString("database.ip");
		String port = getConfig().getString("database.port");
		String database = getConfig().getString("database.dbname");
		String user = getConfig().getString("database.user");
		String pass = getConfig().getString("database.pass");

		//Make a new MineSQL
		myDatabase = new MineSQL(this, host, port, database, user, pass);
				
		//Make a HashMap so we can create a custom table.
		HashMap<String, String> columns = new HashMap<String, String>();
		columns.put("username", "text");
		columns.put("firstjoined", "text");
		
		//Make a new Table
		myTable = new Table(myDatabase, "exampleTable", columns);
	}
	
	@Override
	public void onDisable() {
		myDatabase.closeConnection();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(!myTable.contains("username", e.getPlayer().getName())) { //Make sure that the player is not in the database
			String[] columns = {"username", "firstjoined"};
			String[] values = {e.getPlayer().getName(), String.format("" + e.getPlayer().getFirstPlayed())};
			myTable.insert(columns, values);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("listusers")) {
			ArrayList<HashMap<String, String>> table = myTable.openTable();
			for(HashMap<String, String> hm : table) {
				Iterator<Entry<String, String>> it = hm.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry<String, String> entry = (Entry<String, String>) it.next();
			    	sender.sendMessage(entry.getKey() + " : " + entry.getValue());
			    }
			}
		}
		return false;
	}

}
