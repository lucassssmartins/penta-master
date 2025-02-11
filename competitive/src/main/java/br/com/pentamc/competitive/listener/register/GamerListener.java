package br.com.pentamc.competitive.listener.register;

import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.constructor.Gamer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.listener.GameListener;
import br.com.pentamc.bukkit.event.admin.PlayerAdminModeEvent;

public class GamerListener extends GameListener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED)
			return;

		Player player = event.getPlayer();

		if (!GameMain.getInstance().isAllowEntry()) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cEsta sala está restrita à membros da equipe.");
			return;
		}

		Gamer gamer = GameGeneral.getInstance().getGamerController().containsKey(player.getUniqueId())
				? GameGeneral.getInstance().getGamerController().getGamer(player)
				: new Gamer(player);

		if (!GameGeneral.getInstance().getGamerController().containsKey(player.getUniqueId()))
			GameGeneral.getInstance().getGamerController().loadGamer(player.getUniqueId(), gamer);

		gamer.setPlayer(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer().getUniqueId()) == null) {
			event.getPlayer().kickPlayer("§cConta não carregada!");
			return;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (isPregame()) {
			GameGeneral.getInstance().getGamerController().unload(event.getPlayer().getUniqueId());
		} else {
			Player player = event.getPlayer();
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

			if (gamer.isNotPlaying()) {
				event.setQuitMessage(null);
				return;
			}
		}
	}

	@EventHandler
	public void onPlayerAdminMode(PlayerAdminModeEvent event) {
		if (isPregame()) {
			Gamer gamer = getGameGeneral().getGamerController().getGamer(event.getPlayer().getUniqueId());

			if (event.getAdminMode() == PlayerAdminModeEvent.AdminMode.ADMIN) {
				gamer.setGamemaker(true);
			} else {
				gamer.setGamemaker(false);
				gamer.setSpectator(false);
			}
		} else {
			Gamer gamer = getGameGeneral().getGamerController().getGamer(event.getPlayer().getUniqueId());

			if (event.getAdminMode() == PlayerAdminModeEvent.AdminMode.ADMIN) {
				gamer.setGamemaker(true);
			} else {
				gamer.setGamemaker(false);
				gamer.setSpectator(false);
			}
		}
	}

}
