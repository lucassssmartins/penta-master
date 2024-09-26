package br.com.pentamc.lobby.listener;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.tag.Tag;
import br.com.pentamc.lobby.LobbyPlatform;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.pentamc.bukkit.api.title.Title;
import br.com.pentamc.lobby.gamer.Gamer;

public class GamerListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (member.hasGroupPermission(Group.VIP)) {
			player.setAllowFlight(true);
			player.setFlying(true);

			if (member.getGroup().ordinal() <= Group.YOUTUBER.ordinal()
					&& member.getGroup().ordinal() >= Group.VIP.ordinal())
//			if (member.getGroup().ordinal() >= Group.VIP.ordinal())
				Bukkit.broadcastMessage(Tag.valueOf(member.getGroup().name()).getPrefix() + " " + player.getName()
						+ " §6entrou no lobby!");
		} else {
			for (Gamer gamer : LobbyPlatform.getInstance().getPlayerManager().getGamers())
				if (!gamer.isSeeing())
					gamer.getPlayer().hidePlayer(player);

			player.setFlying(false);
			player.setAllowFlight(false);
		}

		player.teleport(
				member.getLoginConfiguration().isLogged() ? BukkitMain.getInstance().getLocationFromConfig("spawn")
						: BukkitMain.getInstance().getLocationFromConfig("login"));

		Title.clear(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		LobbyPlatform.getInstance().getPlayerManager().removeGamer(e.getPlayer());
	}

}
