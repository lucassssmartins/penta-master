package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Switcher extends Kit implements Listener {

    public Switcher() {
        super(
                "Troque de lugar com seus inimigos!",
                KitType.SWITCHER,
                Material.SNOW_BALL,
                5000,
                Group.PENTA,
                2L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return Arrays.asList(
                new ItemBuilder()
                        .type(Material.SNOW_BALL)
                        .name("§aKit Switcher")
                        .build()
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void launch(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = getUser(player.getUniqueId());

        if (isAvailable(user) && player.getItemInHand().equals(getSpecialItem().get(0))) {
            event.setCancelled(true);
            player.updateInventory();

            if (isCooldown(player))
                return;

            Snowball snowball = player.launchProjectile(Snowball.class);

            snowball.setMetadata("switch", new FixedMetadataValue(GameMain.getPlugin(), player));
            addCooldown(player, getCooldown());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void teleport(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager().hasMetadata("switch")) {
            Player
                    player = (Player) event.getEntity(),
                    damager = (Player) event.getDamager().getMetadata("switch").get(0).value();

            User
                    user = getUser(player.getUniqueId()),
                    damagerUser = getUser(damager.getUniqueId());

            if (isAvailable(damagerUser)) {
                if (user.isProtected())
                    return;

                if (user.isUsingNeo()) {
                    damager.sendMessage("§cO jogador está usando o kit Neo.");
                    return;
                }

                Location
                        playerLocation = player.getLocation().clone(),
                        damagerLocation = damager.getLocation().clone();

                player.teleport(damagerLocation);
                damager.teleport(playerLocation);
            }
        }
    }
}