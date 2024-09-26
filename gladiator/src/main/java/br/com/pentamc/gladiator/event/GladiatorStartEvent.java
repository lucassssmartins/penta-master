package br.com.pentamc.gladiator.event;

import br.com.pentamc.gladiator.challenge.Challenge;
import org.bukkit.event.Cancellable;

import lombok.Getter;
import lombok.Setter;
import br.com.pentamc.bukkit.event.NormalEvent;

@Getter
public class GladiatorStartEvent extends NormalEvent implements Cancellable {

	private Challenge challenge;
	@Setter
	private boolean cancelled;

	public GladiatorStartEvent(Challenge challenge) {
		this.challenge = challenge;
	}

}
