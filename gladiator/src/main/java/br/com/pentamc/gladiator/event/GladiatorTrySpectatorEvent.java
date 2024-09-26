package br.com.pentamc.gladiator.event;

import br.com.pentamc.gladiator.challenge.Challenge;
import org.bukkit.entity.Player;

import lombok.Getter;
import br.com.pentamc.bukkit.event.PlayerCancellableEvent;

@Getter
public class GladiatorTrySpectatorEvent extends PlayerCancellableEvent {

	private Challenge challenge;

	public GladiatorTrySpectatorEvent(Player player, Challenge challenge) {
		super(player);
		this.challenge = challenge;
	}

}
