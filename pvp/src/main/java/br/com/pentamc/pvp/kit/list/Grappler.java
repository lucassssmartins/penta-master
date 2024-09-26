package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import br.com.pentamc.pvp.util.HookUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class Grappler extends Kit implements Listener {

    protected final Map<UUID, HookUtil> hooks;

    public Grappler() {
        super(
                "Agarre-se em seus inimigos e não os solte!",
                KitType.GRAPPLER,
                Material.LEASH,
                5000,
                Group.PENTA,
                6L
        );

        hooks = new HashMap<>();
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return Arrays.asList(
                new ItemBuilder()
                        .type(Material.LEASH)
                        .name("§aKit Grappler")
                        .build()
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void search(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = getUser(player.getUniqueId());

        if (isAvailable(user) && player.getItemInHand().equals(getSpecialItem().get(0))) {
            event.setCancelled(true);

            if (isCooldown(player))
                return;

            Location location = player.getLocation();

            if (event.getAction().toString().contains("LEFT_")) {
                if (hooks.containsKey(player.getUniqueId()))
                    hooks.get(player.getUniqueId()).remove();

                Vector direction = location.getDirection();
                HookUtil nms = new HookUtil(player.getWorld(), ((CraftPlayer) player).getHandle());

                nms.spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()));
                nms.move(5 * direction.getX(), 5 * direction.getY(), 5 * direction.getZ());

                hooks.put(player.getUniqueId(), nms);
            } else if (hooks.containsKey(player.getUniqueId()) && hooks.get(player.getUniqueId()).isHooked()) {
                Location location2 = hooks.get(player.getUniqueId()).getBukkitEntity().getLocation();

                double
                        distance = location2.distance(location),
                        vectorX = (1 + 0.07 * distance) * (location2.getX() - location.getX()) / distance,
                        vectorY = (1 + 0.03 * distance) * (location2.getY() - location.getY()) / distance,
                        vectorZ = (1 + 0.07 * distance) * (location2.getZ() - location.getZ()) / distance;

                player.setVelocity(new Vector(vectorX, vectorY, vectorZ));
                player.setFallDistance(0.0F);
            }
        }
    }

    @EventHandler
    public void lock(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            User user = getUser(player.getUniqueId());

            if (isAvailable(user))
                addCooldown(player, getCooldown());
        }
    }

    @EventHandler
    private void remove(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if (hooks.containsKey(player.getUniqueId())) {
            hooks.get(player.getUniqueId()).remove();
            hooks.remove(player.getUniqueId());
        }
    }

    @EventHandler
    private void remove(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (hooks.containsKey(player.getUniqueId())) {
            hooks.get(player.getUniqueId()).remove();
            hooks.remove(player.getUniqueId());
        }
    }

    @EventHandler
    private void remove(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (hooks.containsKey(player.getUniqueId())) {
            hooks.get(player.getUniqueId()).remove();
            hooks.remove(player.getUniqueId());
        }
    }
}
