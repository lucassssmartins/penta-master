package br.com.pentamc.gladiator.event;

import br.com.pentamc.gladiator.challenge.Challenge;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import br.com.pentamc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class GladiatorFinishEvent extends NormalEvent {

	private Challenge challenge;
	private Player loser;
	private Player winner;

}
