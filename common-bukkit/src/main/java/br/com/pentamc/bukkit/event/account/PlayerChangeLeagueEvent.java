package br.com.pentamc.bukkit.event.account;

import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.event.PlayerCancellableEvent;
import br.com.pentamc.common.account.League;
import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class PlayerChangeLeagueEvent extends PlayerCancellableEvent {
	
	private BukkitMember bukkitMember;
	private League oldLeague;
	private League newLeague;

	public PlayerChangeLeagueEvent(Player p, BukkitMember player, League oldLeague, League newLeague) {
		super(p);
		this.bukkitMember = player;
		this.oldLeague = oldLeague;
		this.newLeague = newLeague;
	}
}
