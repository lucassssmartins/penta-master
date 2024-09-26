package br.com.pentamc.bukkit.event.account;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;
import br.com.pentamc.common.account.medal.Medal;
import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class PlayerChangeMedalEvent extends PlayerCancellableEvent {
	
	private Medal oldMedal;
	private Medal medal;

	public PlayerChangeMedalEvent(Player player, Medal oldMedal, Medal medal) {
		super(player);
		this.oldMedal = oldMedal;
		this.medal = medal;
	}

}
