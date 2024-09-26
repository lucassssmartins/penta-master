package br.com.pentamc.competitive.abilities.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import br.com.pentamc.competitive.abilities.Ability;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import net.md_5.bungee.api.ChatColor;
import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.vanish.AdminMode;

public class TimelordAbility extends Ability {

	private static final int RADIUS = 5;
	private Map<Player, Long> timelordList = new HashMap<>();

	public TimelordAbility() {
		super("Timelord",
				Arrays.asList(new ItemBuilder().name(ChatColor.GOLD + "Timelord").type(Material.WATCH).build()));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (hasAbility(event.getPlayer()) && isAbilityItem(event.getPlayer().getItemInHand())) {
			if (isCooldown(event.getPlayer()))
				return;

			for (Player game : Bukkit.getOnlinePlayers()) {
				if (game == event.getPlayer())
					continue;

				if (AdminMode.getInstance().isAdmin(game))
					continue;

				double distance = event.getPlayer().getLocation().distance(game.getPlayer().getLocation());

				if (distance <= RADIUS) {
					timelordList.put(game, System.currentTimeMillis() + 4000l);
				}
			}

			event.setCancelled(true);
			addCooldown(event.getPlayer(), 45);
			event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.WITHER_SHOOT, 1.0f, 1.0f);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (timelordList.containsKey(event.getPlayer()))
			if (timelordList.get(event.getPlayer()) > System.currentTimeMillis())
				event.setCancelled(true);
			else
				timelordList.remove(event.getPlayer());
	}

}
