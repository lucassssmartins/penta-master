package br.com.pentamc.bukkit.listener;

import br.com.pentamc.bukkit.BukkitMain;
import lombok.Getter;
import br.com.pentamc.bukkit.api.server.Server;

@Getter
public class Listener implements org.bukkit.event.Listener {
	
	private BukkitMain main;
	
	public Listener() {
		main = BukkitMain.getInstance();
	}
	
	public Server getServerConfig() {
		return main.getServerConfig();
	}

}
