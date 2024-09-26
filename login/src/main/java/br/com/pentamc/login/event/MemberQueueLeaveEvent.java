package br.com.pentamc.login.event;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class MemberQueueLeaveEvent extends NormalEvent {
	
	private Player player;
	private BukkitMember member;

}
