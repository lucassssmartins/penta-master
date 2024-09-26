package br.com.pentamc.competitive.listener.register;

import br.com.pentamc.competitive.event.game.GameStateChangeEvent;
import br.com.pentamc.competitive.event.game.GameTimeEvent;
import br.com.pentamc.competitive.listener.GameListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.server.loadbalancer.server.MinigameState;

public class UpdateListener extends GameListener {

	@EventHandler
	public void onGameStateChange(GameStateChangeEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				CommonGeneral.getInstance().getServerData().updateStatus(
						MinigameState.valueOf(getGameGeneral().getGameState().toString()), getGameGeneral().getTime());
			}
		}.runTaskAsynchronously(getGameMain());
	}

	@EventHandler
	public void onGameTime(GameTimeEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				CommonGeneral.getInstance().getServerData().updateStatus(
						MinigameState.valueOf(getGameGeneral().getGameState().toString()), getGameGeneral().getTime());
			}
		}.runTaskAsynchronously(getGameMain());
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

}
