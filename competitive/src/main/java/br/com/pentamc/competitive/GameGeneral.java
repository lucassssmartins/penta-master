package br.com.pentamc.competitive;

import java.util.stream.Collectors;

import br.com.pentamc.competitive.event.game.GameStateChangeEvent;
import br.com.pentamc.competitive.event.game.GameTimeEvent;
import br.com.pentamc.competitive.game.GameState;
import br.com.pentamc.competitive.scheduler.types.PregameScheduler;
import br.com.pentamc.competitive.utils.MapUtils;
import org.bukkit.Bukkit;

import br.com.pentamc.competitive.controller.AbilityController;
import br.com.pentamc.competitive.controller.GamerController;
import br.com.pentamc.competitive.controller.KitController;
import br.com.pentamc.competitive.controller.SchedulerController;
import br.com.pentamc.competitive.controller.SimplekitController;
import br.com.pentamc.competitive.controller.TimeoutController;
import lombok.Getter;
import lombok.Setter;

@Getter
public class GameGeneral {

    @Getter
    private static GameGeneral instance;

    private GameState gameState;
    private int time;
    @Setter
    private boolean
            countTime,
            build = true;

    private GamerController gamerController;

    private AbilityController abilityController;
    private KitController kitController;
    private SchedulerController schedulerController;

    private SimplekitController simplekitController;
    private TimeoutController timeoutController;

    public GameGeneral() {
        instance = this;

        gameState = GameState.WAITING;
        time = gameState.getDefaultTime();

        gamerController = new GamerController();

        abilityController = new AbilityController();
        kitController = new KitController();
        schedulerController = new SchedulerController();

        simplekitController = new SimplekitController();
        timeoutController = new TimeoutController();
    }

    public void onLoad() {
        MapUtils.deleteWorld("world");
    }

    public void onEnable() {
        abilityController.load("br.com.pentamc.competitive.abilities.register");
        kitController.load("br.com.pentamc.competitive.kit.register");
        schedulerController.addSchedule(new PregameScheduler());
    }

    public void onDisable() {

    }

    public void setGameState(GameState gameState) {
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this.gameState, gameState));
        this.gameState = gameState;
        this.time = gameState.getDefaultTime();
    }

    public void setGameState(GameState gameState, boolean defaultTime) {
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this.gameState, gameState));
        this.gameState = gameState;

        if (defaultTime) {
            this.time = gameState.getDefaultTime();
        }
    }

    public void setTime(int time) {
        Bukkit.getPluginManager().callEvent(new GameTimeEvent(time));
        this.time = time;
    }

    public void pulse() {
        if (isCountTime()) {
            schedulerController.pulse();

            if (gameState.isUpTime()) {
                setTime(getTime() + 1);
            } else {
                setTime(getTime() - 1);
            }
        }
    }

    public int getPlayersInGame() {
        return gamerController.getStoreMap().values().stream().filter(gamer -> !gamer.isNotPlaying())
                              .collect(Collectors.toList()).size();
    }
}
