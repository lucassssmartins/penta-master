package br.com.pentamc.bukkit.api.menu.types;

import br.com.pentamc.bukkit.api.menu.MenuInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.click.ClickType;
import br.com.pentamc.bukkit.api.menu.click.MenuClickHandler;

public class ConfirmInventory {

	public ConfirmInventory(Player player, String confirmTitle, ConfirmHandler handler, MenuInventory topInventory) {
		MenuInventory menu = new MenuInventory(confirmTitle, 4);

		MenuClickHandler confirm = new MenuClickHandler() {

			@Override
			public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
				handler.onConfirm(true);
				return false;
			}
		};

		MenuClickHandler noConfirm = new MenuClickHandler() {

			@Override
			public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
				handler.onConfirm(false);

				if (topInventory != null)
					topInventory.open(p);
				else
					menu.close(p);

				return false;
			}
		};

		menu.setItem(10, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14).name("§cRejeitar").build(),
				noConfirm);
		menu.setItem(11, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14).name("§cRejeitar").build(),
				noConfirm);
		menu.setItem(19, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14).name("§cRejeitar").build(),
				noConfirm);
		menu.setItem(20, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14).name("§cRejeitar").build(),
				noConfirm);

		menu.setItem(15, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(5).name("§aAceitar").build(),
				confirm);
		menu.setItem(16, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(5).name("§aAceitar").build(),
				confirm);
		menu.setItem(24, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(5).name("§aAceitar").build(),
				confirm);
		menu.setItem(25, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(5).name("§aAceitar").build(),
				confirm);

		menu.open(player);
	}

	public static interface ConfirmHandler {

		public void onConfirm(boolean confirmed);

	}
}
