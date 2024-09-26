package br.com.pentamc.bukkit.api.cooldown.event;

import br.com.pentamc.bukkit.api.cooldown.types.Cooldown;
import org.bukkit.entity.Player;

public class CooldownFinishEvent extends CooldownStopEvent {

	public CooldownFinishEvent(Player player, Cooldown cooldown) {
		super(player, cooldown);
	}
	
}
