package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class SnailKit extends DefaultKit {

	public SnailKit() {
		super("snail", "Deixe seus inimigos mais lerdos", new ItemStack(Material.WEB), 24500);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("snail"));
	}

}
