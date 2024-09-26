package br.com.pentamc.bukkit.event.login;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;
import br.com.pentamc.common.account.Member;
import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class PlayerRegisterEvent extends PlayerCancellableEvent {
	
	private Member member;

	public PlayerRegisterEvent(Player p, Member member) {
		super(p);
		this.member = member;
	}
}
