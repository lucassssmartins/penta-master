package br.com.pentamc.gladiator;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.MemberVoid;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.combat.CombatStatus;
import br.com.pentamc.gladiator.listener.*;
import br.com.pentamc.gladiator.menu.CustomInventory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import br.com.pentamc.bukkit.api.character.Character;
import br.com.pentamc.bukkit.api.hologram.impl.CraftHologram;
import br.com.pentamc.bukkit.api.hologram.impl.TopRanking;
import br.com.pentamc.bukkit.command.BukkitCommandFramework;
import br.com.pentamc.bukkit.listener.register.CombatListener;
import br.com.pentamc.gladiator.command.DefaultCommand;
import br.com.pentamc.gladiator.command.SpectatorCommand;
import br.com.pentamc.gladiator.controller.GamerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        BukkitMain.getInstance().createCharacter("§b§lEDITAR", "Knight_of_Essen", "inv", new Character.Interact() {

            @Override
            public boolean onInteract(Player player, boolean right) {
                Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

                new CustomInventory(player, member);
                return false;
            }
        }, createEditor());

        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lWINS",
                                           BukkitMain.getInstance().getLocationFromConfig("topranking-hologram-wins")),
                         () -> {

                             List<TopRanking.RankingModel<CombatStatus>> list = new ArrayList<>();
                             Collection<CombatStatus> ranking = CommonGeneral.getInstance().getStatusData()
                                                                             .ranking(StatusType.GLADIATOR, "kills",
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

        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lELO",
                                           BukkitMain.getInstance().getLocationFromConfig("topranking-hologram-elo")),
                         () -> {

                             List<TopRanking.RankingModel<CombatStatus>> list = new ArrayList<>();
                             Collection<CombatStatus> ranking = CommonGeneral.getInstance().getStatusData()
                                                                             .ranking(StatusType.GLADIATOR, "elo",
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
                                                 (model == null ? 0 : model.getStatus().getElo()));

        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lWINSTREAK",
                                           BukkitMain.getInstance().getLocationFromConfig("topranking-hologram-winstreak")),
                         () -> {

                             List<TopRanking.RankingModel<CombatStatus>> list = new ArrayList<>();
                             Collection<CombatStatus> ranking = CommonGeneral.getInstance().getStatusData()
                                                                             .ranking(StatusType.GLADIATOR,
                                                                                      "killstreak", CombatStatus.class);

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
                                                 (model == null ? 0 : model.getStatus().getKillstreak()));

        super.onEnable();
    }

    @Override
    public void onDisable() {

        gameGeneral.onDisable();

        super.onDisable();
    }

    public CraftHologram createEditor() {
       CraftHologram craft = new CraftHologram("§b§lEDITAR", new Location(Bukkit.getWorlds().stream().findFirst().orElse(null), 0, 0, 0));

       craft.addLineBelow("§eClique aqui!");
       return craft;
    }

    public void loadListener() {
        Bukkit.getPluginManager().registerEvents(new ArenaListener(), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new GladiatorListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new CombatListener(), this);
        Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);
        Bukkit.getPluginManager().registerEvents(new StatusListener(), this);
        Bukkit.getPluginManager().registerEvents(new RankingListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }
}
