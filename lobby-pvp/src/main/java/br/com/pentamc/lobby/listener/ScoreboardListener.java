package br.com.pentamc.lobby.listener;

import br.com.pentamc.bukkit.api.scoreboard.Score;
import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import br.com.pentamc.bukkit.event.account.PlayerChangeGroupEvent;
import br.com.pentamc.bukkit.event.player.PlayerScoreboardStateEvent;
import br.com.pentamc.bukkit.event.server.PlayerChangeEvent;
import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.pvp.PvPStatus;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.tag.Tag;
import br.com.pentamc.lobby.LobbyPlatform;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardListener implements Listener {

    public static final Scoreboard DEFAULT_SCOREBOARD;

    static {
        DEFAULT_SCOREBOARD = new SimpleScoreboard("§b§lPVP");
        DEFAULT_SCOREBOARD.blankLine(8);

        DEFAULT_SCOREBOARD.setScore(7, new Score("§7Bem-vindo ao PvP", "m1"));
        DEFAULT_SCOREBOARD.setScore(6, new Score("§7Selecione um modo!", "m2"));
        DEFAULT_SCOREBOARD.blankLine(5);

        DEFAULT_SCOREBOARD.setScore(4, new Score("Coins: §60", "coins"));
        DEFAULT_SCOREBOARD.setScore(3, new Score("Jogadores: §b0", "online"));
        DEFAULT_SCOREBOARD.blankLine(2);

        DEFAULT_SCOREBOARD.setScore(1, new Score("§a§o" + CommonConst.SITE, "website"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        handleScoreboard(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerChangeEvent event) {
        DEFAULT_SCOREBOARD.updateScore(new Score("Jogadores: §b" + event.getTotalMembers(), "online"));
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

        PvPStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.PVP, PvPStatus.class);

        DEFAULT_SCOREBOARD.updateScore(player, new Score("Coins: §6" + status.getCoins(), "coins"));
    }
}
