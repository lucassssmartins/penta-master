package br.com.pentamc.bukkit.event.account;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;
import br.com.pentamc.common.tag.Tag;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerTryChangeTagEvent extends PlayerCancellableEvent {
	
	private Tag oldTag;
	@Setter
	private Tag newTag;
	private boolean forced;

	public PlayerTryChangeTagEvent(Player p, Tag oldTag, Tag newTag, boolean forced) {
		super(p);
		this.oldTag = oldTag;
		this.newTag = newTag;
		this.forced = forced;
	}

}
