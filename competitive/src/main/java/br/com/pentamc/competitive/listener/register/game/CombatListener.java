package br.com.pentamc.competitive.listener.register.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import br.com.pentamc.bukkit.event.update.UpdateEvent;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.game.Team;
import br.com.pentamc.competitive.utils.ServerConfig;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.event.player.PlayerDamagePlayerEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class CombatListener implements Listener {

	public static final Map<UUID, CombatLog> combatMap = new HashMap<>();

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		UUID damaged = event.getPlayer().getUniqueId();
		UUID damager = event.getDamager().getUniqueId();

		CombatLog damagedCombatLog = combatMap.get(damaged);

		if (damagedCombatLog == null)
			damagedCombatLog = combatMap.put(damaged, new CombatLog(damager));
		else
			damagedCombatLog.hitted(damager);

		CombatLog damagerCombatLog = combatMap.get(damager);

		if (damagerCombatLog == null)
			damagerCombatLog = combatMap.put(damager, new CombatLog(damaged));
		else
			damagerCombatLog.hitted(damaged);

		if (event.getPlayer().hasPotionEffect(PotionEffectType.WITHER)) {
			event.setCancelled(true);
			event.getPlayer().damage(event.getFinalDamage());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		combatMap.remove(event.getEntity().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (BukkitMain.getInstance().getServerConfig().isRestoreMode())
			return;

		Player p = event.getPlayer();
		CombatLog log = getCombatLog(p.getUniqueId());

		if (log == null)
			return;

		if (System.currentTimeMillis() < log.getTime()) {
			Player combatLogger = Bukkit.getPlayer(log.getCombatLogged());

			if (combatLogger != null)
				if (combatLogger.isOnline())
					p.damage(10000.0, combatLogger);
		}

		removeCombatLog(p.getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (!ServerConfig.getInstance().isDamageEnabled()) {
			event.setCancelled(true);
			return;
		}

		if ((event.getCause() == DamageCause.CONTACT || event.getCause() == DamageCause.ENTITY_ATTACK) && !ServerConfig.getInstance().isPvpEnabled()) {
			event.setCancelled(true);
			return;
		}
	}

	public CombatLog getCombatLog(UUID uuid) {
		return combatMap.containsKey(uuid) ? combatMap.get(uuid) : null;
	}

	public void removeCombatLog(UUID uuid) {
		CombatLog combatLog = combatMap.get(uuid);

		if (combatLog == null)
			return;

		UUID otherPlayer = combatLog.getCombatLogged();
		CombatLog otherPlayerCombatLog = combatMap.get(otherPlayer);

		if (otherPlayerCombatLog != null) {
			if (otherPlayerCombatLog.getCombatLogged() == uuid)
				combatMap.remove(otherPlayer);
		}
		
		combatMap.remove(uuid);
	}

	public class CombatLog {

		private UUID combatLogged;
		private long time;

		public CombatLog(UUID combatLogged) {
			this.combatLogged = combatLogged;
			this.time = System.currentTimeMillis() + 10000;
		}

		public UUID getCombatLogged() {
			return combatLogged;
		}

		public long getTime() {
			return time;
		}

		public void hitted(UUID uuid) {
			this.combatLogged = uuid;
			this.time = System.currentTimeMillis() + 10000;
		}
	}

}
