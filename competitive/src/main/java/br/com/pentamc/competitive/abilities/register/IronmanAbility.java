package br.com.pentamc.competitive.abilities.register;

import java.util.ArrayList;

import br.com.pentamc.competitive.abilities.Ability;
import br.com.pentamc.competitive.constructor.Gamer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;

public class IronmanAbility extends Ability {

	public IronmanAbility() {
		super("Ironman", new ArrayList<>());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity().getPlayer();
		
		if (e.getEntity().getKiller() == null)
			return;

		if (player == null || !hasAbility(e.getEntity().getKiller()))
			return;
		
		Player killer = e.getEntity().getKiller();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(killer);

		if (killer.getInventory().firstEmpty() == -1) {
			killer.getWorld().dropItemNaturally(killer.getLocation(),
					new ItemStack(Material.IRON_INGOT, gamer.getMatchKills() + 1));
		} else {
			killer.getInventory().addItem(new ItemStack(Material.IRON_INGOT, gamer.getMatchKills() + 1));
		}
	}

}
