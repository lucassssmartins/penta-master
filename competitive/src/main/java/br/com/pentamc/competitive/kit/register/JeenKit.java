package br.com.pentamc.competitive.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;

public class JeenKit extends DefaultKit {

	public JeenKit() {
		super("jeen", "Fique invisivel por 5 segundos e de um dash para frente", new ItemStack(Material.BLAZE_POWDER),
				24000, Arrays.asList(GladiatorKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("jeen"));
	}

}
