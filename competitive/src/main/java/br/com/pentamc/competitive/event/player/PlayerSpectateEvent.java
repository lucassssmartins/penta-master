package br.com.pentamc.competitive.event.player;

import org.bukkit.entity.Player;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;

public class PlayerSpectateEvent extends PlayerCancellableEvent {

	public PlayerSpectateEvent(Player player) {
		super(player);
	}

}
