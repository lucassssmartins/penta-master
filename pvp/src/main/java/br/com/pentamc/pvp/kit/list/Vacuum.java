package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Vacuum extends Kit implements Listener {
    public Vacuum() {
        super(
                "Puxe todos os inimigos que estão ao seu redor para você!",
                KitType.VACUUM,
                Material.ENDER_PEARL,
                5000,
                Group.BETA,
                15L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return Arrays.asList(
                new ItemBuilder()
                        .type(Material.ENDER_PEARL)
                        .name("§aKit Vacuum")
                        .build()
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void push(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = getUser(player.getUniqueId());

        if (isAvailable(user) && player.getItemInHand().equals(getSpecialItem().get(0))) {
            event.setCancelled(true);
            player.updateInventory();

            if (isCooldown(player))
                return;

            for (Entity nearby : player.getNearbyEntities(12, 12, 12)) {
                if (nearby instanceof Player) {
                    Player entity = (Player) nearby;
                    User entityUser = getUser(entity.getUniqueId());

                    if (entityUser.isProtected())
                        continue;

                    Location location = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());

                    double
                            g = -0.12D,
                            d = location.distance(nearby.getLocation()),
                            t = d,
                            v_x = -(g - (location.getX() - nearby.getLocation().getX()) / t),
                            v_y = -(g - (location.getY() - nearby.getLocation().getY()) / t),
                            v_z = -(g - (location.getZ() - nearby.getLocation().getZ()) / t);

                    Vector vector = nearby.getVelocity();

                    vector.setX(v_x);
                    vector.setY(v_y);
                    vector.setZ(v_z);

                    nearby.setVelocity(vector);
                }
            }

            addCooldown(player, getCooldown());
        }
    }
}
