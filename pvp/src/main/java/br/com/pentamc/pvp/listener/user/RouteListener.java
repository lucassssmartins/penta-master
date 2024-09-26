package br.com.pentamc.pvp.listener.user;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.game.Game;
import br.com.pentamc.pvp.game.type.GameType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.UUID;

public class RouteListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public synchronized void registry(AsyncPlayerPreLoginEvent event) {
        UUID uniqueId = event.getUniqueId();
        String name = event.getName();

        try (Jedis jedis = BukkitMain.getInstance().getRedis().getPool().getResource()) {
            User user = new User(uniqueId);
            String route = jedis.get("route$" + uniqueId.toString());

            GameType type = Arrays.stream(GameType.values()).filter(types -> types.name().equalsIgnoreCase(route)).findFirst().orElse(null);
            Game game = GameMain.getPlugin().getGameController().getValue(type);
            
            if (game == null) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cJogo não encontrado.");
                return;
            }

            user.setGame(game);

            GameMain.getPlugin().getUserController().load(uniqueId, user);
        } catch (Exception ex) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cOcorreu um erro ao calcular sua rota.");
            ex.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public synchronized void entry(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());

        try (Jedis jedis = BukkitMain.getInstance().getRedis().getPool().getResource()) {
            String route = "route:" + user.getGame().getType().name().toLowerCase() + "-count";
            int actualCount = 0;

            if (jedis.exists(route)) {
                actualCount = Integer.parseInt(jedis.get(route));

                jedis.del(route);
            }

            jedis.set(route, String.valueOf(actualCount + 1));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public synchronized void quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());

        try (Jedis jedis = BukkitMain.getInstance().getRedis().getPool().getResource()) {
            String route = "route:" + user.getGame().getType().name().toLowerCase() + "-count";
            int actualCount = 0;

            if (jedis.exists(route)) {
                actualCount = Integer.parseInt(jedis.get(route));

                jedis.del(route);
            }

            jedis.set(route, String.valueOf(actualCount - 1));
        }

        GameMain.getPlugin().getUserController().unload(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public synchronized void quit(PlayerKickEvent event) {
        Player player = event.getPlayer();
        User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());

        try (Jedis jedis = BukkitMain.getInstance().getRedis().getPool().getResource()) {
            String route = "route:" + user.getGame().getType().name().toLowerCase() + "-count";
            int actualCount = 0;

            if (jedis.exists(route)) {
                actualCount = Integer.parseInt(jedis.get(route));

                jedis.del(route);
            }

            jedis.set(route, String.valueOf(actualCount - 1));
        }

        GameMain.getPlugin().getUserController().unload(player.getUniqueId());
    }
}
