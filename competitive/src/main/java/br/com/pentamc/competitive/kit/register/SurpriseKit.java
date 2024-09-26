package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class SurpriseKit extends DefaultKit {

	public SurpriseKit() {
		super("surprise", "Selecione um kit aleatório no inicio da partida", new ItemStack(Material.CAKE), 10000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("surprise"));
	}

}
