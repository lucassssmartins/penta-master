package br.com.pentamc.shadow.listener;

import br.com.pentamc.shadow.GameMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import br.com.pentamc.bukkit.api.actionbar.ActionBarAPI;
import br.com.pentamc.bukkit.event.player.PlayerCommandEvent;
import br.com.pentamc.bukkit.event.player.PlayerDamagePlayerEvent;
import br.com.pentamc.shadow.gamer.Gamer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombatlogListener implements Listener {

	private List<String> blockedCommands = new ArrayList<>(Arrays.asList("spawn", "warp", "teleport"));

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Gamer entityGamer = GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId());

		if (!entityGamer.isInCombat()) {
			ActionBarAPI.send((Player) event.getPlayer(),
					"§cVocê entrou em combate com " + event.getDamager().getName() + "!");
		}

		Gamer damagerGamer = GameMain.getInstance().getGamerManager().getGamer(event.getDamager().getUniqueId());

		if (!damagerGamer.isInCombat()) {
			damagerGamer.setCombat();
			ActionBarAPI.send((Player) event.getDamager(),
					"§cVocê entrou em combate com " + event.getPlayer().getName() + "!");
		}

		entityGamer.setCombat();
		damagerGamer.setCombat();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId()).isInCombat())
			if (event.getPlayer().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

				event.getPlayer().damage(Integer.MAX_VALUE,
						((EntityDamageByEntityEvent) event.getPlayer().getLastDamageCause()).getDamager());
			} else
				event.getPlayer().damage(Integer.MAX_VALUE);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		GameMain.getInstance().getGamerManager().getGamer(event.getEntity().getUniqueId()).removeCombat();

		if (event.getEntity().getKiller() != null)
			GameMain.getInstance().getGamerManager().getGamer(event.getEntity().getKiller().getUniqueId())
					.removeCombat();
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId()).removeCombat();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerCommand(PlayerCommandEvent event) {
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId());

		if (gamer.isInCombat()) {
			String command = event.getCommandLabel();

			if (blockedCommands.contains(command.toLowerCase())) {
				event.getPlayer().sendMessage("§c§l> §fVocê não pode usar comandos em combate!");
				event.setCancelled(true);
			}
		}
	}

}
