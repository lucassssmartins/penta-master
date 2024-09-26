package br.com.pentamc.bukkit.event.player;

import br.com.pentamc.bukkit.event.NormalEvent;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerOpenInventoryEvent extends NormalEvent {

    private Player player;
	private Inventory inventory;

}
