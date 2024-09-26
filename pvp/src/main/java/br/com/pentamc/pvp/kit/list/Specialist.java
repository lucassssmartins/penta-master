package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.event.player.PlayerDamagePlayerEvent;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.event.death.PlayerKilledEvent;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Specialist extends Kit implements Listener {
    public Specialist() {
        super(
                "Encante seus itens com seu livro mágico!",
                KitType.SPECIALIST,
                Material.BOOK,
                5000,
                Group.PENTA,
                0L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return Arrays.asList(
                new ItemBuilder()
                        .type(Material.BOOK)
                        .name("§aKit Specialist")
                        .build()
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void open(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = getUser(player.getUniqueId());

        if (isAvailable(user) && player.getItemInHand().equals(getSpecialItem().get(0)) && event.getAction().name().contains("RIGHT")) {
            event.setCancelled(true);

            Block block = new Location(player.getLocation().getWorld(), 501, 0, 500).getBlock();

            block.setType(Material.ENCHANTMENT_TABLE);

            player.openEnchanting(block.getLocation(), true);
            player.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);
        }
    }

    @EventHandler
    public void experience(PlayerKilledEvent event) {
        Player
                player = event.getTarget(),
                killer = event.getKiller();

        if (killer != null) {
            User user = getUser(killer.getUniqueId());

            if (isAvailable(user)) {
                killer.setLevel(killer.getLevel() + 1);
                killer.playSound(killer.getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);
            }
        }
    }
}
