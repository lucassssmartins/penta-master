package br.com.pentamc.bukkit.event.server;

import br.com.pentamc.bukkit.event.NormalEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerChangeEvent extends NormalEvent {

	private int totalMembers;

}
