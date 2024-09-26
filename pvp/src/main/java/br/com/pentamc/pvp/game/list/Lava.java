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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Lava extends Game {
    public Lava() {
        super(GameType.LAVA);
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
            scoreboard = new SimpleScoreboard("§b§lLAVA");

            user.setScoreboard(scoreboard);
        }

        scoreboard.clear();
        scoreboard.setDisplayName("§b§lLAVA");
        scoreboard.blankLine(4);

        scoreboard.setScore(3, new Score("§fCoins: §6" + status.getCoins(), "coins"));
        scoreboard.blankLine(2);

        scoreboard.setScore(1, new Score("§a§o" + CommonConst.SITE, "website"));

        scoreboard.createScoreboard(human);
    }

    protected void handleStack(Player human) {
        PlayerInventory inventory = human.getInventory();

        inventory.clear();

        inventory.setItem(13, new ItemStack(Material.BOWL, 32));
        inventory.setItem(14, new ItemStack(Material.RED_MUSHROOM, 32));
        inventory.setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 32));

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
    }
}
