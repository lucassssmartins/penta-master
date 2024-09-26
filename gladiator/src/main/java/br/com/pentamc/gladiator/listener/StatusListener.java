package br.com.pentamc.gladiator.listener;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.combat.CombatStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import br.com.pentamc.gladiator.event.GladiatorFinishEvent;

public class StatusListener implements Listener {

    public static final int MIN_ELO_REWARD  = 3; // xp minimo
    public static final int MAX_ELO_REWARD = 20; // xp maximo

    public static final int MIN_ELO_LOSE  = 2; // xp minimo
    public static final int MAX_ELO_LOSE = 6; // xp maximo

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerWarpDeath(GladiatorFinishEvent event) {
        Player player = event.getLoser();
        Player killer = event.getWinner();

        if (killer == null) {
            player.sendMessage("§cVocê morreu!");
            return;
        }

        CombatStatus playerStatus = CommonGeneral.getInstance().getStatusManager()
                                                 .loadStatus(player.getUniqueId(), StatusType.GLADIATOR,
                                                             CombatStatus.class);
        CombatStatus killerStatus = CommonGeneral.getInstance().getStatusManager()
                                                 .loadStatus(killer.getUniqueId(), StatusType.GLADIATOR,
                                                             CombatStatus.class);

        int xpReward = CommonConst.RANDOM.nextInt(MAX_ELO_REWARD - MIN_ELO_REWARD) + MIN_ELO_REWARD,
                xpLost = CommonConst.RANDOM.nextInt(MAX_ELO_LOSE - MIN_ELO_LOSE) + MIN_ELO_LOSE;


        player.sendMessage("§c" + player.getName() + " §efoi morto por §9" + killer.getName() + "§e.");
        player.sendMessage("§9" + killer.getName() + " §evenceu.");

        killer.sendMessage("§c" + player.getName() + " §efoi morto por §9" + killer.getName() + "§e.");
        killer.sendMessage("§9" + killer.getName() + " §evenceu.");

        if (event.getChallenge().isRanked()) {
            int eloReward = CommonConst.RANDOM.nextInt(MAX_ELO_REWARD - MIN_ELO_REWARD) + MIN_ELO_REWARD, eloLost = CommonConst.RANDOM.nextInt(MAX_ELO_LOSE - MIN_ELO_LOSE) + MIN_ELO_LOSE;

            killerStatus.addElo(eloReward);
            playerStatus.removeElo(eloLost);

            playerStatus.addDeath();
            playerStatus.resetKillstreak();

            killerStatus.addKill();
            killerStatus.addKillstreak();

            killer.sendMessage("§a+" + eloReward + " ELO"); //sou foda
            player.sendMessage("§a-" + eloLost + " ELO");
        }
    }
}
