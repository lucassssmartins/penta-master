package br.com.pentamc.pvp.game;

import br.com.pentamc.bukkit.api.scoreboard.Score;
import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.pvp.PvPStatus;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.event.death.PlayerKilledEvent;
import br.com.pentamc.pvp.game.type.GameType;
import br.com.pentamc.pvp.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public abstract class Game {

    protected final GameType type;

    public abstract void load(Player human);

    public static void handleDeath(Player player, Player target) {
        Bukkit.getPluginManager().callEvent(new PlayerKilledEvent(player, target));

        User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());
        Game game = user.getGame();

        if (target != null) {
            User targetUser = GameMain.getPlugin().getUserController().getValue(target.getUniqueId());
            Game targetGame = targetUser.getGame();

            PvPStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(target.getUniqueId(), StatusType.PVP, PvPStatus.class);
            Scoreboard scoreboard = targetUser.getScoreboard();

            if (targetGame.getType().equals(GameType.BATTLE)) {
                status.getBattle().setKills(status.getBattle().getKills() + 1);
                status.getBattle().setActualStreak(status.getBattle().getActualStreak() + 1);

                status.save("battle");

                scoreboard.updateScore(target, new Score("Kills: §a" + status.getBattle().getKills(), "battle-kills"));
                scoreboard.updateScore(target, new Score("Streak: §a" + status.getBattle().getActualStreak(), "battle-streak"));
            } else if (targetGame.getType().equals(GameType.FPS)) {
                status.getFps().setKills(status.getFps().getKills() + 1);
                status.getFps().setActualStreak(status.getFps().getActualStreak() + 1);

                status.save("fps");

                scoreboard.updateScore(target, new Score("Kills: §a" + status.getFps().getKills(), "fps-kills"));
                scoreboard.updateScore(target, new Score("Streak: §a" + status.getFps().getActualStreak(), "fps-streak"));
            }

            status.addCoins(10);
            scoreboard.updateScore(target, new Score("Coins: §6" + status.getCoins(), "coins"));

            target.sendMessage(new String[] {
                    "§aVocê matou " + player.getName() + ".",
                    "§6+10 coins"
            });
        }

        PvPStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.PVP, PvPStatus.class);
        Scoreboard scoreboard = user.getScoreboard();

        if (game.getType().equals(GameType.BATTLE)) {
            status.getBattle().setDeaths(status.getBattle().getDeaths() + 1);

            if (status.getBattle().getMaxStreak() < status.getBattle().getActualStreak())
                status.getBattle().setMaxStreak(status.getBattle().getActualStreak());

            status.getBattle().setActualStreak(0);

            status.save("battle");

            scoreboard.updateScore(player, new Score("Deaths: §a" + status.getBattle().getDeaths(), "battle-deaths"));
            scoreboard.updateScore(player, new Score("Streak: §a" + status.getBattle().getActualStreak(), "battle-streak"));
        } else if (game.getType().equals(GameType.FPS)) {
            status.getFps().setDeaths(status.getFps().getDeaths() + 1);

            if (status.getFps().getMaxStreak() < status.getFps().getActualStreak())
                status.getFps().setMaxStreak(status.getFps().getActualStreak());

            status.getFps().setActualStreak(0);

            status.save("fps");

            scoreboard.updateScore(player, new Score("Deaths: §a" + status.getFps().getDeaths(), "fps-deaths"));
            scoreboard.updateScore(player, new Score("Streak: §a" + status.getFps().getActualStreak(), "fps-streak"));
        }

        player.sendMessage("§cVocê foi morto" + (target != null ? " por " + target.getName() : "") + ".");
    }
}
