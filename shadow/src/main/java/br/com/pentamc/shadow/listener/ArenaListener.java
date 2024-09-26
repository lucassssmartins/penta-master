package br.com.pentamc.shadow.listener;

import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.shadow.event.GladiatorStartEvent;

public class ArenaListener implements Listener {

	@EventHandler
	public void onGladiatorStart(GladiatorStartEvent event) {
		Location firstLocation = BukkitMain.getInstance().getLocationFromConfig("first-location");

		event.getChallenge().getEnimy().teleport(firstLocation);

		Location secondLocation = BukkitMain.getInstance().getLocationFromConfig("second-location");

		event.getChallenge().getPlayer().teleport(secondLocation);

		event.getChallenge().setMainLocation(BukkitMain.getInstance().getLocationFromConfig("main-location"));
	}
}
