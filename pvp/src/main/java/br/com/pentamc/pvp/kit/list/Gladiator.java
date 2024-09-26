package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.event.ChallengeGladiatorEvent;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Gladiator extends Kit implements Listener {

    protected final List<Block> blocks;

    public Gladiator() {
        super(
                "Desafie seus inimigos para um duelo de gladiadores!",
                KitType.GLADIATOR,
                Material.IRON_FENCE,
                5000,
                Group.PENTA,
                0L
        );

        blocks = new ArrayList<>();
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return Arrays.asList(
                new ItemBuilder()
                        .type(Material.IRON_FENCE)
                        .name("§aKit Gladiator")
                        .build()
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void call(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked() instanceof Player) {
            Player target = (Player) event.getRightClicked();

            User
                    playerUser = getUser(player.getUniqueId()),
                    targetUser = getUser(target.getUniqueId());

            if (isAvailable(playerUser) && player.getItemInHand().equals(getSpecialItem().get(0))) {
                if (targetUser.isUsingNeo()) {
                    player.sendMessage("§cO jogador está usando o kit Neo.");
                    return;
                }

                ChallengeGladiatorEvent challengeGladiatorEvent = new ChallengeGladiatorEvent(player, target);

                challengeGladiatorEvent.setCancelled(!(!GameMain.getPlugin().getGladiatorController().isInFight(player) && !GameMain.getPlugin().getGladiatorController().isInFight(target)));
                Bukkit.getPluginManager().callEvent(challengeGladiatorEvent);

                if (!challengeGladiatorEvent.isCancelled())
                    GameMain.getPlugin().getGladiatorController().sendGladiator(player, target);
            }
        }
    }
}
