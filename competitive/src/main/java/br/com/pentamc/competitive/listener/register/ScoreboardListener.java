package br.com.pentamc.competitive.listener.register;

import br.com.pentamc.competitive.GameConst;
import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.event.VarChangeEvent;
import br.com.pentamc.competitive.event.game.GameStartEvent;
import br.com.pentamc.competitive.event.game.GameTimeEvent;
import br.com.pentamc.competitive.event.kit.PlayerSelectedKitEvent;
import br.com.pentamc.competitive.event.player.PlayerTimeoutEvent;
import br.com.pentamc.competitive.event.team.TeamPlayerJoinEvent;
import br.com.pentamc.competitive.event.team.TeamPlayerLeaveEvent;
import br.com.pentamc.competitive.game.Team;
import br.com.pentamc.competitive.utils.ServerConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.kit.KitType;
import br.com.pentamc.competitive.listener.GameListener;
import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.api.scoreboard.Score;
import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import br.com.pentamc.bukkit.event.admin.PlayerAdminModeEvent;
import br.com.pentamc.bukkit.event.player.PlayerScoreboardStateEvent;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.utils.string.NameUtils;
import br.com.pentamc.common.utils.string.StringUtils;

public class ScoreboardListener extends GameListener {

    private static final Scoreboard SCOREBOARD;

    static {
        SCOREBOARD = new SimpleScoreboard(GameMain.getInstance().getVarManager().getVar("scoreboard-name", "§b§lCOMP"));
    }

    @EventHandler
    public void onPlayerScoreboardState(PlayerScoreboardStateEvent event) {
        if (event.isScoreboardEnabled()) {
            Player player = event.getPlayer();

            if (player != null) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (!player.isOnline()) {
                            return;
                        }

                        Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
                        Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

                        SCOREBOARD.updateScore(player, new Score("Jogadores: §7" + (isPregame() ? getGameGeneral().getPlayersInGame() + "/" + Bukkit.getMaxPlayers() : getGameGeneral().getPlayersInGame()), "players"));
                        SCOREBOARD.updateScore(player, new Score("Liga: " + member.getLeague(StatusType.HG).getColor() + member.getLeague(StatusType.HG).getName(), "ranking"));
                        SCOREBOARD.updateScore(player, new Score("Kills: §a" + gamer.getMatchKills(), "kills"));
                        SCOREBOARD.updateScore(player, new Score("Kit: §b" + NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)), "kit1"));

                        if (GameMain.getInstance().isTeamEnabled()) {
                            Team team = gamer.getTeam();

                            Player target = null;

                            if (team != null)
                                target = team.getParticipantsAsPlayer().stream().filter(members -> !members.getName().equalsIgnoreCase(player.getName())).findFirst().orElse(null);

                            if (target == null) {
                                SCOREBOARD.updateScore(player, new Score("Dupla: §aNinguém", "pair"));
                            } else {
                                SCOREBOARD.updateScore(player, new Score("Dupla: §a" + target.getName(), "pair"));
                            }
                        }
                    }
                }.runTaskLater(GameMain.getInstance(), 7l);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        createScoreboard(player);

        new BukkitRunnable() {

            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }

                Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

                SCOREBOARD.updateScore(player, new Score(
                        "Liga: " + member.getLeague(StatusType.HG).getColor() + member.getLeague(StatusType.HG).getName(),
                        "ranking"));
                SCOREBOARD.updateScore(new Score("Jogadores: §7" + (isPregame() ?
                        getGameGeneral().getPlayersInGame() + "/" +
                                Bukkit.getMaxPlayers() :
                        getGameGeneral().getPlayersInGame()), "players"));

                Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

                SCOREBOARD.updateScore(player, new Score(
                        "Kit: §b" + NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)), "kit1"));
            }
        }.runTaskLater(GameMain.getInstance(), 7l);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getKiller();

        new BukkitRunnable() {

            @Override
            public void run() {
                if (player != null) {
                    Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
                    SCOREBOARD.updateScore(player, new Score("Kills: §a" + gamer.getMatchKills(), "kills"));
                }

                SCOREBOARD.updateScore(new Score("Jogadores: §7" + (isPregame() ?
                                                                    getGameGeneral().getPlayersInGame() + "/" +
                                                                    Bukkit.getMaxPlayers() :
                                                                    getGameGeneral().getPlayersInGame()), "players"));
            }
        }.runTaskLater(GameMain.getInstance(), 7l);
    }

    @EventHandler
    public void onPlayerSelectedKit(PlayerSelectedKitEvent event) {
        if (event.getKit() == null) {
            return;
        }

        Player player = event.getPlayer();

        SCOREBOARD.updateScore(player, new Score("Kit: §b" + NameUtils.formatString(event.getKit().getName()), "kit1"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAdminMode(PlayerAdminModeEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                SCOREBOARD.updateScore(new Score("Jogadores: §7" + (isPregame() ?
                                                                    getGameGeneral().getPlayersInGame() + "/" +
                                                                    Bukkit.getMaxPlayers() :
                                                                    getGameGeneral().getPlayersInGame()), "players"));
            }
        }.runTaskLater(GameMain.getInstance(), 7l);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                SCOREBOARD.updateScore(new Score("Jogadores: §7" + (isPregame() ?
                                                                    getGameGeneral().getPlayersInGame() + "/" +
                                                                    Bukkit.getMaxPlayers() :
                                                                    getGameGeneral().getPlayersInGame()), "players"));
            }
        }.runTaskLater(GameMain.getInstance(), 7l);
    }

    @EventHandler
    public void onTeam(TeamPlayerJoinEvent event) {
        if (GameMain.getInstance().getMaxPlayersPerTeam() == 2) {
            event.getTeam().getParticipantsAsPlayer().forEach(player -> {
                System.out.println("aaaaab");

                Player team = event.getTeam().getParticipantsAsPlayer().stream().filter(p -> !p.equals(player))
                                   .findFirst().orElse(null);

                if (team == null) {
                    SCOREBOARD.updateScore(player, new Score("Dupla: §aNinguém", "pair"));
                } else {
                    SCOREBOARD.updateScore(player, new Score("Dupla: §a" + team.getName(), "pair"));
                }
            });
        }
    }

    @EventHandler
    public void onTeam(TeamPlayerLeaveEvent event) {
        if (GameMain.getInstance().getMaxPlayersPerTeam() == 2) {
            event.getTeam().getParticipantsAsPlayer().forEach(player -> {
                System.out.println("aaaaa");

                Player team = event.getTeam().getParticipantsAsPlayer().stream().filter(p -> !p.equals(player))
                                   .findFirst().orElse(null);

                if (team == null) {
                    SCOREBOARD.updateScore(player, new Score("Dupla: §aNinguém", "pair"));
                } else {
                    SCOREBOARD.updateScore(player, new Score("Dupla: §a" + team.getName(), "pair"));
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTimeout(PlayerTimeoutEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                SCOREBOARD.updateScore(new Score("Jogadores: §7" + (isPregame() ?
                                                                    getGameGeneral().getPlayersInGame() + "/" +
                                                                    Bukkit.getMaxPlayers() :
                                                                    getGameGeneral().getPlayersInGame()), "players"));
            }
        }.runTaskLater(GameMain.getInstance(), 7l);
    }

    @EventHandler
    public void onGameStage(GameTimeEvent event) {
        String str = "Aguardando: §7";

        switch (getGameGeneral().getGameState()) {
        case WAITING: {
            if (ServerConfig.getInstance().isTimeInWaiting()) {
                str = "Aguardando: §7";
            }
            break;
        }
        case WINNING:
        case GAMETIME:
            str = "Tempo: §7";
            break;
        case INVINCIBILITY:
            str = "Invencivel por: §7";
            break;
        default:
            break;
        }

        SCOREBOARD.updateScore(new Score(str + StringUtils.format(getGameGeneral().getTime()), "time"));
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        SCOREBOARD.clear();

        createScore();

        for (Player player : Bukkit.getOnlinePlayers()) {
            SCOREBOARD.createScoreboard(player);

            Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
            Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

            SCOREBOARD.updateScore(player, new Score("Kit: §b" + NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)), "kit1"));
            SCOREBOARD.updateScore(player, new Score("Liga: " + member.getLeague(StatusType.HG).getColor() + member.getLeague(StatusType.HG).getName(), "ranking"));

            if (GameMain.getInstance().isTeamEnabled()) {
                Team team = gamer.getTeam();

                Player target = null;

                if (team != null)
                    target = team.getParticipantsAsPlayer().stream().filter(members -> !members.getName().equalsIgnoreCase(player.getName())).findFirst().orElse(null);

                if (target == null) {
                    SCOREBOARD.updateScore(player, new Score("Dupla: §aNinguém", "pair"));
                } else {
                    SCOREBOARD.updateScore(player, new Score("Dupla: §a" + target.getName(), "pair"));
                }
            }
        }

        SCOREBOARD.updateScore(new Score("Jogadores: §7" + getGameGeneral().getPlayersInGame(), "players"));
    }

    @EventHandler
    public void onScoreboardTitleChange(VarChangeEvent event) {
        if (event.getVarName().equals(GameConst.MODE_NAME))
            SCOREBOARD.updateScore(new Score("Modo: §a" + StringUtils.formatString(event.getNewValue()), "modo"));
    }

    public void createScoreboard(Player player) {
        SCOREBOARD.createScoreboard(player);
    }

    public static void createScore() {
        SCOREBOARD.clear();

        if (GameGeneral.getInstance().getGameState().isPregame()) {
            if (GameMain.getInstance().isTeamEnabled()) {
                SCOREBOARD.blankLine(11);
                SCOREBOARD.setScore(10, new Score("Aguardando: §7", "time"));
                SCOREBOARD.setScore(9, new Score("Jogadores: §70/80", "players"));
                SCOREBOARD.blankLine(8);
                SCOREBOARD.setScore(7, new Score("Kit: §bNenhum", "kit1"));
                SCOREBOARD.setScore(6, new Score("Liga: §8Void", "ranking"));
                SCOREBOARD.blankLine(5);
                SCOREBOARD.setScore(4, new Score("Modo: §aDupla", "modo"));
                SCOREBOARD.setScore(3, new Score("Dupla: §aNinguém", "pair"));
                SCOREBOARD.blankLine(2);
            } else {
                SCOREBOARD.blankLine(10);
                SCOREBOARD.setScore(9, new Score("Aguardando: §7", "time"));
                SCOREBOARD.setScore(8, new Score("Jogadores: §70/80", "players"));
                SCOREBOARD.blankLine(7);
                SCOREBOARD.setScore(6, new Score("Kit: §bNenhum", "kit1"));
                SCOREBOARD.setScore(5, new Score("Liga: §8Void", "ranking"));
                SCOREBOARD.blankLine(4);
                SCOREBOARD.setScore(3, new Score("Modo: §aSolo", "modo"));
                SCOREBOARD.blankLine(2);
            }
        } else {
            if (GameMain.getInstance().isTeamEnabled()) {
                SCOREBOARD.blankLine(12);
                SCOREBOARD.setScore(11, new Score("Invencivel por: §7", "time"));
                SCOREBOARD.setScore(10, new Score("Jogadores: §70/80", "players"));
                SCOREBOARD.blankLine(9);
                SCOREBOARD.setScore(8, new Score("Kit: §bNenhum", "kit1"));
                SCOREBOARD.setScore(7, new Score("Kills: §a0", "kills"));
                SCOREBOARD.setScore(6, new Score("Liga: §8Void", "ranking"));
                SCOREBOARD.blankLine(5);
                SCOREBOARD.setScore(4, new Score("Modo: §aDupla", "modo"));
                SCOREBOARD.setScore(3, new Score("Dupla: §aNinguém", "pair"));
                SCOREBOARD.blankLine(2);
            } else {
                SCOREBOARD.blankLine(11);
                SCOREBOARD.setScore(10, new Score("Invencivel por: §7", "time"));
                SCOREBOARD.setScore(9, new Score("Jogadores: §70/80", "players"));
                SCOREBOARD.blankLine(8);
                SCOREBOARD.setScore(7, new Score("Kit: §bNenhum", "kit1"));
                SCOREBOARD.setScore(6, new Score("Kills: §a0", "kills"));
                SCOREBOARD.setScore(5, new Score("Liga: §8Void", "ranking"));
                SCOREBOARD.blankLine(4);
                SCOREBOARD.setScore(3, new Score("Modo: §aSolo", "modo"));
                SCOREBOARD.blankLine(2);
            }
        }

        SCOREBOARD.setScore(1, new Score("§a§o" + CommonConst.SITE, "website"));
    }
}
