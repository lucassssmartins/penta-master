package br.com.pentamc.competitive.scheduler.types;

import java.util.Arrays;
import java.util.List;

import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.event.player.PlayerTimeoutEvent;
import br.com.pentamc.competitive.structure.impl.FeastStructure;
import br.com.pentamc.competitive.structure.impl.MinifeastStructure;
import br.com.pentamc.competitive.utils.ServerConfig;
import br.com.pentamc.competitive.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.game.GameState;
import br.com.pentamc.competitive.listener.register.game.CombatListener;
import br.com.pentamc.competitive.listener.register.winner.WinnerListener;
import br.com.pentamc.bukkit.event.admin.PlayerAdminModeEvent;
import br.com.pentamc.common.utils.string.StringUtils;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameScheduler implements GameSchedule {

    public static Location feastLocation;

    private GameGeneral gameGeneral;
    private List<Listener> listenerList;

    private int feastTimer;

    private FeastStructure feastStructure;

    public GameScheduler() {
        this.gameGeneral = GameGeneral.getInstance();
        this.listenerList = Arrays.asList(new CombatListener());

        registerListener();
        checkWin();
    }

    @Override
    public void pulse(int time, GameState gameState) {
        checkWin();

        ServerConfig.getInstance().execute(gameState, time);

        if (GameMain.getInstance().getVarManager().getVar("minifeast-enabled", true)) {
            if (time % 360 == 0 && time < 1800) {
                MinifeastStructure minifest = new MinifeastStructure();
                Location place = minifest.findPlace();
                minifest.spawn(place);

                Bukkit.broadcastMessage("§cUm minifeast spawnou entre §c(X: " + ((int) place.getX() + 100) + ", " +
                        ((int) place.getX() - 100) + ") e §c(Z:" + ((int) place.getZ() + 100) + ", " +
                        ((int) place.getZ() - 100) + ")!");
            }
        }

        if (GameMain.getInstance().getVarManager().getVar("feast-enabled", true)) {
            if (feastStructure == null) {
                if (time == GameMain.getInstance().getVarManager().getVar("feast-time", 720)) {
                    feastStructure = new FeastStructure();
                    feastLocation = feastStructure.findPlace();
                    feastStructure.spawn(feastLocation);

                    feastTimer = GameMain.getInstance().getVarManager().getVar("feast-time-to-spawn", 300);
                    Bukkit.broadcastMessage(
                            "§cO feast irá spawnar em " + (int) feastLocation.getX() + ", " + (int) feastLocation.getY() +
                                    ", " + (int) feastLocation.getZ() + " em " + StringUtils.formatTime(feastTimer));
                }
            } else {
                int feastTime = time - GameMain.getInstance().getVarManager().getVar("feast-time", 720);

                if (feastTime >= GameMain.getInstance().getVarManager().getVar("feast-time-to-spawn", 300)) {
                    feastStructure.spawnChest(feastLocation);
                    Bukkit.broadcastMessage(
                            "§cO feast spawnou em " + (int) feastLocation.getX() + ", " + (int) feastLocation.getY() +
                                    ", " + (int) feastLocation.getZ() + "!");

                    feastStructure = null;
                } else if ((feastTime % 60 == 0 ||
                        (feastTime > 240 && (feastTime % 15 == 0 || feastTime == 290 || feastTime >= 295)))) {
                    Bukkit.broadcastMessage(
                            "§cO feast irá spawnar em " + (int) feastLocation.getX() + ", " + (int) feastLocation.getY() +
                                    ", " + (int) feastLocation.getZ() + " em " + StringUtils.formatTime(300 - feastTime));
                }
            }
        }

        if (time >= 1800) {
            if (br.com.pentamc.bukkit.listener.register.CombatListener.enabledClean) {
                br.com.pentamc.bukkit.listener.register.CombatListener.cancelAll();
            }
        }

        if (time == 1800) {
            for (Gamer gamer : gameGeneral.getGamerController().getGamers()) {
                Team team = gamer.getTeam();

                Gamer participant = team.getParticipantsAsGamer().stream()
                        .filter(participants -> !participants.equals(gamer))
                        .findFirst()
                        .orElse(null);

                if (participant == null) {
                    gamer.setAllowPair(true);
                    break;
                }
            }

            ServerConfig.getInstance().setPvpEnabled(false);
            Bukkit.getScheduler().runTaskLater(GameMain.getInstance(), () -> ServerConfig.getInstance().setPvpEnabled(true), 20 * 5);
        }
        if (time == 2100) {
            ServerConfig.getInstance().setPvpEnabled(false);
            Bukkit.getScheduler().runTaskLater(GameMain.getInstance(), () -> ServerConfig.getInstance().setPvpEnabled(true), 20 * 5);
        }
        if (time == 2700) {
            ServerConfig.getInstance().setPvpEnabled(false);

            Bukkit.getScheduler().runTaskLater(GameMain.getInstance(), () -> ServerConfig.getInstance().setPvpEnabled(true), 20 * 5);
        }

        if (time >= 2701) {
            Bukkit.getOnlinePlayers().stream().filter(player -> !player.hasPotionEffect(PotionEffectType.WITHER))
                    .forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 99999, 1)));
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAdminMode(PlayerAdminModeEvent event) {
        checkWin();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAdminMode(PlayerDeathEvent event) {
        checkWin();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAdminMode(PlayerQuitEvent event) {
        checkWin();
    }

    @EventHandler
    public void onPlayerTimeout(PlayerTimeoutEvent event) {
        checkWin();
    }

    public boolean checkWin() {
        if (gameGeneral.getGameState() == GameState.WINNING) {
            return false;
        }

        if (GameMain.getPlugin().isTeamEnabled()) {
            if (GameMain.getInstance().getAliveTeams().size() > 1)
                return false;

            Team team = GameMain.getInstance().getAliveTeams().get(0);

            if (team == null) {
                Bukkit.shutdown();
                return false;
            }

            unregisterListener();
            GameGeneral.getInstance().getSchedulerController().removeSchedule(this);
            GameMain.getInstance()
                    .registerListener(new WinnerListener(team.getParticipantsAsPlayer().toArray(new Player[0])));
            return true;
        }

        if (gameGeneral.getPlayersInGame() > 1) {
            return false;
        }

        gameGeneral.setGameState(GameState.WINNING);
        Player pWin = null;

        for (Player p : Bukkit.getOnlinePlayers()) {
            Gamer gamer = gameGeneral.getGamerController().getGamer(p);

            if (gamer.isGamemaker()) {
                continue;
            }

            if (gamer.isSpectator()) {
                continue;
            }

            if (!p.isOnline()) {
                continue;
            }

            pWin = p;
            break;
        }

        if (pWin == null) {
            Bukkit.shutdown();
            return false;
        }

        unregisterListener();
        GameGeneral.getInstance().getSchedulerController().removeSchedule(this);
        GameMain.getInstance().registerListener(new WinnerListener(pWin));

        return true;
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
