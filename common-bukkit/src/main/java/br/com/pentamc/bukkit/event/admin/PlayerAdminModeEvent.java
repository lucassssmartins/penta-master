package br.com.pentamc.bukkit.event.admin;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerAdminModeEvent extends PlayerCancellableEvent {

	private AdminMode adminMode;
	@Setter
	private GameMode gameMode;

	public PlayerAdminModeEvent(Player player, AdminMode adminMode, GameMode mode) {
		super(player);
		this.adminMode = adminMode;
		this.gameMode = mode;
	}

	public static enum AdminMode {
		ADMIN, //
		PLAYER
	}

}
