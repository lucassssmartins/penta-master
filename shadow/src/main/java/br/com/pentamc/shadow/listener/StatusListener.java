package br.com.pentamc.shadow.listener;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.GameStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import br.com.pentamc.shadow.event.GladiatorFinishEvent;

public class StatusListener implements Listener {

    public static final int MIN_XP_REWARD  = 8; // xp minimo
    public static final int MAX_XP_REWARD = 27; // xp maximo

    public static final int MIN_XP_LOSE  = 4; // xp minimo
    public static final int MAX_XP_LOSE = 9; // xp maximo

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerWarpDeath(GladiatorFinishEvent event) {
        Player player = event.getLoser();
        Player killer = event.getWinner();

        if (killer == null) {
            player.sendMessage("§cVocê morreu!");
            return;
        }

        GameStatus playerStatus = CommonGeneral.getInstance().getStatusManager()
                                                 .loadStatus(player.getUniqueId(), StatusType.SHADOW,
                                                         GameStatus.class);
        GameStatus killerStatus = CommonGeneral.getInstance().getStatusManager()
                                                 .loadStatus(killer.getUniqueId(), StatusType.SHADOW,
                                                         GameStatus.class);

        int xpReward = CommonConst.RANDOM.nextInt(MAX_XP_REWARD - MIN_XP_REWARD) + MIN_XP_REWARD,
                xpLost = CommonConst.RANDOM.nextInt(MAX_XP_LOSE - MIN_XP_LOSE) + MIN_XP_LOSE;

        player.sendMessage("§c" + player.getName() + " §efoi morto por §9" + killer.getName() + "§e.");
        player.sendMessage("§9" + killer.getName() + " §evenceu.");

        killer.sendMessage("§c" + player.getName() + " §efoi morto por §9" + killer.getName() + "§e.");
        killer.sendMessage("§9" + killer.getName() + " §evenceu.");

        if (event.getChallenge().isRanked()) {
            killer.sendMessage("§b+" + xpReward + " XP");
            player.sendMessage("§b-" + xpLost + " XP");

            playerStatus.addDeath();
            playerStatus.resetKillstreak();

            killerStatus.addKill();
            killerStatus.addKillstreak();

            CommonGeneral.getInstance().getMemberManager().getMember(killer.getUniqueId()).addXp(StatusType.SHADOW, xpReward);
            CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()).removeXp(StatusType.SHADOW, xpLost);
        }
    }
}
