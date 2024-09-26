package br.com.pentamc.shadow;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.MemberVoid;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.combat.CombatStatus;
import br.com.pentamc.common.account.status.types.game.GameStatus;
import br.com.pentamc.shadow.controller.GamerManager;
import br.com.pentamc.shadow.listener.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.api.hologram.impl.CraftHologram;
import br.com.pentamc.bukkit.api.hologram.impl.TopRanking;
import br.com.pentamc.bukkit.command.BukkitCommandFramework;
import br.com.pentamc.bukkit.listener.register.CombatListener;
import br.com.pentamc.shadow.command.DefaultCommand;
import br.com.pentamc.shadow.command.SpectatorCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Testando nesse plugin um modelo aberto Onde cada classe tem sua função bem
 * definida e as outras não podem interferir
 *
 * @author Allan
 */

@Getter
public class GameMain extends JavaPlugin {

    @Getter
    private static GameMain instance;

    private GameGeneral gameGeneral;

    private GamerManager gamerManager;

    @Override
    public void onLoad() {
        instance = this;
        gameGeneral = new GameGeneral();

        gameGeneral.onLoad();
        super.onLoad();
    }

    @Override
    public void onEnable() {
        loadListener();
        gameGeneral.onEnable();

        gamerManager = new GamerManager();

        BukkitCommandFramework.INSTANCE.registerCommands(new DefaultCommand());
        BukkitCommandFramework.INSTANCE.registerCommands(new SpectatorCommand());

        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lWINS",
                                           BukkitMain.getInstance().getLocationFromConfig("topranking-hologram-wins")),
                         () -> {

                             List<TopRanking.RankingModel<CombatStatus>> list = new ArrayList<>();
                             Collection<CombatStatus> ranking = CommonGeneral.getInstance().getStatusData()
                                                                             .ranking(StatusType.SHADOW, "kills",
                                                                                      CombatStatus.class);

                             for (CombatStatus wins : ranking) {
                                 Member member = CommonGeneral.getInstance().getPlayerData()
                                                              .loadMember(wins.getUniqueId(), MemberVoid.class);

                                 list.add(new TopRanking.RankingModel<>(wins, member.getPlayerName(),
                                                                        member.getServerGroup()));
                             }

                             return list;
                         }, (model, position) -> "§e" + position + ". " + (model == null ? "§7Ninguém" :
                                                                           model.getGroup().getColor() +
                                                                           model.getPlayerName()) + " §7- §e" +
                                                 (model == null ? 0 : model.getStatus().getKills()));

        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lRANKING", BukkitMain.getInstance().getLocationFromConfig(
                "topranking-hologram-ranking")),
                () -> {
                    List<TopRanking.RankingModel<GameStatus>> list = new ArrayList<>();
                    Collection<GameStatus> ranking = CommonGeneral.getInstance().getStatusData()
                            .ranking(StatusType.SHADOW, "xp",
                                    GameStatus.class);

                    for (GameStatus wins : ranking) {
                        Member member = CommonGeneral.getInstance().getPlayerData()
                                .loadMember(wins.getUniqueId(), MemberVoid.class);

                        list.add(new TopRanking.RankingModel<>(wins, member.getPlayerName(),
                                member.getServerGroup()));
                    }

                    return list;
                },
                (model, position) ->
                        "§e" + position + ". " + (model == null ? "§7Ninguém" :
                                model.getGroup().getColor() +
                                        model.getPlayerName()) + " §7- §e" +
                                (model == null ? 0 : model.getStatus().getXp()));

        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lWINSTREAK", BukkitMain.getInstance().getLocationFromConfig(
                "topranking-hologram-winstreak")), () -> {

            List<TopRanking.RankingModel<CombatStatus>> list = new ArrayList<>();
            Collection<CombatStatus> ranking = CommonGeneral.getInstance().getStatusData()
                                                            .ranking(StatusType.SHADOW, "killstreak",
                                                                     CombatStatus.class);

            for (CombatStatus wins : ranking) {
                Member member = CommonGeneral.getInstance().getPlayerData()
                                             .loadMember(wins.getUniqueId(), MemberVoid.class);

                list.add(new TopRanking.RankingModel<>(wins, member.getPlayerName(), member.getServerGroup()));
            }

            return list;
        }, (model, position) -> "§e" + position + ". " +
                                (model == null ? "§7Ninguém" : model.getGroup().getColor() + model.getPlayerName()) +
                                " §7- §e" + (model == null ? 0 : model.getStatus().getKillstreak()));

        super.onEnable();
    }

    @Override
    public void onDisable() {

        gameGeneral.onDisable();

        super.onDisable();
    }

    public void loadListener() {
        Bukkit.getPluginManager().registerEvents(new ArenaListener(), this);
        Bukkit.getPluginManager().registerEvents(new GladiatorListener(), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new CombatListener(), this);
        Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);
        Bukkit.getPluginManager().registerEvents(new StatusListener(), this);
        Bukkit.getPluginManager().registerEvents(new RankingListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
    }
}
