package br.com.pentamc.competitive.event.ability;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import br.com.pentamc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class GladiatorScapeEvent extends NormalEvent {
	
	private Player gladiator;

}
