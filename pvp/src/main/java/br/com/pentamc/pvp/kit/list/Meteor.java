package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class Meteor extends Kit implements Listener {

    protected final List<UUID>
            using = new ArrayList<>(),
            active = new ArrayList<>();

    public Meteor() {
        super(
                "Ascenda igual um meteoro!",
                KitType.METEOR,
                Material.FIREBALL,
                5000,
                Group.BETA,
                15L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return Arrays.asList(
                new ItemBuilder()
                        .type(Material.FIREBALL)
                        .name("§aKit Meteor")
                        .build()
        );
    }

    @EventHandler
    public void active(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = getUser(player.getUniqueId());

        if (isAvailable(user) && player.getItemInHand().equals(getSpecialItem().get(0))) {
            event.setCancelled(true);
            player.updateInventory();

            if (isCooldown(player))
                return;

            Vector vector = new Vector(0, 2, 0);

            player.setVelocity(vector);

            using.add(player.getUniqueId());
            active.add(player.getUniqueId());

            addCooldown(player, getCooldown());
        }
    }

    @EventHandler
    public void boost(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        User user = getUser(player.getUniqueId());

        if (isAvailable(user) && active.contains(player.getUniqueId())) {
            Vector vector = player.getLocation().getDirection().multiply(4).setY(-5);

            player.setVelocity(vector);
            active.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void disallow(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            Player player = (Player) event.getEntity();
            User user = getUser(player.getUniqueId());

            if (isAvailable(user) && using.contains(player.getUniqueId())) {
                event.setDamage(0);

                for (Entity nearby : player.getNearbyEntities(5, 5, 5)) {
                    if (nearby instanceof Player) {
                        Player nearbyPlayer = (Player) nearby;

                        nearbyPlayer.getWorld().strikeLightning(nearbyPlayer.getLocation());
                    }
                }

                using.remove(player.getUniqueId());
            }
        }
    }
}
