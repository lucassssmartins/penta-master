package br.com.pentamc.competitive.event.ability;

import org.bukkit.entity.Player;

import lombok.Getter;
import br.com.pentamc.bukkit.event.PlayerCancellableEvent;

@Getter
public class PlayerStompedEvent extends PlayerCancellableEvent {

	private Player stomper;
	
	public PlayerStompedEvent(Player player, Player stomper) {
		super(player);
		this.stomper = stomper;
	}

}
