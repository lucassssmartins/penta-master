package br.com.pentamc.competitive.event.kit;

import org.bukkit.entity.Player;

import br.com.pentamc.competitive.kit.Kit;
import br.com.pentamc.competitive.kit.KitType;
import lombok.Getter;
import br.com.pentamc.bukkit.event.PlayerCancellableEvent;

@Getter
public class PlayerSelectedKitEvent extends PlayerCancellableEvent {
	
	private Kit kit;
	private KitType kitType;
	
	public PlayerSelectedKitEvent(Player player, Kit kit, KitType kitType) {
		super(player);
		this.kit = kit;
		this.kitType = kitType;
	}

}
