package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Archer extends Kit implements Listener {
    public Archer() {
        super(
                "Torne-se um arqueiro e domine seus inimigos!",
                KitType.ARCHER,
                Material.BOW,
                1550,
                Group.VIP,
                0L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return Arrays.asList(
                new ItemBuilder().type(Material.BOW).name("§aKit Archer").build(),
                new ItemStack(Material.ARROW)
        );
    }

    @EventHandler
    public void launch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();

            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                User user = getUser(player.getUniqueId());

                if (isAvailable(user) && player.getItemInHand().equals(getSpecialItem().get(0)))
                    Bukkit.getScheduler().runTaskLater(GameMain.getPlugin(), () -> player.getItemInHand().setDurability((short) 0), 1L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void shoot(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof  Player && event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            Player damager = (Player) arrow.getShooter();

            User user = getUser(damager.getUniqueId());

            if (isAvailable(user) && damager.isOnline()) {
                damager.getInventory().addItem(new ItemStack(Material.ARROW));

                Bukkit.getScheduler().runTaskLater(GameMain.getPlugin(), () -> {
                    damager.getItemInHand().setDurability((short) 0);
                }, 1L);
            }
        }
    }
}
