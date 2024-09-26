package br.com.pentamc.gladiator.listener;

import br.com.pentamc.gladiator.GameMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import br.com.pentamc.gladiator.gamer.Gamer;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener {

    public PlayerListener() {
        ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.MUSHROOM_SOUP));

        recipe.addIngredient(new MaterialData(Material.INK_SACK, (byte) 3));
        recipe.addIngredient(new MaterialData(Material.BOWL));

        Bukkit.addRecipe(recipe);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerLoginEvent event) {
        if (event.getResult() != Result.ALLOWED)
            return;

        Player player = event.getPlayer();
        Gamer gamer = new Gamer(player);

        GameMain.getInstance().getGamerManager().loadGamer(player.getUniqueId(), gamer);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GameMain.getInstance().getGamerManager().unloadGamer(event.getPlayer().getUniqueId());
        event.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR)
            return;
        if (item.getType() == Material.MUSHROOM_SOUP) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (p.getHealth() < p.getMaxHealth() || p.getFoodLevel() < 20) {
                    int restores = 7;
                    event.setCancelled(true);
                    if (p.getHealth() < p.getMaxHealth())
                        if (p.getHealth() + restores <= p.getMaxHealth())
                            p.setHealth(p.getHealth() + restores);
                        else
                            p.setHealth(p.getMaxHealth());
                    else if (p.getFoodLevel() < 20)
                        if (p.getFoodLevel() + restores <= 20) {
                            p.setFoodLevel(p.getFoodLevel() + restores);
                            p.setSaturation(3);
                        } else {
                            p.setFoodLevel(20);
                            p.setSaturation(3);
                        }
                    item = new ItemStack(Material.BOWL);
                    p.setItemInHand(item);
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;

        Player p = (Player) event.getDamager();

        if (p.getItemInHand() != null && p.getItemInHand().getType().name().contains("SWORD"))
            p.getItemInHand().setDurability((short) 0);
    }

}
