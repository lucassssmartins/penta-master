package br.com.pentamc.competitive.kit.register;

import java.util.Arrays;

import org.bukkit.Material;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;
import br.com.pentamc.bukkit.api.item.ItemBuilder;

public class MagmaKit extends DefaultKit {

	public MagmaKit() {
		super("magma", "Tenha 33% de chance de colocar fogo em quem você bater",
				new ItemBuilder().type(Material.LAVA_BUCKET).build(), 29000,
				Arrays.asList(AjninKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("magma"));
	}

}
