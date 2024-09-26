package br.com.pentamc.pvp.listener.server;

import br.com.pentamc.bukkit.api.scoreboard.Score;
import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.event.player.PlayerDamagePlayerEvent;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.pvp.PvPStatus;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.event.death.PlayerKilledEvent;
import br.com.pentamc.pvp.game.Game;
import br.com.pentamc.pvp.game.type.GameType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class StructureListener implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void damage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());

            event.setCancelled(user.isProtected());
        }
    }

    @EventHandler
    public void allowKit(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            Player player = (Player) event.getEntity();
            PlayerInventory inventory = player.getInventory();

            User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());
            Game game = user.getGame();

            if (game.getType().equals(GameType.FPS) && user.isProtected()) {
                event.setCancelled(true);
                inventory.clear();

                inventory.setHelmet(new ItemStack(Material.IRON_HELMET));
                inventory.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                inventory.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                inventory.setBoots(new ItemStack(Material.IRON_BOOTS));

                inventory.setItem(0, new ItemStack(Material.DIAMOND_SWORD));

                inventory.setItem(13, new ItemStack(Material.BOWL, 32));
                inventory.setItem(14, new ItemStack(Material.RED_MUSHROOM, 32));
                inventory.setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 32));

                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack item = inventory.getItem(i);

                    if (item != null)
                        continue;

                    inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                }

                user.setProtected(false);
            } else if (game.getType().equals(GameType.BATTLE) && user.isProtected()) {
                event.setCancelled(true);
                inventory.clear();

                inventory.setItem(0, new ItemStack(Material.STONE_SWORD));

                inventory.setItem(13, new ItemStack(Material.BOWL, 32));
                inventory.setItem(14, new ItemStack(Material.RED_MUSHROOM, 32));
                inventory.setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 32));

                if (user.getKitOne().getKit().getSpecialItem() != null)
                    for (ItemStack stack : user.getKitOne().getKit().getSpecialItem())
                        inventory.setItem(inventory.firstEmpty(), stack);

                if (user.getKitTwo().getKit().getSpecialItem() != null)
                    for (ItemStack stack : user.getKitTwo().getKit().getSpecialItem())
                        inventory.setItem(inventory.firstEmpty(), stack);


                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack item = inventory.getItem(i);

                    if (item != null)
                        continue;

                    inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                }

                user.setProtected(false);
            }
        }
    }

    @EventHandler
    public void damage(PlayerDamagePlayerEvent event) {
        Player
                player = event.getPlayer(),
                damager = event.getDamager();

        User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());
        Game game = user.getGame();

        GameMain.getPlugin().getCombatController().addCombat(player.getUniqueId(), damager.getUniqueId());

        if (event.getFinalDamage() >= player.getHealth()) {
            event.setCancelled(true);
            event.setDamage(0);

            System.out.println("chegou 2");

            GameMain.getPlugin().getCombatController().remove(player.getUniqueId());

            Game.handleDeath(player, damager);

            game.load(player);
            user.setProtected(true);
        }
    }

    @EventHandler
    public void death(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player
                    player = (Player) event.getEntity(),
                    target = null;

            if (event.getFinalDamage() >= player.getHealth()) {
                event.setCancelled(true);
                event.setDamage(0);

                System.out.println("chegou 3");

                User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());
                Game game = user.getGame();

                if (GameMain.getPlugin().getCombatController().read(player.getUniqueId()) != null) {
                    target = Bukkit.getPlayer(GameMain.getPlugin().getCombatController().read(player.getUniqueId()));

                    GameMain.getPlugin().getCombatController().remove(player.getUniqueId());
                }

                Game.handleDeath(player, target);

                game.load(player);
                user.setProtected(true);
            }
        }
    }

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
            }
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());

        ItemStack stackDropped = event.getItemDrop().getItemStack();

        event.setCancelled(user.getSpecialItems().stream().anyMatch(stack -> stack.equals(stackDropped)) || stackDropped.getType().toString().contains("_SWORD") || user.isProtected());
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

        event.setCancelled(!player.isBuildEnabled());
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

        event.setCancelled(!player.isBuildEnabled());
    }

    @EventHandler
    public void food(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}
