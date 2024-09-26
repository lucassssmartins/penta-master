package br.com.pentamc.competitive.game;

import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.event.team.TeamPlayerJoinEvent;
import br.com.pentamc.competitive.event.team.TeamPlayerLeaveEvent;
import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class Team {

    private final UUID id;
    private final Color color;

    private List<UUID> playerList = new ArrayList<>();

    @Setter
    private int kills;

    public Team addPlayer(Gamer gamer) {
        if (isFull()) {
            gamer.getPlayer().sendMessage("§cEste jogador já está em uma dupla.");
            return this;
        }

        if (playerList.contains(gamer.getUniqueId())) {
            gamer.getPlayer().sendMessage("§cVocê já está em uma dupla.");
            return this;
        }

        if (gamer.getTeam() != null) {
            gamer.getTeam().removePlayer(gamer);
        }

        gamer.setTeam(this);

        playerList.add(gamer.getUniqueId());

        TeamPlayerJoinEvent event = new TeamPlayerJoinEvent(this, gamer);
        Bukkit.getPluginManager().callEvent(event);
        return this;
    }

    public Team removePlayer(Gamer gamer) {
        if (!getPlayerList().contains(gamer.getUniqueId())) {
            gamer.getPlayer().sendMessage("§cVocê não está nesta dupla.");
            return this;
        }

        gamer.setTeam(null);

        TeamPlayerLeaveEvent event = new TeamPlayerLeaveEvent(this, gamer);
        Bukkit.getPluginManager().callEvent(event);

        playerList.remove(gamer.getUniqueId());
        return this;
    }

    public void addKill() {
        setKills(getKills() + 1);
    }

    public void forceAddGamer(Gamer gamer) {
        gamer.setTeam(this);

        TeamPlayerJoinEvent event = new TeamPlayerJoinEvent(this, gamer);
        Bukkit.getPluginManager().callEvent(event);

        playerList.add(gamer.getUniqueId());
    }

    public void forceRemoveGamer(Gamer gamer) {
        gamer.setTeam(null);
        playerList.remove(gamer.getUniqueId());
    }

    public List<Gamer> getParticipantsAsGamer() {
        return playerList.stream().map(uuid -> GameGeneral.getInstance().getGamerController().getGamer(uuid))
                         .collect(Collectors.toList());
    }

    public List<Player> getParticipantsAsPlayer() {
        return playerList.stream().map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    public boolean isFull() {
        return playerList.size() >= GameMain.getInstance().getMaxPlayersPerTeam();
    }

    public static Team createTeam(UUID id, Color color) {
        return new Team(id, color);
    }

    public static Team createTeamForGamer(UUID id, Color color, Gamer gamer) {
        return new Team(id, color).addPlayer(gamer);
    }

    public boolean isAlive() {
        if (playerList.size() == 0) {
            return false;
        }

        return getParticipantsAsGamer().stream().anyMatch(Gamer::isPlaying);
    }
}
