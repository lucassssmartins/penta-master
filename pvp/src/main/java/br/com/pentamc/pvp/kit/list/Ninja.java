package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Ninja extends Kit implements Listener {

    protected final Map<UUID, UUID> targetMap;

    public Ninja() {
        super(
                "Teleporte-se para trás de seus inimigos!",
                KitType.NINJA,
                Material.EMERALD,
                5000,
                Group.VIP,
                6L
        );

        targetMap = new HashMap<>();
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return null;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void check(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player
                    player = (Player) event.getEntity(),
                    damager = (Player) event.getDamager();

            User damagerUser = getUser(damager.getUniqueId());

            if (isAvailable(damagerUser)) {
                if (targetMap.containsKey(damager.getUniqueId()))
                    targetMap.replace(damager.getUniqueId(), player.getUniqueId());

                targetMap.put(damager.getUniqueId(), player.getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void action(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        User user = getUser(player.getUniqueId());

        if (event.isSneaking()) {
            if (isAvailable(user)) {
                if (isCooldown(player))
                    return;

                if (targetMap.containsKey(player.getUniqueId())) {
                    UUID targetId = targetMap.get(player.getUniqueId());

                    if (targetId != null) {
                        Player target = Bukkit.getPlayer(targetId);
                        User targetUser = getUser(targetId);

                        if (targetUser.isProtected()) {
                            targetMap.remove(player.getUniqueId());
                            return;
                        }

                        if (player.getLocation().distance(target.getLocation()) >= 30) {
                            player.sendMessage("§cVocê não pode teleportar-se para um jogador que está muito longe.");
                            return;
                        }

                        if (targetUser.isUsingNeo()) {
                            player.sendMessage("§cO jogador está usando o kit Neo.");
                            return;
                        }

                        player.teleport(target.getLocation());
                        targetMap.remove(player.getUniqueId());
                        addCooldown(player, getCooldown());
                    }
                }
            }
        }
    }
}
