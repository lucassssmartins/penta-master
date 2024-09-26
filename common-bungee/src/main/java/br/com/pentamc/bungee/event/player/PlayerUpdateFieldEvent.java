package br.com.pentamc.bungee.event.player;

import br.com.pentamc.bungee.bungee.BungeeMember;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Event;

@AllArgsConstructor
@Getter
public class PlayerUpdateFieldEvent extends Event {
	
	private BungeeMember bungeeMember;
	private String field;
	@Setter
	private Object oldObject;
	@Setter
	private Object object;

}
