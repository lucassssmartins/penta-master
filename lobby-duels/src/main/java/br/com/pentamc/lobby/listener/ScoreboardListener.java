package br.com.pentamc.lobby.listener;

import br.com.pentamc.bukkit.api.scoreboard.Score;
import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import br.com.pentamc.bukkit.event.account.PlayerChangeGroupEvent;
import br.com.pentamc.bukkit.event.player.PlayerScoreboardStateEvent;
import br.com.pentamc.bukkit.event.server.PlayerChangeEvent;
import br.com.pentamc.bukkit.event.update.UpdateEvent;
import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.combat.CombatStatus;
import br.com.pentamc.common.account.status.types.game.GameStatus;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.tag.Tag;
import br.com.pentamc.lobby.LobbyMain;
import br.com.pentamc.lobby.LobbyPlatform;
import br.com.pentamc.lobby.scoreboard.objective.ObjectiveSidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardListener implements Listener {

    private int online = 0;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        handleScoreboard(event.getPlayer());
    }

    @EventHandler
    public void update(UpdateEvent event) {
        if (event.getCurrentTick() % 10 != 0)
            return;
        Bukkit.getOnlinePlayers().forEach(this::update);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerChangeEvent event) {
        online = event.getTotalMembers();
    }

    public void update(Player player) {
        br.com.pentamc.lobby.scoreboard.Scoreboard sb = LobbyMain.getInstance().getSbHandler().getScoreboard(player);
        if (sb == null)
            return;
        ObjectiveSidebar sidebar = sb.getSidebar();

        if (sidebar ==  null)
            return;

        GameStatus soupStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.SHADOW, GameStatus.class);
        CombatStatus gladiatorStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.GLADIATOR, CombatStatus.class);

        sidebar.setScore(12, "");
        sidebar.setScore(11, "§e§oSopa:");
        sidebar.setScore(10, " §fWins: §a" + soupStatus.getWinStreak());
        sidebar.setScore(9, " §fStreak: §a" + gladiatorStatus.getWins());
        sidebar.setScore(8, "");
        sidebar.setScore(7, "§e§oGladiator:");
        sidebar.setScore(6, " §fWins: §a" + gladiatorStatus.getWins());
        sidebar.setScore(5, " §fStreak: §a" + gladiatorStatus.getWinStreak());
        sidebar.setScore(4, "");
        sidebar.setScore(3, "§fJogadores: §b" + online);
        sidebar.setScore(2, "");
        sidebar.setScore(1, "§a§o" + CommonConst.SITE);
    }

    private void handleScoreboard(Player player) {
        Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

        if (member == null) {
            player.kickPlayer("§cSua conta não foi carregada!");
            return;
        }

        br.com.pentamc.lobby.scoreboard.Scoreboard sb = LobbyMain.getInstance().getSbHandler().createScoreboard(player);

        ObjectiveSidebar sidebar = sb.getSidebar();

        GameStatus soupStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.SHADOW, GameStatus.class);
        CombatStatus gladiatorStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.GLADIATOR, CombatStatus.class);

        sidebar.setDisplayName("§b§lDUELS");

        sidebar.setScore(12, "");
        sidebar.setScore(11, "§e§oSopa:");
        sidebar.setScore(10, " §fWins: §a" + soupStatus.getWinStreak());
        sidebar.setScore(9, " §fStreak: §a" + gladiatorStatus.getWins());
        sidebar.setScore(8, "");
        sidebar.setScore(7, "§e§oGladiator:");
        sidebar.setScore(6, " §fWins: §a" + gladiatorStatus.getWins());
        sidebar.setScore(5, " §fStreak: §a" + gladiatorStatus.getWinStreak());
        sidebar.setScore(4, "");
        sidebar.setScore(3, "§fJogadores: §b" + online);
        sidebar.setScore(2, "");
        sidebar.setScore(1, "§a§o" + CommonConst.SITE);
    }
}
