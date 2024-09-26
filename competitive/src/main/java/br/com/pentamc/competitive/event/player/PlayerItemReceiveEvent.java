package br.com.pentamc.competitive.event.player;

import org.bukkit.entity.Player;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;

public class PlayerItemReceiveEvent extends PlayerCancellableEvent {

	public PlayerItemReceiveEvent(Player player) {
		super(player);
	}

}
