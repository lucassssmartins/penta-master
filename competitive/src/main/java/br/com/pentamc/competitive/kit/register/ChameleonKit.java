package br.com.pentamc.competitive.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class ChameleonKit extends DefaultKit {

	public ChameleonKit() {
		super("chameleon", "Transforme-se em mobs", new ItemStack(Material.WHEAT), 26000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("chameleon"));
	}

}
