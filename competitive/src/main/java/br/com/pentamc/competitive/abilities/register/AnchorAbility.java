package br.com.pentamc.competitive.abilities.register;

import java.util.ArrayList;

import br.com.pentamc.competitive.abilities.Ability;
import br.com.pentamc.competitive.game.GameState;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.bukkit.event.player.PlayerDamagePlayerEvent;

public class AnchorAbility extends Ability {

	public AnchorAbility() {
		super("Anchor", new ArrayList<>());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		if (GameGeneral.getInstance().getGameState() != GameState.GAMETIME)
			return;

		Player player = event.getPlayer();
		Player damager = event.getDamager();

		if (hasAbility(player) || hasAbility(damager)) {
			if (!GameGeneral.getInstance().getGamerController().getGamer(player).isNotPlaying()
					&& !GameGeneral.getInstance().getGamerController().getGamer(damager).isNotPlaying()) {
				player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 0.15F, 1.0F);

				velocityPlayer(player);
			}
		}
	}

	private void velocityPlayer(Player player) {
		player.setVelocity(new Vector(0, 0, 0));

		new BukkitRunnable() {
			public void run() {
				player.setVelocity(new Vector(0, 0, 0));
			}
		}.runTaskLater(GameMain.getPlugin(), 1L);
	}
}
