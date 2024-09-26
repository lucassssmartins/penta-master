package br.com.pentamc.competitive.event.ability;

import org.bukkit.entity.Player;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;

public class PlayerEndermageEvent extends PlayerCancellableEvent {

	public PlayerEndermageEvent(Player player) {
		super(player);
	}

}
