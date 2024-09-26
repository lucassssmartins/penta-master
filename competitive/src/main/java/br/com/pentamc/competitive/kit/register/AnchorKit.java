package br.com.pentamc.competitive.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class AnchorKit extends DefaultKit {

	public AnchorKit() {
		super("anchor", "Se prenda ao chão e não saia dele", new ItemStack(Material.ANVIL), 18000,
				Arrays.asList(AssassinKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("anchor"));
	}

}
