package br.com.pentamc.lobby.scoreboard;

import br.com.pentamc.lobby.scoreboard.objective.ObjectiveBase;
import br.com.pentamc.lobby.scoreboard.objective.ObjectiveBelowName;
import br.com.pentamc.lobby.scoreboard.objective.ObjectivePlayerlist;
import br.com.pentamc.lobby.scoreboard.objective.ObjectiveSidebar;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Scoreboard {

    private final org.bukkit.scoreboard.Scoreboard scoreboard;
    private ObjectiveBase sidebar, belowName, playerList;

    public ObjectiveSidebar getSidebar() {
        return (ObjectiveSidebar) (sidebar == null ? sidebar = new ObjectiveSidebar(scoreboard) : sidebar);
    }

    public ObjectiveBelowName getBelowName() {
        return (ObjectiveBelowName) (belowName == null ? belowName = new ObjectiveBelowName(scoreboard) : belowName);
    }

    public ObjectivePlayerlist getPlayerlist() {
        return (ObjectivePlayerlist) (playerList == null ? playerList = new ObjectivePlayerlist(scoreboard) : playerList);
    }
}
