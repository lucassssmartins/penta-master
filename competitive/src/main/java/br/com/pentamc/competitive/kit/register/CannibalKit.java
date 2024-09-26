package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class CannibalKit extends DefaultKit {

	public CannibalKit() {
		super("cannibal", "Ao bater em algum player ira deixa-lo com fome e a sua recuperará",
				new ItemStack(Material.RAW_FISH), 16000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("cannibal"));
	}
}
