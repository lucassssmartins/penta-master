package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;
import br.com.pentamc.bukkit.api.item.ItemBuilder;

public class MinerKit extends DefaultKit {

	public MinerKit() {
		super("miner", "Quebre minerios rapidamente", new ItemBuilder().type(Material.STONE_PICKAXE).glow()
				.enchantment(Enchantment.DURABILITY).enchantment(Enchantment.DIG_SPEED, 2).build(), 29000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("miner"));
	}

}
