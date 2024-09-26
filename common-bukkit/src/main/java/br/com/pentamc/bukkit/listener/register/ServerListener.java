package br.com.pentamc.bukkit.listener.register;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.event.update.UpdateEvent;
import br.com.pentamc.bukkit.listener.Listener;
import br.com.pentamc.common.CommonGeneral;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import redis.clients.jedis.Jedis;

public class ServerListener extends Listener {

    @EventHandler
    public void competitiveAnnounce(UpdateEvent event) {
        if (event.getType().equals(UpdateEvent.UpdateType.SECOND)) {
            try (Jedis jedis = BukkitMain.getInstance().getRedis().getPool().getResource()) {
                if (jedis.exists("competitive:time")) {
                    String message = jedis.get("competitive:time");

                    if (message != null)
                        CommonGeneral.getInstance().getMemberManager().getMembers().forEach(member -> {
                            Player player = Bukkit.getPlayer(member.getUniqueId());

                            if (player != null) {
                                player.sendMessage(message);

                                player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 100, 100);
                            }
                        });
                }
            }
        }
    }
}
