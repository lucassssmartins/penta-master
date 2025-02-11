package br.com.pentamc.bukkit.listener.register;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.bukkit.listener.Listener;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_8_R3.ContainerEnchantTable;
import br.com.pentamc.bukkit.api.item.ItemBuilder;

public class EnchantListener extends Listener {

	public static final ItemStack LAPIS = new ItemBuilder().name("§aLapis Lazuli").durability(4).type(Material.INK_SACK)
			.amount(3).build();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEnchantItem(EnchantItemEvent event) {
		if (event.getInventory() instanceof EnchantingInventory)
			((EnchantingInventory) event.getInventory()).setSecondary(LAPIS);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getInventory() instanceof EnchantingInventory)
			((EnchantingInventory) event.getInventory()).setSecondary(LAPIS);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory() instanceof EnchantingInventory)
			if (event.getRawSlot() == 1)
				event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemSpawn(ItemSpawnEvent event) {
		if (event.getEntity().getType() == EntityType.DROPPED_ITEM) {
			Item item = event.getEntity();

			if (item.getItemStack().getType() == Material.INK_SACK)
				if (item.getItemStack().getDurability() == 4) {
					ItemStack itemStack = item.getItemStack();

					if (itemStack.hasItemMeta())
						if (itemStack.getItemMeta().hasDisplayName())
							if (itemStack.getItemMeta().getDisplayName().equalsIgnoreCase("§aLapis Lazuli")) {
								event.getEntity().remove();
							}
				}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (event.getItemDrop().getType() == EntityType.DROPPED_ITEM) {
			Item item = event.getItemDrop();

			if (item.getItemStack().getType() == Material.INK_SACK)
				if (item.getItemStack().getDurability() == 4) {
					ItemStack itemStack = item.getItemStack();

					if (itemStack.hasItemMeta())
						if (itemStack.getItemMeta().hasDisplayName())
							if (itemStack.getItemMeta().getDisplayName().equalsIgnoreCase("§aLapis Lazuli")) {
								event.setCancelled(false);
							}
				}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
		CraftInventoryView view = (CraftInventoryView) event.getView();
		ContainerEnchantTable table = (ContainerEnchantTable) view.getHandle();

		for (int x = 0; x < table.costs.length; x++) {
			int cost = table.costs[x];

			if (cost <= 4)
				table.costs[x] = CommonConst.RANDOM.nextInt(4) + 1;
		}
	}

}
