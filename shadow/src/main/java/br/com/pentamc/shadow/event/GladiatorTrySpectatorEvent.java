package br.com.pentamc.shadow.event;

import br.com.pentamc.shadow.challenge.Challenge;
import lombok.Getter;
import org.bukkit.entity.Player;
import br.com.pentamc.bukkit.event.PlayerCancellableEvent;

@Getter
public class GladiatorTrySpectatorEvent extends PlayerCancellableEvent {

	private Challenge challenge;

	public GladiatorTrySpectatorEvent(Player player, Challenge challenge) {
		super(player);
		this.challenge = challenge;
	}

}
