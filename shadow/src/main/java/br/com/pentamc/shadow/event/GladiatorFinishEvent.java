package br.com.pentamc.shadow.event;

import br.com.pentamc.shadow.challenge.Challenge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import br.com.pentamc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class GladiatorFinishEvent extends NormalEvent {

	private Challenge challenge;
	private Player loser;
	private Player winner;

}
