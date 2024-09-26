package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class LumberjackKit extends DefaultKit {

	public LumberjackKit() {
		super("lumberjack", "Quebre árvores como um verdadeiro lenhador!", new ItemStack(Material.WOOD_AXE), 23000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("lumberjack"));
	}

}
