package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.game.Game;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Stomper extends Kit implements Listener {
    public Stomper() {
        super(
                "Esmaque seus inimigos!",
                KitType.STOMPER,
                Material.DIAMOND_BOOTS,
                5000,
                Group.PENTA,
                0L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void stomp(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            User user = getUser(player.getUniqueId());

            if (isAvailable(user) && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                double damage = event.getDamage();
                System.out.println("chegou 1");

                event.setCancelled(true);
                event.setDamage(0);

                player.damage(2D);

                for (Entity nearby : player.getNearbyEntities(5, 5, 5)) {
                    if (nearby instanceof Player) {
                        Player target = (Player) nearby;
                        User targetUser = getUser(target.getUniqueId());

                        if (targetUser.isProtected())
                            continue;

                        if (targetUser.isUsing(KitType.ANTISTOMPER))
                            continue;

                        if (damage >= target.getHealth()) {
                            Game.handleDeath(target, player);

                            Game game = targetUser.getGame();

                            game.load(target);
                            targetUser.setProtected(true);
                        } else {
                            player.damage(damage);
                        }
                    }
                }
            }
        }
    }
}
