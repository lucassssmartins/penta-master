package br.com.pentamc.competitive.scheduler.types;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.pentamc.competitive.GameConst;
import br.com.pentamc.competitive.event.game.GameStartEvent;
import br.com.pentamc.competitive.utils.ServerConfig;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.game.GameState;
import br.com.pentamc.competitive.listener.register.GameListener;
import br.com.pentamc.competitive.listener.register.pregame.PregameListener;
import br.com.pentamc.bukkit.api.title.types.SimpleTitle;
import br.com.pentamc.common.utils.string.StringUtils;
import redis.clients.jedis.Jedis;

public class PregameScheduler implements GameSchedule {

    private GameGeneral gameGeneral;
    private List<Listener> listenerList;
    private boolean countTime;

    private Map<String, List<String>> MESSAGES;

    public PregameScheduler() {
        this.gameGeneral = GameGeneral.getInstance();
        this.listenerList = Arrays.asList(new PregameListener());

        registerListener();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerLoginEvent event) {
        if (gameGeneral.getGameState() == GameState.WAITING) {
            if (!ServerConfig.getInstance().isTimeInWaiting()) {
                gameGeneral.setGameState(GameState.PREGAME);
            }
        }

        if (!countTime) {
            if (GameGeneral.getInstance().getPlayersInGame() >= ServerConfig.getInstance().getPlayersToStart()) {
                countTime = true;
                gameGeneral.setCountTime(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() < ServerConfig.getInstance().getPlayersToStart()) {
                    gameGeneral.setGameState(GameState.WAITING, false);
                    gameGeneral.setTime(
                            ServerConfig.getInstance().isTimeInWaiting() ? GameState.WAITING.getDefaultTime() :
                            GameState.PREGAME.getDefaultTime());

                    if (countTime) {
                        if (GameGeneral.getInstance().getPlayersInGame() <
                            ServerConfig.getInstance().getPlayersToStart()) {
                            gameGeneral.setCountTime(false);
                            countTime = false;
                        }
                    }
                }
            }
        }.runTaskLater(GameMain.getInstance(), 7l);
    }

    @Override
    public void pulse(int time, GameState gameState) {
        if (gameState == GameState.WAITING && !ServerConfig.getInstance().isTimeInWaiting()) {
            return;
        }

        ServerConfig.getInstance().execute(gameState, time);

        /*
         * Teleport the player a only a time and change PREGAME to STARTING
         */

        if (gameState == GameState.WAITING) {
            if (time <= 0) {
                gameGeneral.setGameState(GameState.PREGAME, false);
                gameGeneral.setTime(60);
            }
            return;
        }

        if (time <= 15 && gameState == GameState.PREGAME) {
            gameGeneral.setGameState(GameState.STARTING);
            gameGeneral.setTime(time);
        }

        /*
         * Start the game
         */

        if (time <= 0) {
            gameGeneral.setGameState(GameState.INVINCIBILITY, false);
            gameGeneral.setTime(GameMain.getInstance().getVarManager().getVar("invincibility-start-time", GameState.INVINCIBILITY.getDefaultTime()));
            GameMain.getInstance().registerListener(new GameListener());
            Bukkit.getPluginManager().callEvent(new GameStartEvent());
            unregisterListener();
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 1f));
            return;
        }

        /*
         * If the time is lower than 5, send sound to players
         */

        if (time <= 5) {
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1f, 1f));
        }

        /*
         * Pregame message
         */

        if (time % 60 == 0)
            try (Jedis jedis = GameMain.getInstance().getRedis().getPool().getResource()) {
                jedis.setex("competitive:time", 3, "§b§lPENTA §7§l» §6§lCOMPETITIVO §e(" + GameMain.getInstance().getVarManager().getVar(GameConst.MODE_NAME, "null") + ") §b/comp");
            }

        if ((time % 60 == 0 || (time < 60 && (time % 15 == 0 || time == 10 || time <= 5)))) {
            if (GameMain.getInstance().getVarManager().getVar("announce-time", true)) {
                Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage("§aO jogo inicia em " + StringUtils.formatTime(time) + "!"));
            }

            new SimpleTitle("§eIniciando em", "§b" + StringUtils.formatTime(time, StringUtils.TimeFormat.NORMAL)).broadcast();
        }
    }

    /*
     * Handle GameStart
     */

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        unregisterListener();
        unload();
    }

    @Override
    public void registerListener() {
        listenerList.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, GameMain.getInstance()));
    }

    @Override
    public void unregisterListener() {
        listenerList.forEach(listener -> HandlerList.unregisterAll(listener));
    }
}
