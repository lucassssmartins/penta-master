package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Viper extends Kit implements Listener {
    public Viper() {
        super(
                "Envenene seus inimigos!",
                KitType.VIPER,
                Material.SPIDER_EYE,
                2500,
                Group.VIP,
                0L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return null;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void damage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player
                    entity = (Player) event.getEntity(),
                    damager = (Player) event.getDamager();

            User damagerUser = getUser(damager.getUniqueId());

            if (isAvailable(damagerUser)) {
                if (new Random().nextInt(100) <= 40) {
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 0));

                    Bukkit.getOnlinePlayers().forEach(players -> {
                        if (entity.getLocation().distance(players.getLocation()) <= 4.0)
                            players.playSound(players.getLocation(), Sound.SPIDER_IDLE, 1.0F, 1.0F);
                    });
                }
            }
        }
    }
}
