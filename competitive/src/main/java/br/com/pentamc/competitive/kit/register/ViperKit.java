package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class ViperKit extends DefaultKit {

	public ViperKit() {
		super("viper", "Deixe seus inimigos envenenados", new ItemStack(Material.SPIDER_EYE), 24500);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("viper"));
	}

}
