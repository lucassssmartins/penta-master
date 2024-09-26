package br.com.pentamc.pvp.listener.server.gladiator;

import br.com.pentamc.bukkit.event.player.TeleportAllEvent;
import br.com.pentamc.bukkit.event.update.UpdateEvent;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.controller.GladiatorController;
import br.com.pentamc.pvp.event.death.PlayerKilledEvent;
import br.com.pentamc.pvp.kit.list.Gladiator;
import br.com.pentamc.pvp.kit.list.gladiator.GladiatorConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class GladiatorListener implements Listener {

    private boolean registered;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerKilledEvent event) {
        System.out.println("a");
        Player target = event.getTarget();
        System.out.println(target);

        if (GameMain.getPlugin().getGladiatorController().isInFight(target)) {
            for (ItemStack stack : target.getInventory().getContents()) {
                net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(stack);
                net.minecraft.server.v1_8_R3.NBTTagCompound compound = nmsItem.getTag();

                if (compound != null && compound.hasKey("cantDrop"))
                    continue;

                if (stack.getType().toString().contains("_SWORD"))
                    continue;

                target.getWorld().dropItemNaturally(target.getLocation(), stack);
            }

            GameMain.getPlugin().getGladiatorController().getGladiator(target).handleWin(target);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (GameMain.getPlugin().getGladiatorController().isInFight(player))
            GameMain.getPlugin().getGladiatorController().getGladiator(player).handleWin(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player player = (Player) event.getEntity();

            if (GameMain.getPlugin().getGladiatorController().isInFight(player)) {
                GladiatorConstructor gladiator = GameMain.getPlugin().getGladiatorController().getGladiator(player);

                if (gladiator.isInGladiator(damager)) {
                    event.setCancelled(false);
                } else
                    event.setCancelled(true);
            } else if (GameMain.getPlugin().getGladiatorController().isInFight(damager)) {
                GladiatorConstructor gladiator = GameMain.getPlugin().getGladiatorController().getGladiator(damager);

                if (gladiator.isInGladiator(player)) {
                    event.setCancelled(false);
                } else
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (GameMain.getPlugin().getGladiatorController().isInFight(player))
            GameMain.getPlugin().getGladiatorController().getGladiator(player).addBlock(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (GameMain.getPlugin().getGladiatorController().getBlockList().contains(event.getBlock())) {
            event.setCancelled(true);
            return;
        }

        if (GameMain.getPlugin().getGladiatorController().isInFight(player)) {
            GameMain.getPlugin().getGladiatorController().getGladiator(player).removeBlock(event.getBlock());
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockDamageEvent event) {
        if (GameMain.getPlugin().getGladiatorController().getBlockList().contains(event.getBlock())) {
            Player player = event.getPlayer();
            Block block = event.getBlock();

            player.sendBlockChange(block.getLocation(), Material.BEDROCK, (byte) 0);
            return;
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == UpdateEvent.UpdateType.SECOND) {
            GameMain.getPlugin().getGladiatorController().getGladiatorList().iterator().forEachRemaining(GladiatorConstructor::pulse);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (GameMain.getPlugin().getGladiatorController().isInFight(player)) {
            GladiatorConstructor gladiator = GameMain.getPlugin().getGladiatorController().getGladiator(player);

            if (event.getFrom().getY() - 190 > GameMain.getPlugin().getGladiatorController().getHeight())
                gladiator.handleEscape(true);
            else if (event.getFrom().getY() <= 190 - 2 && gladiator.getTime() > 2)
                gladiator.handleEscape(true);
        }
    }

    @EventHandler
    public void onTeleportAll(TeleportAllEvent event) {
        for (GladiatorConstructor gladiator : GameMain.getPlugin().getGladiatorController().getGladiatorList())
            gladiator.handleEscape(false);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        Iterator<Block> blockIt = event.blockList().iterator();

        while (blockIt.hasNext()) {
            Block b = (Block) blockIt.next();
            if (GameMain.getPlugin().getGladiatorController().getBlockList().contains(b)) {
                blockIt.remove();
            }
        }
    }

    public void register() {
        if (!registered) {
            Bukkit.getPluginManager().registerEvents(this, GameMain.getPlugin());
            registered = true;
        }
    }

    public void unregister() {
        if (registered) {
            HandlerList.unregisterAll(this);
            registered = false;
        }
    }
}