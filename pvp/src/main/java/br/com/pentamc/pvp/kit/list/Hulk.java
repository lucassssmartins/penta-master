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
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class Hulk extends Kit implements Listener {
    public Hulk() {
        super(
                "Carregue seus inimigos em seu ombro!",
                KitType.HULK,
                Material.DISPENSER,
                2500,
                Group.VIP,
                10L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return null;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void take(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            Player
                    player = event.getPlayer(),
                    target = (Player) event.getRightClicked();

            User
                    playerUser = getUser(player.getUniqueId()),
                    targetUser = getUser(target.getUniqueId());

            if (isAvailable(playerUser) && player.getItemInHand().getType().equals(Material.AIR) && player.getPassenger() == null) {
                if (targetUser.isUsingNeo()) {
                    player.sendMessage("§cO jogador está usando o kit Neo.");
                    return;
                }

                if (isCooldown(player))
                    return;

                player.setPassenger(target);
                addCooldown(player, getCooldown());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void release(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = getUser(player.getUniqueId());

        if (isAvailable(user) && player.getPassenger() != null && player.getItemInHand().getType().equals(Material.AIR) && event.getAction().toString().contains("LEFT_")) {
            Player target = (Player) player.getPassenger();
            Vector vector = player.getLocation().getDirection().multiply(1.5D).setY(1.0D);

            player.getPassenger().leaveVehicle();
            target.setVelocity(vector);
        }
    }
}
