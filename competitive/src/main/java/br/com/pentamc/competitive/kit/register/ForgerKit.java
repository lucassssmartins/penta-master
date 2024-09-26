package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class ForgerKit extends DefaultKit {

	public ForgerKit() {
		super("forger", "Forje barras misturando carvão e minérios em seu inventário", new ItemStack(Material.COAL),
				28500);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("forger"));
	}

}
