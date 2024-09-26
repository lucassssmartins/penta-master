package br.com.pentamc.competitive.kit;

import java.util.Collection;

import br.com.pentamc.competitive.abilities.Ability;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Kit {

	String getName();

	String getDescription();

	ItemStack getKitIcon();

	Collection<Ability> getAbilities();

	void registerAbilities(Player player);

	int getPrice();

	boolean isNotCompatible(Class<? extends Kit> kitClazz);
	
	default boolean isNotCompatible(Kit kit) {
		return isNotCompatible(kit.getClass());
	}

}
