package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class TurtleKit extends DefaultKit {

	public TurtleKit() {
		super("turtle", "Tome menos dano enquanto estiver agachado", new ItemStack(Material.DIAMOND_CHESTPLATE), 19000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("turtle"));
	}

}
