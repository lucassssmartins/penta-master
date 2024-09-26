package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;
import br.com.pentamc.bukkit.api.item.ItemBuilder;

public class MonkKit extends DefaultKit {

	public MonkKit() {
		super("monk", "Desarme seu inimigo usando seu Blaze Rod", new ItemBuilder().type(Material.BLAZE_ROD).build(), 23000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("monk"));
	}

}
