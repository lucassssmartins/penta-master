package br.com.pentamc.bukkit.event.player;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import lombok.Getter;

@Getter
public class MenuOpenEvent extends PlayerCancellableEvent {
	
	private Inventory inventory;

	public MenuOpenEvent(Player player, Inventory inventory) {
		super(player);
		this.inventory = inventory;
	}

}
