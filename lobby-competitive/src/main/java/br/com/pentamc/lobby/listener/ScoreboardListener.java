package br.com.pentamc.lobby.listener;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.GameStatus;
import br.com.pentamc.common.permission.Group;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import br.com.pentamc.bukkit.api.scoreboard.Score;
import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import br.com.pentamc.bukkit.event.player.PlayerScoreboardStateEvent;
import br.com.pentamc.bukkit.event.server.PlayerChangeEvent;

public class ScoreboardListener implements Listener {

    public static final Scoreboard DEFAULT_SCOREBOARD;

    static {
        DEFAULT_SCOREBOARD = new SimpleScoreboard("§b§lCOMPETITIVO");

        DEFAULT_SCOREBOARD.blankLine(13);
        DEFAULT_SCOREBOARD.setScore(12, new Score("§eComp: ", "comp"));
        DEFAULT_SCOREBOARD.setScore(11, new Score(" Wins: §a0", "wins"));
        DEFAULT_SCOREBOARD.setScore(10, new Score(" Kills: §a0", "kills"));
        DEFAULT_SCOREBOARD.blankLine(9);
        DEFAULT_SCOREBOARD.setScore(8, new Score("§eEventos: ", "event"));
        DEFAULT_SCOREBOARD.setScore(7, new Score(" Wins: §a0", "event-wins"));
        DEFAULT_SCOREBOARD.setScore(6, new Score(" Kills: §a0", "event-kills"));
        DEFAULT_SCOREBOARD.blankLine(5);
        DEFAULT_SCOREBOARD.setScore(4, new Score("Coins: §60", "coins"));
        DEFAULT_SCOREBOARD.setScore(3, new Score("Jogadores: §a0", "online"));
        DEFAULT_SCOREBOARD.blankLine(2);
        DEFAULT_SCOREBOARD.setScore(1, new Score("§a§o" + CommonConst.SITE, "site"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        handleScoreboard(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerChangeEvent event) {
        DEFAULT_SCOREBOARD.updateScore(new Score("Jogadores: §a" + event.getTotalMembers(), "online"));
    }

    @EventHandler
    public void onPlayerScoreboardState(PlayerScoreboardStateEvent event) {
        if (event.isScoreboardEnabled()) {
            handleScoreboard(event.getPlayer());
        }
    }

    private void handleScoreboard(Player player) {
        Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

        if (member == null) {
            player.kickPlayer("§cSua conta não foi carregada!");
            return;
        }
        DEFAULT_SCOREBOARD.createScoreboard(player);

        Group group = member.getGroup();

        GameStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.HG, GameStatus.class);

        DEFAULT_SCOREBOARD.updateScore(player, new Score(" Wins: §a" + status.getWins(), "wins"));
        DEFAULT_SCOREBOARD.updateScore(player, new Score(" Kills: §a" + status.getKills(), "kills"));

        DEFAULT_SCOREBOARD.setScore(4, new Score("Coins: §6" + member.getMoney(), "coins"));

        status = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.EVENTO, GameStatus.class);

        DEFAULT_SCOREBOARD.updateScore(player, new Score(" Wins: §a" + status.getWins(), "event-wins"));
        DEFAULT_SCOREBOARD.updateScore(player, new Score(" Kills: §a" + status.getKills(), "event-kills"));
    }
}
