package br.com.pentamc.competitive.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class GrapplerKit extends DefaultKit {

	public GrapplerKit() {
		super("grappler", "Movimente-se mais rapido com sua corda", new ItemStack(Material.LEASH), 34000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("grappler"));
	}

}
