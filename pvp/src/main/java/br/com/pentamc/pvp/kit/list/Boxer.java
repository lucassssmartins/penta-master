package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Boxer extends Kit implements Listener {
    public Boxer() {
        super(
                "Acerte golpes certeiros em seus inimigos!",
                KitType.BOXER,
                Material.QUARTZ,
                0,
                Group.MEMBRO,
                0L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return null;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void damage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            User user = getUser(damager.getUniqueId());

            if (isAvailable(user))
                event.setDamage(event.getDamage() + 3.0D);
        }
    }
}
