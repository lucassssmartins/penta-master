package br.com.pentamc.pvp.listener.user;

import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.game.Game;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GameListener implements Listener {

    @EventHandler
    public void load(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());
        Game game = user.getGame();

        Bukkit.getWorld(game.getType().getWorldName()).getPlayers().forEach(players -> {
            players.hidePlayer(player);
            players.showPlayer(player);
        });

        game.load(player);
    }
}