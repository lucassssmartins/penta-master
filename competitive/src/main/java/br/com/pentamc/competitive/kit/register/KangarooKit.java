package br.com.pentamc.competitive.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class KangarooKit extends DefaultKit {

	public KangarooKit() {
		super("kangaroo", "Movimente-se mais rapido com seu kangaroo", new ItemStack(Material.FIREWORK), 36000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("kangaroo"));
	}

}
