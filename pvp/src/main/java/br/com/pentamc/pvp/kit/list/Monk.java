package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Monk extends Kit implements Listener {
    public Monk() {
        super(
                "Embaralhe o inventário de seus inimigos!",
                KitType.MONK,
                Material.BLAZE_ROD,
                2500,
                Group.VIP,
                10L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return Arrays.asList(
                new ItemBuilder()
                        .type(Material.BLAZE_ROD)
                        .name("§aKit Monk")
                        .build()
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void action(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            Player
                    player = event.getPlayer(),
                    target = (Player) event.getRightClicked();

            User
                    playerUser = getUser(player.getUniqueId()),
                    targetUser = getUser(target.getUniqueId());

            if (isAvailable(playerUser) && player.getItemInHand().equals(getSpecialItem().get(0))) {
                if (targetUser.isUsingNeo()) {
                    player.sendMessage("§cO jogador está usando o kit Neo.");
                    return;
                }

                if (isCooldown(player))
                    return;

                int position = new Random().nextInt(36);

                ItemStack
                        actual = (target.getItemInHand() != null ? target.getItemInHand().clone() : null),
                        random = (target.getInventory().getItem(position) != null ? target.getInventory().getItem(position).clone() : null);

                if (random == null) {
                    target.getInventory().setItem(position, actual);
                    target.setItemInHand(null);
                } else {
                    target.getInventory().setItem(position, actual);
                    target.getInventory().setItemInHand(random);
                }

                addCooldown(player, getCooldown());
            }
        }
    }
}
