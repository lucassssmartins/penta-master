package br.com.pentamc.bukkit.api.menu.click;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface MenuClickHandler {
	boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot);
}
