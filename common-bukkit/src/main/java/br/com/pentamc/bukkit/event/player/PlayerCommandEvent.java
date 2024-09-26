package br.com.pentamc.bukkit.event.player;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;
import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class PlayerCommandEvent extends PlayerCancellableEvent {
	
	private String commandLabel;

	public PlayerCommandEvent(Player player, String commandLabel) {
		super(player);
		this.commandLabel = commandLabel;
	}

}
