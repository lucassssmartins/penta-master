package br.com.pentamc.bukkit.event.login;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;
import br.com.pentamc.common.account.Member;
import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class PlayerChangeLoginStatusEvent extends PlayerCancellableEvent {
	
	private Member member;
	private boolean logged;

	public PlayerChangeLoginStatusEvent(Player p, Member member, boolean newState) {
		super(p);
		this.member = member;
		this.logged = newState;
	}
}
