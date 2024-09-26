package br.com.pentamc.bukkit.scheduler;

import br.com.pentamc.bukkit.BukkitConst;
import org.bukkit.Bukkit;

import br.com.pentamc.bukkit.event.update.UpdateEvent;
import br.com.pentamc.bukkit.event.update.UpdateEvent.UpdateType;

public class UpdateScheduler implements Runnable {

	public static long currentTick;

	@Override
	public void run() {
		currentTick++;

		if (currentTick % BukkitConst.TPS / 20 == 0) {
			Bukkit.getPluginManager().callEvent(new UpdateEvent(UpdateType.TICK, currentTick));
		}

		if (currentTick % BukkitConst.TPS == 0) {
			Bukkit.getPluginManager().callEvent(new UpdateEvent(UpdateType.SECOND, currentTick));
		}

		if (currentTick % BukkitConst.TPS * 60 == 0) {
			Bukkit.getPluginManager().callEvent(new UpdateEvent(UpdateType.MINUTE, currentTick));
		}
	}
}
