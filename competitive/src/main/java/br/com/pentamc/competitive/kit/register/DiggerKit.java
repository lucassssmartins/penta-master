package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class DiggerKit extends DefaultKit {

	public DiggerKit() {
		super("digger", "Escave um grande buraco e diga adeus para seus oponentes", new ItemStack(Material.DRAGON_EGG),
				23000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("digger"));
	}

}
