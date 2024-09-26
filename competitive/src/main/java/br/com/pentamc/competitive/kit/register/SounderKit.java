package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class SounderKit extends DefaultKit {

	public SounderKit() {
		super("sounder", "Deixe seus inimigos mais lerdos", new ItemStack(Material.NOTE_BLOCK), 26500);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("sounder"));
	}

}
