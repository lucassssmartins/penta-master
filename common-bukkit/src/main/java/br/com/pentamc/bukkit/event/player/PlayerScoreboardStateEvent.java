package br.com.pentamc.bukkit.event.player;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;
import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class PlayerScoreboardStateEvent extends PlayerCancellableEvent {
	
	private boolean scoreboardEnabled;
	
	public PlayerScoreboardStateEvent(Player player, boolean scoreboardEnabled) {
		super(player);
		this.scoreboardEnabled = scoreboardEnabled;
	}

}
