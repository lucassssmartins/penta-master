package br.com.pentamc.competitive.event.team;

import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.game.Team;
import lombok.Getter;
import br.com.pentamc.bukkit.event.PlayerCancellableEvent;

@Getter
public class TeamPlayerEvent extends PlayerCancellableEvent {

    private Gamer gamer;

    private Team team;

    public TeamPlayerEvent(Team team, Gamer gamer) {
        super(gamer.getPlayer());
        this.gamer = gamer;
        this.team = team;
    }
}
