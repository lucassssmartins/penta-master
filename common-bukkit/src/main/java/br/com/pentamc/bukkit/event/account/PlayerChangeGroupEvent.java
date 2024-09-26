package br.com.pentamc.bukkit.event.account;

import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.event.PlayerCancellableEvent;
import br.com.pentamc.common.permission.Group;
import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class PlayerChangeGroupEvent extends PlayerCancellableEvent {
	
	private BukkitMember bukkitMember;
	private Group group;

	public PlayerChangeGroupEvent(Player p, BukkitMember player, Group group) {
		super(p);
		this.bukkitMember = player;
		this.group = group;
	}
}
