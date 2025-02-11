package br.com.pentamc.bukkit.event.teleport;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import br.com.pentamc.bukkit.event.PlayerCancellableEvent;

public class PlayerTeleportCommandEvent extends PlayerCancellableEvent {

	@Getter
	@Setter
	private TeleportResult result;

	public PlayerTeleportCommandEvent(Player player, TeleportResult result) {
		super(player);
		this.result = result;
	}

	public static enum TeleportResult {
		NO_PERMISSION, ONLY_PLAYER_TELEPORT, ALLOWED;
	}

}
