package br.com.pentamc.lobby.scoreboard.objective;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class ObjectivePlayerlist extends ObjectiveBase {

    public ObjectivePlayerlist(Scoreboard scoreboard) {
        super(scoreboard, DisplaySlot.PLAYER_LIST);
    }

    public void setScore(String name, int score) {
        getObjective().getScore(name).setScore(score);
    }

    public void unregisterScore(String name) {
        getScoreboard().resetScores(name);
    }

}
