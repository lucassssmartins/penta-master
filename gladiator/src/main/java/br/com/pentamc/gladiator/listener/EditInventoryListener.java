package br.com.pentamc.gladiator.listener;

import br.com.pentamc.gladiator.GameMain;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class EditInventoryListener implements Listener {

    private Player player;
    private PacketAdapter craftAdapter;

    public EditInventoryListener(Player player) {
        this.player = player;
    }

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                craftAdapter = new PacketAdapter(GameMain.getInstance(),
                        ListenerPriority.HIGHEST, PacketType.Play.Client.WINDOW_CLICK) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (player == null || !player.isOnline())
                            return;
                        if (!player.getUniqueId().equals(event.getPlayer().getUniqueId()))
                            return;
                        // Check if player interacted with their inventory
                        PacketContainer packet = event.getPacket();
                        int windowId = packet.getIntegers().read(0);

                        if (windowId == 0) { // Make sure it's the player's inventory window
                            int slot = packet.getIntegers().read(1);

                            if (slot >= 1 && slot <= 4) {
                                event.setCancelled(true);
                            }
                        }
                    }
                });
        Bukkit.getServer().getPluginManager().registerEvents(this, GameMain.getInstance());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer().getUniqueId() == player.getUniqueId()) {
            destroy();
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent event) {
        if (event.getPlayer().getUniqueId() != player.getUniqueId())
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent event) {
        if (event.getPlayer().getUniqueId() != player.getUniqueId())
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getUniqueId() != player.getUniqueId()) {
            return;
        }

        int rawSlot = event.getRawSlot();
        if (rawSlot == 1 || rawSlot == 2 || rawSlot == 3 || rawSlot == 4) {
            event.setCancelled(true);
            return;
        }
        if (event.getClickedInventory() == player.getInventory()) {
            if (rawSlot == 5 || rawSlot == 6 || rawSlot == 7 || rawSlot == 8) {
                event.setCancelled(true);
                return;
            }
            if (event.isRightClick()) {
                event.setCancelled(true);
            }
        }

        if (!event.isCancelled()) {
            ItemStack current = event.getCurrentItem();
            if (current != null) {

            }
        }
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
        ProtocolLibrary.getProtocolManager().removePacketListener(craftAdapter);
        craftAdapter = null;
        player = null;
    }
}