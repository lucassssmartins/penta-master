package br.com.pentamc.competitive.kit.register;

import java.util.Arrays;

import org.bukkit.Material;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;
import br.com.pentamc.bukkit.api.item.ItemBuilder;

public class BlinkKit extends DefaultKit {

	public BlinkKit() {
		super("blink", "Teletransporte-se para onde você estiver olhando",
				new ItemBuilder().type(Material.NETHER_STAR).build(), 13000,
				Arrays.asList(AjninKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("blink"));
	}

}
