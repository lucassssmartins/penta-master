package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class CultivatorKit extends DefaultKit {

	public CultivatorKit() {
		super("cultivator", "Cultive plantas rapidamente",
				new ItemStack(Material.SAPLING), 17000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("cultivator"));
	}

}
