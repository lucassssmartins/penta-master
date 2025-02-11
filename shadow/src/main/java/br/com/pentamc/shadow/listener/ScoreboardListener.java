package br.com.pentamc.shadow.listener;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.League;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.GameStatus;
import br.com.pentamc.common.utils.string.StringUtils;
import br.com.pentamc.shadow.GameMain;
import br.com.pentamc.shadow.challenge.Challenge;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import br.com.pentamc.bukkit.api.scoreboard.Score;
import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.event.player.PlayerScoreboardStateEvent;
import br.com.pentamc.shadow.event.GladiatorFinishEvent;
import br.com.pentamc.shadow.event.GladiatorPulseEvent;
import br.com.pentamc.shadow.event.GladiatorSpectatorEvent;
import br.com.pentamc.shadow.event.GladiatorSpectatorEvent.Action;
import br.com.pentamc.shadow.event.GladiatorStartEvent;

public class ScoreboardListener implements Listener {

    private static final Scoreboard SCOREBOARD = new SimpleScoreboard("§b§l1v1");
    private static final Scoreboard FIGHT_SCOREBOARD = new SimpleScoreboard("§b§l1v1");

    {
        SCOREBOARD.blankLine(8);
        SCOREBOARD.setScore(7, new Score("Vitórias: 0", "wins"));
        SCOREBOARD.setScore(6, new Score("Derrotas: 0", "loses"));
        SCOREBOARD.setScore(5, new Score("Winstreak: 0", "winstreak"));
        SCOREBOARD.setScore(4, new Score("Ranking: " + League.values()[0].getColor() + League.values()[0].getName(), "ranking"));
        SCOREBOARD.blankLine(3);
        SCOREBOARD.setScore(2, new Score("Jogadores: 0", "players"));
        SCOREBOARD.blankLine(1);
        SCOREBOARD.setScore(0, new Score("§a§o" + CommonConst.SITE, "site"));

        FIGHT_SCOREBOARD.blankLine(10);
        FIGHT_SCOREBOARD.setScore(9, new Score("Tempo: §a", "time"));
        FIGHT_SCOREBOARD.setScore(8, new Score("Winstreak: 0", "winstreak"));
        FIGHT_SCOREBOARD.blankLine(7);
        FIGHT_SCOREBOARD.setScore(6, new Score("§3Ninguém: §e0ms", "firstPing"));
        FIGHT_SCOREBOARD.setScore(5, new Score("§bNinguém: §e0ms", "secondPing"));
        FIGHT_SCOREBOARD.blankLine(4);
        FIGHT_SCOREBOARD.setScore(3, new Score("Modo: §a1v1 Solo", "modo"));
        FIGHT_SCOREBOARD.setScore(2, new Score("Ranking: " + League.values()[0].getColor() + League.values()[0].getName(), "ranking"));
        FIGHT_SCOREBOARD.blankLine(1);
        FIGHT_SCOREBOARD.setScore(0, new Score("§a§o" + CommonConst.SITE, "site"));
    }

    @EventHandler
    public void onPlayerWarpJoin(PlayerScoreboardStateEvent event) {
        if (event.isScoreboardEnabled()) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    loadScoreboard(event.getPlayer());
                }
            }.runTaskLater(GameMain.getInstance(), 5L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                loadScoreboard(event.getPlayer());
            }
        }.runTaskLater(GameMain.getInstance(), 7L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        SCOREBOARD.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                            .getMember(event.getPlayer().getUniqueId()));
        FIGHT_SCOREBOARD.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                                  .getMember(event.getPlayer().getUniqueId()));

        new BukkitRunnable() {

            @Override
            public void run() {
                SCOREBOARD.updateScore(new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
                FIGHT_SCOREBOARD.updateScore(new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
            }
        }.runTaskLater(GameMain.getInstance(), 7L);
    }

    @EventHandler
    public void onGladiatorFinish(GladiatorStartEvent event) {
        Player player = event.getChallenge().getPlayer();
        Player enimy = event.getChallenge().getEnimy();

        SCOREBOARD.removeViewer(
                (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));

        SCOREBOARD.removeViewer(
                (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(enimy.getUniqueId()));

        FIGHT_SCOREBOARD.createScoreboard(player);
        FIGHT_SCOREBOARD.createScoreboard(enimy);

        updateScore(player, event.getChallenge());
        updateScore(enimy, event.getChallenge());
    }

    @EventHandler
    public void onGladiatorSpectator(GladiatorSpectatorEvent event) {
        if (event.getAction() == Action.JOIN) {
            SCOREBOARD.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                                .getMember(event.getPlayer().getUniqueId()));

            FIGHT_SCOREBOARD.createScoreboard(event.getPlayer());
            updateScore(event.getPlayer(), event.getChallenge());
        } else {
            loadScoreboard(event.getPlayer());
        }
    }

    @EventHandler
    public void onGladiatorPulse(GladiatorPulseEvent event) {
        updateScore(event.getChallenge().getEnimy(), event.getChallenge());
        updateScore(event.getChallenge().getPlayer(), event.getChallenge());

        event.getChallenge().getSpectatorSet().forEach(player -> updateScore(player, event.getChallenge()));
    }

    @EventHandler
    public void onGladiatorFinish(GladiatorFinishEvent event) {
        loadScoreboard(event.getChallenge().getEnimy());
        loadScoreboard(event.getChallenge().getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerWarpDeath(GladiatorFinishEvent event) {
        boolean updatePlayer = true;
        boolean updateKiller = event.getWinner() != null;

        Player player = event.getLoser();
        updateState(player);

        if (updateKiller) {
            Player killer = event.getWinner();
            updateState(killer);
        }
    }

    private void updateState(Player killer) {
        GameStatus killerStatus = CommonGeneral.getInstance().getStatusManager()
                                                 .loadStatus(killer.getUniqueId(), StatusType.SHADOW,
                                                         GameStatus.class);
        Member member = CommonGeneral.getInstance().getMemberManager().getMember(killer.getUniqueId());

        SCOREBOARD.updateScore(killer, new Score("Vitórias: §7" + killerStatus.getKills(), "wins"));
        SCOREBOARD.updateScore(killer, new Score("Derrotas: §7" + killerStatus.getDeaths(), "loses"));
        SCOREBOARD.updateScore(killer, new Score("Winstreak: §a" + killerStatus.getKillstreak(), "winstreak"));

        SCOREBOARD.updateScore(killer,
                               new Score("Ranking: §a" + member.getLeague(StatusType.SHADOW).getColor() + member.getLeague(StatusType.SHADOW).getName(),
                                         "ranking"));

        FIGHT_SCOREBOARD.updateScore(killer, new Score("Winstreak: §a" + killerStatus.getKillstreak(), "winstreak"));
        FIGHT_SCOREBOARD.updateScore(killer,
                               new Score("Ranking: §a" + member.getLeague(StatusType.SHADOW).getColor() + member.getLeague(StatusType.SHADOW).getName(),
                                         "ranking"));

    }

    public void loadScoreboard(Player player) {
        FIGHT_SCOREBOARD.removeViewer(
                (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));

        SCOREBOARD.createScoreboard(player);
        updateScore(player);
    }

    public void updateScore(Player player) {
        Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

        updateState(player);

        SCOREBOARD.updateScore(new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
    }

    private void updateScore(Player player, Challenge challenge) {
        Player enimy = challenge.getPlayer() == player ? challenge.getEnimy() : challenge.getPlayer();
        Player target = challenge.getPlayer() == player ? challenge.getPlayer() : challenge.getEnimy();

        FIGHT_SCOREBOARD.updateScore(new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
        FIGHT_SCOREBOARD.updateScore(player, new Score("§3" + target.getName() + ": §e" +
                                                       (((CraftPlayer) target).getHandle().ping >= 1000 ? "1000+" :
                                                        ((CraftPlayer) target).getHandle().ping) + "ms", "firstPing"));
        FIGHT_SCOREBOARD.updateScore(player, new Score("§b" + enimy.getName() + ": §e" +
                                                       (((CraftPlayer) enimy).getHandle().ping >= 1000 ? "1000+" :
                                                        ((CraftPlayer) enimy).getHandle().ping) + "ms", "secondPing"));
        FIGHT_SCOREBOARD.updateScore(player, new Score("Tempo: §a" + StringUtils.format(challenge.getTime()), "time"));

        updateState(player);
    }
}
