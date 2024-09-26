package br.com.pentamc.gladiator.listener;

import net.minecraft.server.v1_8_R3.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void close(InventoryCloseEvent event) {
        Player human = (Player) event.getPlayer();

        if (human.getItemOnCursor() != null || !human.getItemOnCursor().getType().equals(Material.AIR)) {
            human.setItemOnCursor(null);

            human.updateInventory();
        }
    }
}
