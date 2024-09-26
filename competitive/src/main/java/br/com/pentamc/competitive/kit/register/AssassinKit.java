package br.com.pentamc.competitive.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class AssassinKit extends DefaultKit {

	public AssassinKit() {
		super("assassin", "A cada 5 hits dê 1 coração de vida a mais", new ItemStack(Material.IRON_SWORD), 32000,
				Arrays.asList(AnchorKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("assassin"));
	}

}
