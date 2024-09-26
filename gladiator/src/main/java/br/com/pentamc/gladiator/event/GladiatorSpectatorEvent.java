package br.com.pentamc.gladiator.event;

import br.com.pentamc.gladiator.challenge.Challenge;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
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
