package br.com.pentamc.login.event;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.event.PlayerCancellableEvent;

@Getter
public class MemberQueueEvent extends PlayerCancellableEvent {

	private BukkitMember member;
	@Setter
	private boolean priority;
	
	public MemberQueueEvent(Player player, BukkitMember member) {
		super(player);
		this.member = member;
	}
	
	public MemberQueueEvent(Player player, BukkitMember member, boolean priority) {
		super(player);
		this.member = member;
		this.priority = priority;
	}

}
