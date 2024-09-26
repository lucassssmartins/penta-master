package br.com.pentamc.bukkit.event.vanish;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;
import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class PlayerShowToPlayerEvent extends PlayerCancellableEvent {
	private Player toPlayer;

	public PlayerShowToPlayerEvent(Player player, Player toPlayer) {
		super(player);
		this.toPlayer = toPlayer;
	}
}
