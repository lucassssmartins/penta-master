package br.com.pentamc.bukkit.listener.register;

import java.util.stream.Collectors;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.permission.Group;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.event.report.ReportReceiveEvent;

public class ReportListener implements Listener {

	@EventHandler
	public void onReportReceive(ReportReceiveEvent event) {
		CommonGeneral.getInstance().getMemberManager().getMembers().stream().filter(
				player -> player.getAccountConfiguration().isReportEnabled() && player.hasGroupPermission(Group.TRIAL))
				.collect(Collectors.toList()).forEach(member -> ((BukkitMember) member).getPlayer()
						.playSound(((BukkitMember) member).getPlayer().getLocation(), Sound.LEVEL_UP, 0.1f, 0.1f));
	}

}
