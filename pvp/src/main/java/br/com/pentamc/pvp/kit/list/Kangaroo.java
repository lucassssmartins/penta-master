package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class Kangaroo extends Kit implements Listener {

    protected final List<UUID> using;

    public Kangaroo() {
        super(
                "Pule como um canguru!",
                KitType.KANGAROO,
                Material.FIREWORK,
                5000,
                Group.PENTA,
                6L
        );

        using = new ArrayList<>();
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return Arrays.asList(
                new ItemBuilder()
                        .type(Material.FIREWORK)
                        .name("§aKit Kangaroo")
                        .build()
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void boost(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = getUser(player.getUniqueId());

        if (isAvailable(user) && player.getItemInHand().equals(getSpecialItem().get(0))) {
            event.setCancelled(true);

            if (isCooldown(player))
                return;

            if (!using.contains(player.getUniqueId())) {
                Vector vector = player.getEyeLocation().getDirection();

                if (player.isSneaking()) {
                    vector = vector.multiply(1.5F).setY(0.5F);
                } else {
                    vector = vector.multiply(0.5F).setY(1F);
                }

                player.setVelocity(vector);
                using.add(player.getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void unlock(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (using.contains(player.getUniqueId()) && (!player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR) || player.isOnGround()))
            using.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void lock(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player
                    player = (Player) event.getEntity(),
                    damager = (Player) event.getDamager();

            User user = getUser(player.getUniqueId());

            if (isAvailable(user))
                addCooldown(player, getCooldown());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void reducer(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            User user = getUser(player.getUniqueId());

            if (isAvailable(user) && event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && event.getDamage() > 7.0)
                event.setDamage(7.0);
        }
    }
}
