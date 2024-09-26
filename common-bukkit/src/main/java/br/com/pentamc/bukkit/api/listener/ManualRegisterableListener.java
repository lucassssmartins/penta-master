package br.com.pentamc.bukkit.api.listener;

import br.com.pentamc.bukkit.BukkitMain;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class ManualRegisterableListener implements RegisterableListener {
	
	@Getter
	private boolean registered;
	
	@Override
	public void registerListener() {
		if (!registered) {
			Bukkit.getPluginManager().registerEvents(this, BukkitMain.getInstance());
			registered = true;
		}
	}

	@Override
	public void unregisterListener() {
		if (registered) {
			HandlerList.unregisterAll(this);
			registered = false;
		}
	}


}
