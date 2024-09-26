package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class ThorKit extends DefaultKit {

	public ThorKit() {
		super("thor", "Lance raios com o seu machado", new ItemStack(Material.WOOD_AXE), 21000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("thor"));
	}

}
