package br.com.pentamc.pvp.hologram;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.api.hologram.impl.CraftHologram;
import br.com.pentamc.bukkit.api.hologram.impl.TopRanking;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.MemberVoid;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.pvp.PvPStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameRankingHologram {

    public void registry() {
        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lKILLS", BukkitMain.getInstance().getLocationFromConfig("arena-kills")), () -> {
            Collection<PvPStatus> ranking = BukkitMain.getInstance().getGeneral().getStatusData().ranking(StatusType.PVP, "arena", PvPStatus.class);
            List<TopRanking.RankingModel<PvPStatus>> model = new ArrayList<>();

            for (PvPStatus status : ranking) {
                Member member = CommonGeneral.getInstance().getPlayerData().loadMember(status.getUniqueId(), MemberVoid.class);

                if (member == null)
                    break;

                model.add(new TopRanking.RankingModel<>(status, member.getPlayerName(), member.getServerGroup()));
            }

            return model;
        }, (model, position) -> "§e" + position + ". " + (model == null ? "§7Ninguém" : model.getGroup().getColor() + model.getPlayerName()) + " §7- §e" + (model == null ? 0 : model.getStatus().getBattle().getKills()));

        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lKILLS", BukkitMain.getInstance().getLocationFromConfig("fps-kills")), () -> {
            Collection<PvPStatus> ranking = BukkitMain.getInstance().getGeneral().getStatusData().ranking(StatusType.PVP, "fps", PvPStatus.class);
            List<TopRanking.RankingModel<PvPStatus>> model = new ArrayList<>();

            for (PvPStatus status : ranking) {
                Member member = CommonGeneral.getInstance().getPlayerData().loadMember(status.getUniqueId(), MemberVoid.class);

                if (member == null)
                    break;

                model.add(new TopRanking.RankingModel<>(status, member.getPlayerName(), member.getServerGroup()));
            }

            return model;
        }, (model, position) -> "§e" + position + ". " + (model == null ? "§7Ninguém" : model.getGroup().getColor() + model.getPlayerName()) + " §7- §e" + (model == null ? 0 : model.getStatus().getFps().getKills()));
    }
}
