package br.com.pentamc.shadow.listener;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.permission.Group;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import br.com.pentamc.bukkit.bukkit.BukkitMember;

public class WorldListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		Item drop = event.getItemDrop();
		ItemStack item = drop.getItemStack();

		if (item.toString().contains("SWORD") || item.toString().contains("AXE")) {
			event.setCancelled(true);
			return;
		}

		event.getItemDrop().remove();
	}

	@EventHandler
	public void onItemPickUp(PlayerPickupItemEvent event) {
		ItemStack item = event.getItem().getItemStack();

		if (item.getItemMeta().hasDisplayName()) {
			event.setCancelled(true);
			return;
		}

		if (item.getType().toString().contains("SWORD") || item.getType().toString().contains("AXE")) {
			event.setCancelled(true);
			return;
		}

		if (item.getType().toString().contains("HELMET") || item.getType().toString().contains("CHESTPLATE")
			|| item.getType().toString().contains("LEGGING") || item.getType().toString().contains("BOOTS")) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.CUSTOM)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onCreatureSpawnChange(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.CUSTOM)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent e) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
														  .getMember(e.getPlayer().getUniqueId());

		if (player.isBuildEnabled())
			if (player.hasGroupPermission(Group.ADMIN)) {
				e.setCancelled(false);
				return;
			}

		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent e) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
														  .getMember(e.getPlayer().getUniqueId());

		if (player.isBuildEnabled())
			if (player.hasGroupPermission(Group.ADMIN)) {
				e.setCancelled(false);
				return;
			}

		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(PlayerBucketEmptyEvent e) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
														  .getMember(e.getPlayer().getUniqueId());

		if (player.isBuildEnabled())
			if (player.hasGroupPermission(Group.ADMIN)) {
				e.setCancelled(false);
				return;
			}

		e.setCancelled(true);
	}
}
