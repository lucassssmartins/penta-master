package br.com.pentamc.competitive.abilities.register;

import java.util.ArrayList;
import java.util.Random;

import br.com.pentamc.competitive.abilities.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.pentamc.bukkit.event.player.PlayerDamagePlayerEvent;

public class ViperAbility extends Ability {

	public ViperAbility() {
		super("Viper", new ArrayList<>());
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player damager = event.getDamager();

		if (!hasAbility(damager))
			return;

		Random r = new Random();
		Player damaged = event.getPlayer();
		
		if (r.nextInt(3) == 0) {
			damaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 3 * 20, 0));
		}
	}
}
