package br.com.pentamc.shadow.event;

import br.com.pentamc.shadow.challenge.Challenge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import br.com.pentamc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class GladiatorClearEvent extends NormalEvent {
	
	private Challenge challenge;

}
