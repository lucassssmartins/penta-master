package br.com.pentamc.pvp.listener.server;

import br.com.pentamc.bukkit.api.scoreboard.Score;
import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.pvp.PvPStatus;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.game.Game;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SignListener implements Listener {

    @EventHandler
    public void sign(SignChangeEvent event) {
        Player player = event.getPlayer();

        if (event.getLine(0).equalsIgnoreCase("sopa")) {
            event.setLine(0, "§c§m-§6§m-§e§m-§a§m-§b§m-");
            event.setLine(1, "§bPenta");
            event.setLine(2, "§7» §8Sopas");
            event.setLine(3, "§c§m-§6§m-§e§m-§a§m-§b§m-");
        }

        if (event.getLine(0).equalsIgnoreCase("recraft")) {
            event.setLine(0, "§c§m-§6§m-§e§m-§a§m-§b§m-");
            event.setLine(1, "§bPenta");
            event.setLine(2, "§7» §8Recraft");
            event.setLine(3, "§c§m-§6§m-§e§m-§a§m-§b§m-");
        }

        if (event.getLine(0).equalsIgnoreCase("facil")) {
            event.setLine(0, "§c§m-§6§m-§e§m-§a§m-§b§m-");
            event.setLine(1, "§bPenta");
            event.setLine(2, "§7» §8Fácil");
            event.setLine(3, "§c§m-§6§m-§e§m-§a§m-§b§m-");
        }

        if (event.getLine(0).equalsIgnoreCase("medio")) {
            event.setLine(0, "§c§m-§6§m-§e§m-§a§m-§b§m-");
            event.setLine(1, "§bPenta");
            event.setLine(2, "§7» §8Médio");
            event.setLine(3, "§c§m-§6§m-§e§m-§a§m-§b§m-");
        }

        if (event.getLine(0).equalsIgnoreCase("dificil")) {
            event.setLine(0, "§c§m-§6§m-§e§m-§a§m-§b§m-");
            event.setLine(1, "§bPenta");
            event.setLine(2, "§7» §8Difícil");
            event.setLine(3, "§c§m-§6§m-§e§m-§a§m-§b§m-");
        }

        if (event.getLine(0).equalsIgnoreCase("extremo")) {
            event.setLine(0, "§c§m-§6§m-§e§m-§a§m-§b§m-");
            event.setLine(1, "§bPenta");
            event.setLine(2, "§7» §8Extremo");
            event.setLine(3, "§c§m-§6§m-§e§m-§a§m-§b§m-");
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());
        Game game = user.getGame();

        if (event.getClickedBlock() != null) {
            Block block = event.getClickedBlock();

            if (block.getState() instanceof Sign && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Sign sign = (Sign) block.getState();

                if (sign.getLine(2).equalsIgnoreCase("§7» §8Sopas")) {
                    Inventory inventory = Bukkit.createInventory(player, 9 * 5, "Sopas");

                    for (int i = 0; i < inventory.getSize(); i++)
                        inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));

                    player.openInventory(inventory);
                }

                if (sign.getLine(2).equalsIgnoreCase("§7» §8Recraft")) {
                    Inventory inventory = Bukkit.createInventory(player, 9 * 3, "Recraft");

                    for (int i = 0; i < 9; i++)
                        inventory.setItem(i, new ItemStack(Material.BOWL, 64));

                    for (int i = 9; i < 18; i++)
                        inventory.setItem(i, new ItemStack(Material.RED_MUSHROOM, 64));

                    for (int i = 18; i < 27; i++)
                        inventory.setItem(i, new ItemStack(Material.BROWN_MUSHROOM, 64));

                    player.openInventory(inventory);
                }

                if (sign.getLine(2).equalsIgnoreCase("§7» §8Fácil")) {
                    PvPStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.PVP, PvPStatus.class);
                    Scoreboard scoreboard = user.getScoreboard();

                    status.addCoins(1);
                    scoreboard.updateScore(player, new Score("Coins: §6" + status.getCoins(), "coins"));

                    game.load(player);
                    player.sendMessage(new String[] {
                            "§aVocê completou o nível fácil.",
                            "§6+1 coin"
                    });
                }

                if (sign.getLine(2).equalsIgnoreCase("§7» §8Médio")) {
                    PvPStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.PVP, PvPStatus.class);
                    Scoreboard scoreboard = user.getScoreboard();

                    status.addCoins(7);
                    scoreboard.updateScore(player, new Score("Coins: §6" + status.getCoins(), "coins"));

                    game.load(player);
                    player.sendMessage(new String[] {
                            "§aVocê completou o nível médio.",
                            "§6+7 coins"
                    });
                }

                if (sign.getLine(2).equalsIgnoreCase("§7» §8Difícil")) {
                    PvPStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.PVP, PvPStatus.class);
                    Scoreboard scoreboard = user.getScoreboard();

                    status.addCoins(25);
                    scoreboard.updateScore(player, new Score("Coins: §6" + status.getCoins(), "coins"));

                    game.load(player);
                    player.sendMessage(new String[] {
                            "§aVocê completou o nível difícil.",
                            "§6+25 coins"
                    });
                }

                if (sign.getLine(2).equalsIgnoreCase("§7» §8Extremo")) {
                    PvPStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.PVP, PvPStatus.class);
                    Scoreboard scoreboard = user.getScoreboard();

                    status.addCoins(50);
                    scoreboard.updateScore(player, new Score("Coins: §6" + status.getCoins(), "coins"));

                    game.load(player);
                    player.sendMessage(new String[] {
                            "§aVocê completou o nível extremo.",
                            "§6+50 coins"
                    });
                }
            }
        }
    }
}
