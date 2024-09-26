package br.com.pentamc.competitive.scheduler.types;

import org.bukkit.event.Listener;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.scheduler.Schedule;

/**
 * 
 * GameSchedule is a class that will assist me to make a GameScheduler like
 * PregameScheduler
 * 
 * @author yandv
 *
 */

public interface GameSchedule extends Schedule, Listener {
	
	/**
	 * 
	 * Register a listenerList 
	 * 
	 */

	void registerListener();

	void unregisterListener();

	default void unload() {
		GameGeneral.getInstance().getSchedulerController().removeSchedule(this);
	}

}
