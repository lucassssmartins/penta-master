package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Thor extends Kit implements Listener {
    public Thor() {
        super(
                "Atinja seus inimigos com um poderoso trovão!",
                KitType.THOR,
                Material.GOLD_AXE,
                2500,
                Group.VIP,
                5L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return Arrays.asList(
                new ItemBuilder()
                        .type(Material.GOLD_AXE)
                        .name("§aKit Thor")
                        .build()
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void invoke(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = getUser(player.getUniqueId());

        if (isAvailable(user) && player.getItemInHand().equals(getSpecialItem().get(0))) {
            if (isCooldown(player))
                return;

            player.getWorld().strikeLightning(player.getTargetBlock((HashSet<Byte>) null, 20).getLocation());
            addCooldown(player, getCooldown());

            Bukkit.getScheduler().runTaskLater(GameMain.getPlugin(), () -> {
                player.getItemInHand().setDurability((short) 0);
            }, 1L);
        }
    }
}
