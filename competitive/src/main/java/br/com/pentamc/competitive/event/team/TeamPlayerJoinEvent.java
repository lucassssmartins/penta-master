package br.com.pentamc.competitive.event.team;


import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.game.Team;

public class TeamPlayerJoinEvent extends TeamPlayerEvent {
    public TeamPlayerJoinEvent(Team team, Gamer gamer) {
        super(team, gamer);
    }
}
