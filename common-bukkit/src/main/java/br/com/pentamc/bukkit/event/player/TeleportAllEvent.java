package br.com.pentamc.bukkit.event.player;

import br.com.pentamc.bukkit.event.NormalEvent;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeleportAllEvent extends NormalEvent {
	
	private Player target;

}
