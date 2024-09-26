package br.com.pentamc.bukkit.event.restore;

import br.com.pentamc.bukkit.event.NormalEvent;
import org.bukkit.event.Cancellable;

import lombok.Getter;
import lombok.Setter;

public class RestoreEvent extends NormalEvent implements Cancellable {
	
	@Setter
	@Getter
	private boolean cancelled;

}
