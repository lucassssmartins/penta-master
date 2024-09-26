package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class PyroKit extends DefaultKit {

	public PyroKit() {
		super("pyro", "Como um piromaniaco coloque fogo em tudo com suas bolas de fogo",
				new ItemStack(Material.FIREBALL), 23000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("pyro"));
	}

}
