package br.com.pentamc.competitive.kit.register;

import org.bukkit.Material;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.kit.DefaultKit;
import br.com.pentamc.bukkit.api.item.ItemBuilder;

public class NeoKit extends DefaultKit {

	public NeoKit() {
		super("neo", "Reflita projéteis e não seja afetado pelos kits Gladiator, Ninja, Ajnin, Endermage e Ultimato",
				new ItemBuilder().type(Material.ARROW).build(), 23000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("neo"));
	}

}
