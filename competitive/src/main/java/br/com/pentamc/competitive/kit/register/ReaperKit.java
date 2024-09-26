package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class ReaperKit extends DefaultKit {

	public ReaperKit() {
		super("reaper", "Use sua enxada para deixar seu inimigo com wither", new ItemStack(Material.WOOD_HOE), 22500);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("reaper"));
	}

}
