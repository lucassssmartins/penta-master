package br.com.pentamc.pvp.game.list;

import br.com.pentamc.bukkit.api.scoreboard.Score;
import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.pvp.PvPStatus;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.game.Game;
import br.com.pentamc.pvp.game.type.GameType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class FPS extends Game {
    public FPS() {
        super(GameType.FPS);
    }

    @Override
    public void load(Player human) {
        human.teleport(Bukkit.getWorld(getType().getWorldName()).getSpawnLocation());
        human.setHealth(20.0D);

        handleScoreboard(human);
        handleStack(human);
    }

    protected void handleScoreboard(Player human) {
        User user = GameMain.getPlugin().getUserController().getValue(human.getUniqueId());
        PvPStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(human.getUniqueId(), StatusType.PVP, PvPStatus.class);
        Scoreboard scoreboard;

        if (user.getScoreboard() != null)
            scoreboard = user.getScoreboard();
        else {
            scoreboard = new SimpleScoreboard("§b§lFPS");

            user.setScoreboard(scoreboard);
        }

        scoreboard.clear();
        scoreboard.setDisplayName("§b§lFPS");
        scoreboard.blankLine(8);

        scoreboard.setScore(7, new Score("Kills: §a" + status.getFps().getKills(), "fps-kills"));
        scoreboard.setScore(6, new Score("Deaths: §a" + status.getFps().getDeaths(), "fps-deaths"));
        scoreboard.setScore(5, new Score("Streak §a" + status.getFps().getActualStreak(), "fps-streak"));
        scoreboard.blankLine(4);

        scoreboard.setScore(3, new Score("Coins: §6" + status.getCoins(), "coins"));
        scoreboard.blankLine(2);

        scoreboard.setScore(1, new Score("§a§o" + CommonConst.SITE, "website"));

        scoreboard.createScoreboard(human);
    }

    protected void handleStack(Player human) {
        PlayerInventory inventory = human.getInventory();

        inventory.clear();
    }
}
