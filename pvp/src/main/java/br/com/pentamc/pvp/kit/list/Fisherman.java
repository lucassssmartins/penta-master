package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Fisherman extends Kit implements Listener {

    public Fisherman() {
        super(
                "Pesque seus inimigos!",
                KitType.FISHERMAN,
                Material.FISHING_ROD,
                5000,
                Group.VIP,
                0L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return Arrays.asList(
                new ItemBuilder()
                        .type(Material.FISHING_ROD)
                        .name("§aKit Fisherman")
                        .build()
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void search(PlayerFishEvent event) {
        Player player = event.getPlayer();
        User user = getUser(player.getUniqueId());

        if (isAvailable(user) && player.getItemInHand().equals(getSpecialItem().get(0))) {
            if (event.getState().equals(PlayerFishEvent.State.CAUGHT_ENTITY)) {
                Player target = (Player) event.getCaught();

                World world = player.getLocation().getWorld();

                double
                        x = player.getLocation().getBlockX() + 0.5D,
                        y = player.getLocation().getBlockY(),
                        z = player.getLocation().getBlockZ() + 0.5D;

                float
                        yaw = target.getLocation().getYaw(),
                        pitch = target.getLocation().getPitch();

                Location location = new Location(world, x, y, z, yaw, pitch);

                target.teleport(location);
            }

            Bukkit.getScheduler().runTaskLater(GameMain.getPlugin(), () -> {
                player.getItemInHand().setDurability((short) 0);
            }, 1L);
        }
    }
}
