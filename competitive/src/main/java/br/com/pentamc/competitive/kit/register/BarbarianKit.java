package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;
import br.com.pentamc.bukkit.api.item.ItemBuilder;

public class BarbarianKit extends DefaultKit {

	public BarbarianKit() {
		super("barbarian", "Ganhe XP matando players para evoluir sua espada",
				new ItemBuilder().type(Material.WOOD_SWORD).glow().enchantment(Enchantment.DURABILITY, 1).build(),
				28000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("barbarian"));
	}

}
