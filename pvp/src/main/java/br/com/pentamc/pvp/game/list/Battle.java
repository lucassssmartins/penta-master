package br.com.pentamc.pvp.game.list;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.api.item.ActionItemStack;
import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.scoreboard.Score;
import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.pvp.PvPStatus;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.game.Game;
import br.com.pentamc.pvp.game.type.GameType;
import br.com.pentamc.pvp.inventory.KitInventory;
import br.com.pentamc.pvp.inventory.ShopInventory;
import br.com.pentamc.pvp.inventory.type.InventoryType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Battle extends Game {
    public Battle() {
        super(GameType.BATTLE);
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
            scoreboard = new SimpleScoreboard("§b§lARENA");

            user.setScoreboard(scoreboard);
        }

        scoreboard.clear();
        scoreboard.blankLine(11);

        scoreboard.setScore(10, new Score("Kills: §a" + status.getBattle().getKills(), "battle-kills"));
        scoreboard.setScore(9, new Score("Deaths: §a" + status.getBattle().getDeaths(), "battle-deaths"));
        scoreboard.setScore(8, new Score("Streak: §a" + status.getBattle().getActualStreak(), "battle-streak"));
        scoreboard.blankLine(7);

        scoreboard.setScore(6, new Score("Kit 1: §a" + user.getKitOne().getKit().getName(), "kit1"));
        scoreboard.setScore(5, new Score("Kit 2: §a" + user.getKitTwo().getKit().getName(), "kit2"));
        scoreboard.blankLine(4);

        scoreboard.setScore(3, new Score("Coins: §6" + status.getCoins(), "coins"));
        scoreboard.blankLine(2);

        scoreboard.setScore(1, new Score("§a§o" + CommonConst.SITE, "website"));

        scoreboard.createScoreboard(human);
    }

    protected void handleStack(Player human) {
        PlayerInventory inventory = human.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        inventory.setItem(0, new ActionItemStack(
                new ItemBuilder().type(Material.CHEST).name("§aSelecionar kit 1").build(),
                new ActionItemStack.Interact() {

                    @Override
                    public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
                        if (action.equals(ActionItemStack.ActionType.RIGHT))
                            new KitInventory(player, InventoryType.PRIMARY, 1);

                        return false;
                    }
                }).getItemStack());

        inventory.setItem(1, new ActionItemStack(
                new ItemBuilder().type(Material.CHEST).name("§aSelecionar kit 2").build(),
                new ActionItemStack.Interact() {

                    @Override
                    public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
                        if (action.equals(ActionItemStack.ActionType.RIGHT))
                            new KitInventory(player, InventoryType.SECONDARY, 1);

                        return false;
                    }
                }).getItemStack());

        inventory.setItem(4, new ActionItemStack(
                new ItemBuilder().type(Material.EMERALD).name("§aLoja de kits").build(),
                new ActionItemStack.Interact() {

                    @Override
                    public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
                        new ShopInventory(player, 1);

                        return false;
                    }
                }).getItemStack());

        inventory.setItem(8, new ActionItemStack(
                new ItemBuilder().type(Material.BED).name("§aVoltar ao lobby").build(),
                new ActionItemStack.Interact() {

                    @Override
                    public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
                        BukkitMain.getInstance().sendServer(player, ServerType.LOBBY_PVP);
                        return false;
                    }
                }).getItemStack());
    }
}
