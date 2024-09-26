package br.com.pentamc.shadow.event;

import br.com.pentamc.shadow.challenge.Challenge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import br.com.pentamc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class GladiatorSpectatorEvent extends NormalEvent {
	
	private Player player;
	private Challenge challenge;
	private Action action;
	
	public enum Action {
		
		JOIN, LEAVE
		
	}

}
