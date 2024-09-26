package br.com.pentamc.lobby.scoreboard.objective;

import br.com.pentamc.lobby.scoreboard.SidebarText;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class ObjectiveSidebar extends ObjectiveBase {

    private Map<Integer, Map.Entry<SidebarText, Score>> scores = new HashMap<>();

    public ObjectiveSidebar(Scoreboard scoreboard) {
        super(scoreboard, DisplaySlot.SIDEBAR);
    }

    public void setScore(int index, String text) {
        Preconditions.checkArgument(index > 0 && index < 16,
                "Parameter 'index' must be between 1 and 15");
        if (text != null) {
            Map.Entry<SidebarText, Score> score = scores.computeIfAbsent(index, v ->
                    new AbstractMap.SimpleEntry<>(new SidebarText(), new Score(index)));
            if (!text.equals(score.getValue().getText())) {
                Team team = getScoreboard().getTeam(score.getValue().getTeam());
                if (team == null) {
                    getObjective().getScore(score.getValue().getEntry()).setScore(index);
                    team = getScoreboard().registerNewTeam(score.getValue().getTeam());
                    if (!team.hasEntry(score.getValue().getEntry()))
                        team.addEntry(score.getValue().getEntry());
                }
                score.getKey().build(text);
                team.setPrefix(score.getKey().getBefore16());
                team.setSuffix(score.getKey().getAfter16());
                score.getValue().setText(text);
            }
        }
    }

    public void setScores(List<String> scores) {
        for (int p = 0, i = 15; i > 0; i--) {
            if (i <= scores.size()) {
                setScore(i, scores.get(p++));
            } else {
                unregisterScore(i);
            }
        }
    }

    public void unregisterScore(int index) {
        if (scores.containsKey(index)) {
            Map.Entry<SidebarText, Score> score = scores.remove(index);
            getScoreboard().resetScores(score.getValue().getEntry());
            Team team = getScoreboard().getTeam(score.getValue().getTeam());
            if (team != null) team.unregister();
        }
    }

    public void unregisterScores() {
        for (int i = 15; i > 0; i--) {
            unregisterScore(i);
        }
    }

    protected String cutStr(String string) {
        int length = string.length();
        if (length > 16)
            string = string.substring(0, 16);
        if (string.endsWith("§"))
            string = string.substring(0, length-1);
        return string;
    }

    @Getter
    protected class Score {

        @Setter
        private String text;
        private String team;
        private String entry;

        private int index;

        public Score(int index) {
            this.team = "score-" + (index < 10 ? "0" : "") + index;
            ChatColor color = ChatColor.values()[index];
            this.entry = color.toString() + ChatColor.RESET;
            this.index = index;
        }

        @Override
        protected void finalize() throws Throwable {
            text = null;
            team = null;
            entry = null;
            super.finalize();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        scores.clear();
        scores = null;
        super.finalize();
    }

}
