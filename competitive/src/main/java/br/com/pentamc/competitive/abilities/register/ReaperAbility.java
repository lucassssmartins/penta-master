package br.com.pentamc.competitive.abilities.register;

import java.util.Arrays;
import java.util.Random;

import br.com.pentamc.competitive.abilities.Ability;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;
import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.event.player.PlayerDamagePlayerEvent;

public class ReaperAbility extends Ability {

	public ReaperAbility() {
		super("Reaper", Arrays.asList(new ItemBuilder().type(Material.WOOD_HOE).name(ChatColor.GOLD + "Reaper").build()));
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player damager = (Player) event.getDamager();
		
		if (!hasAbility(damager))
			return;
		
		ItemStack item = damager.getItemInHand();
		
		if (item == null)
			return;
		
		if (!isAbilityItem(item))
			return;
		
		event.setCancelled(true);
		item.setDurability((short) 0);
		damager.updateInventory();
		Random r = new Random();
		Player damaged = (Player) event.getPlayer();
		
		if (damaged instanceof Player) {
			if (r.nextInt(3) == 0) {
				damaged.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 0));
			}
		}
	}
}
