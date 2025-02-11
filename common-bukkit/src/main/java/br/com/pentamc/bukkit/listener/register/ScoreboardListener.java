package br.com.pentamc.bukkit.listener.register;

import br.com.pentamc.common.CommonGeneral;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import br.com.pentamc.bukkit.bukkit.BukkitMember;

public class ScoreboardListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuitListener(PlayerQuitEvent e) {
		Scoreboard board = e.getPlayer().getScoreboard();

		if (board != null) {
			for (Team t : board.getTeams()) {
				t.unregister();
			}

			for (Objective ob : board.getObjectives()) {
				ob.unregister();
			}
		}

		e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

		if (CommonGeneral.getInstance().getMemberManager().containsKey(e.getPlayer().getUniqueId())) {
			BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
					.getMember(e.getPlayer().getUniqueId());

			br.com.pentamc.bukkit.api.scoreboard.Scoreboard scoreboard = member.getScoreboard();
			if (scoreboard != null)
				scoreboard.removeViewer(member);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoinListener(PlayerJoinEvent e) {
		e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}

}
