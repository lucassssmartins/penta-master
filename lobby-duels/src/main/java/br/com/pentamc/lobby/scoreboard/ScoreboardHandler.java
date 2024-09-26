package br.com.pentamc.lobby.scoreboard;

import br.com.pentamc.bukkit.api.scoreboard.Score;
import br.com.pentamc.lobby.LobbyMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ScoreboardHandler implements Listener {

    private Map<UUID, Scoreboard> map = new HashMap<>();

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, LobbyMain.getInstance());
    }

    public Scoreboard getScoreboard(Player player) {
        return map.get(player.getUniqueId());
    }

    public Scoreboard createScoreboard(Player player) {
        org.bukkit.scoreboard.Scoreboard board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        player.setScoreboard(board);
        Scoreboard b = new Scoreboard(board);
        map.put(player.getUniqueId(), b);
        return b;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        map.remove(event.getPlayer().getUniqueId());
    }

    public void onDisable() {
        map.clear();
        map = null;
    }
}
