package br.com.pentamc.competitive.listener.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SoupListener implements Listener {

    @EventHandler
    public void soup(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (event.getAction().toString().contains("RIGHT_") && item != null && item.getType().equals(Material.MUSHROOM_SOUP)) {
            event.setCancelled(true);

            double
                    healthCurrent = player.getHealth(),
                    healthMax = player.getMaxHealth();

            if (healthCurrent < healthMax) {
                player.setItemInHand(new ItemStack(Material.BOWL));
                player.setHealth(Math.min(healthMax, healthCurrent + 7));
            } else if (player.getFoodLevel() < 20) {
                player.setFoodLevel(Math.min(20, player.getFoodLevel() + 7));
            }
        }
    }
}
