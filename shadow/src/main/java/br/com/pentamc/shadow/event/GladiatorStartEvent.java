package br.com.pentamc.shadow.event;

import br.com.pentamc.shadow.challenge.Challenge;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
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
