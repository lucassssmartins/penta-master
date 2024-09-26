package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class CamelKit extends DefaultKit {

	public CamelKit() {
		super("camel", "Receba efeitos em certos biomas e faça sopas especiais.", new ItemStack(Material.SAND), 18000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("camel"));
	}

}