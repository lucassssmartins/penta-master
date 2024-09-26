package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class IronmanKit extends DefaultKit {

	public IronmanKit() {
		super("ironman", "Receba ferros quando matar um jogador", new ItemStack(Material.IRON_INGOT), 25000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("ironman"));
	}

}
