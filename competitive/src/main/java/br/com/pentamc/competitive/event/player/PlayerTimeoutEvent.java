package br.com.pentamc.competitive.event.player;

import br.com.pentamc.competitive.constructor.Gamer;
import lombok.Getter;
import br.com.pentamc.bukkit.event.PlayerCancellableEvent;

@Getter
public class PlayerTimeoutEvent extends PlayerCancellableEvent {
	
	private Gamer gamer;

	public PlayerTimeoutEvent(Gamer gamer) {
		super(null);
		this.gamer = gamer;
	}

}