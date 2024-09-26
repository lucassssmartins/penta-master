package br.com.pentamc.bukkit.listener.register;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.api.cooldown.CooldownController;
import br.com.pentamc.bukkit.api.cooldown.types.Cooldown;
import br.com.pentamc.bukkit.api.hologram.Hologram;
import br.com.pentamc.bukkit.api.hologram.TouchHandler;
import br.com.pentamc.bukkit.api.hologram.ViewHandler;
import br.com.pentamc.bukkit.api.hologram.impl.CraftHologram;
import br.com.pentamc.bukkit.api.hologram.impl.CraftSingleHologram;
import br.com.pentamc.bukkit.event.update.UpdateEvent;
import br.com.pentamc.bukkit.listener.Listener;
import br.com.pentamc.common.CommonConst;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;

import br.com.pentamc.bukkit.event.player.PlayerDamagePlayerEvent;

import java.util.HashMap;
import java.util.Map;

public class CombatListener extends Listener {

	public static final Map<Player, Cooldown> cleanMap = new HashMap<>();
	public static boolean enabledClean = true;

	public static void cancelAll() {
		enabledClean = false;
		for (Player p : Bukkit.getOnlinePlayers()) {
			removeClean(p);
		}
	}

	public static void setClean(Player target) {
		if (!enabledClean)
			return;
		cleanMap.put(target, new Cooldown("clean", 25L));
	}

	public static boolean isInClean(Player target) {
		if (!enabledClean)
			return false;
		Cooldown cooldown = cleanMap.get(target);
		return cooldown != null && !cooldown.expired();
	}

	public static void removeClean(Player player) {
		cleanMap.remove(player);
	}

	@EventHandler
	public void update(UpdateEvent event) {
		if (event.getCurrentTick() % 10 == 0) {
			if (!enabledClean)
				return;
			Bukkit.getScheduler().runTaskAsynchronously(BukkitMain.getInstance(), () -> {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (cleanMap.containsKey(p)) {
						Cooldown cooldown = cleanMap.get(p);
						if (cooldown.expired())
							removeClean(p);

					}
				}
			});
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		removeClean(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (event.getCause() == DamageCause.FALL)
			if (player.hasMetadata("nofall")) {
				MetadataValue metadata = player.getMetadata("nofall").stream().findFirst().orElse(null);

				if (metadata.asLong() > System.currentTimeMillis())
					event.setCancelled(true);

				metadata.invalidate();
			}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		Player damager = null;

		if (event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();

			if (projectile.getShooter() instanceof Player) {
				damager = (Player) projectile.getShooter();
			}
		}


		if (isInClean(damager)) {
			removeClean(damager);
		} else if (isInClean(player)) {
			event.setCancelled(true);
			if (damager instanceof Player) {
				damager.sendMessage("§cEste jogador está em clean " + CommonConst.DECIMAL_FORMAT.format(cleanMap.get(player).getRemaining()));
			}
		}

		if (!(damager instanceof Player))
			return;

		PlayerDamagePlayerEvent playerDamagePlayerEvent = new PlayerDamagePlayerEvent(player, damager,
				event.isCancelled(), event.getDamage(), event.getFinalDamage());

		Bukkit.getPluginManager().callEvent(playerDamagePlayerEvent);

		event.setCancelled(playerDamagePlayerEvent.isCancelled());
		event.setDamage(playerDamagePlayerEvent.getDamage());
	}

}
