package br.com.pentamc.competitive.abilities.register;

import java.util.ArrayList;
import java.util.HashMap;

import br.com.pentamc.bukkit.api.cooldown.types.Cooldown;
import br.com.pentamc.competitive.abilities.Ability;
import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.event.ability.PlayerNinjaTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.bukkit.event.player.PlayerDamagePlayerEvent;

public class NinjaAbility extends Ability {

	private final HashMap<String, NinjaHit> ninjaHits;

	public NinjaAbility() {
		super("Ninja", new ArrayList<>());

		ninjaHits = new HashMap<>();
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player damaged = event.getPlayer();
		Player damager = event.getDamager();

		if (hasAbility(damaged)) {
			Cooldown cooldown = getCooldown(damaged);
			if (cooldown == null || cooldown.getRemaining() < 1) {
				removeCooldown(damaged);
				addCooldown(damaged, 2);
			}
		}

		if (hasAbility(damager)) {
			NinjaHit ninjaHit = ninjaHits.get(damager.getName());

			if (ninjaHit == null)
				ninjaHit = new NinjaHit(damaged);
			else
				ninjaHit.setTarget(damaged);

			ninjaHits.put(damager.getName(), ninjaHit);
		}
	}

	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();

		if (!event.isSneaking())
			return;

		if (!hasAbility(player))
			return;

		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

		if (gamer.isNotPlaying())
			return;

		if (!ninjaHits.containsKey(player.getName()))
			return;

		NinjaHit ninjaHit = ninjaHits.get(player.getName());
		Player target = ninjaHit.getTarget();

		if (target.isDead())
			return;

		if (ninjaHit.getTargetExpires() < System.currentTimeMillis())
			return;

		if ((player.getLocation().distance(target.getLocation()) > 35)) {
			player.sendMessage("§cO ultimo jogador hitado está muito longe!");
			return;
		}

		if (isCooldown(player))
			return;

		if (gamer.hasAbility("kangaroo"))
			if (gamer.getAbility("kangaroo").isAbilityItem(player.getItemInHand()))
				return;

		PlayerNinjaTeleportEvent playerNinjaTeleportEvent = new PlayerNinjaTeleportEvent(player, target);

		Bukkit.getPluginManager().callEvent(playerNinjaTeleportEvent);

		if (playerNinjaTeleportEvent.isCancelled()) {
			ninjaHits.remove(player.getName());
		} else {
			player.teleport(target.getLocation());
			addCooldown(player.getUniqueId(), 6);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (ninjaHits.containsKey(event.getEntity().getName()))
			ninjaHits.remove(event.getEntity().getName());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (ninjaHits.containsKey(event.getPlayer().getName()))
			ninjaHits.remove(event.getPlayer().getName());
	}

	private static class NinjaHit {
		private Player target;
		private long targetExpires;

		public NinjaHit(Player target) {
			this.target = target;
			this.targetExpires = System.currentTimeMillis() + 15000;
		}

		public Player getTarget() {
			return target;
		}

		public long getTargetExpires() {
			return targetExpires;
		}

		public void setTarget(Player player) {
			this.target = player;
			this.targetExpires = System.currentTimeMillis() + 20000;
		}

	}

}
