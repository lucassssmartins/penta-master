package br.com.pentamc.bukkit.menu.report;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.MemberVoid;
import br.com.pentamc.common.report.Report;
import br.com.pentamc.common.tag.Tag;
import br.com.pentamc.common.utils.DateUtils;
import br.com.pentamc.common.utils.string.MessageBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.bukkit.api.menu.types.ConfirmInventory;
import br.com.pentamc.bukkit.api.menu.types.ConfirmInventory.ConfirmHandler;

public class ReportInventory {

    public ReportInventory(Player player, Report report) {
        MenuInventory menu = new MenuInventory("§7Report do " + report.getPlayerName(), 5);

        Member rPlayer = CommonGeneral.getInstance().getMemberManager().getMember(report.getPlayerUniqueId());

        if (rPlayer == null) {
            rPlayer = new MemberVoid(
                    CommonGeneral.getInstance().getPlayerData().loadMember(report.getPlayerUniqueId()));
        }

        final Member reportPlayer = rPlayer;

        create(player, reportPlayer, report, menu);
        menu.setUpdateHandler((p, m) -> create(player, reportPlayer, report, menu));

        menu.open(player);
    }

    private void create(Player player, Member reportPlayer, Report report, MenuInventory menu) {
        String tag = Tag.valueOf(reportPlayer.getGroup().name()).getPrefix() +
                     (ChatColor.stripColor(Tag.valueOf(reportPlayer.getGroup().name()).getPrefix()).trim().length() >
                      0 ? " " : "");

        if (report.isOnline()) {
            menu.setItem(13, new ItemBuilder().type(Material.SKULL_ITEM).durability(3)
                                              .lore("", "§fServidor: §a" + reportPlayer.getServerId(),
                                                    "§fExpira em: §a" + DateUtils.getTime(report.getReportExpire()), "",
                                                    "§aO jogador está online no momento!")
                                              .name(tag + " " + report.getPlayerName()).skin(report.getPlayerName())
                                              .build());
        } else {
            menu.setItem(13, new ItemBuilder().type(Material.SKULL_ITEM).durability(3)
                                              .lore("", "§fServidor: §a" + reportPlayer.getServerId(),
                                                    "§fExpira em: §a" + DateUtils.getTime(report.getReportExpire()))
                                              .name(tag + " " + report.getPlayerName()).skin(report.getPlayerName())
                                              .build());
        }

        menu.setItem(29, new ItemBuilder().type(Material.COMPASS).name("§aClique para teletransportar!").build(),
                     (p, inv, type, stack, slot) -> {
                         if (!report.isOnline()) {
                             p.sendMessage("§cO jogador " + report.getPlayerName() + " não está online no momento!");
                             return false;
                         }

                         p.spigot().sendMessage(new MessageBuilder("§aClique aqui para teletransportar!").setClickEvent(
                                                                                                                 new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp " + report.getPlayerName()))
                                                                                                         .create());
                         return false;
                     });

        menu.setItem(30, new ItemBuilder().type(Material.BOOK_AND_QUILL).name("§aLista de reports")
                                          .lore("\n§7Motivo: §f" + report.getLastReport().getReason() +
                                                "\n§fPlayer: §f" + report.getLastReport().getPlayerName() +
                                                "\n\n§fNumero de reports: §a" + report.getPlayersReason().size())
                                          .build(), (p, inv, type, stack, slot) -> {
            new ReportInformationListInventory(player, report, menu, 1);
            return false;
        });

        menu.setItem(31, new ItemBuilder().type(Material.REDSTONE_BLOCK).name("§aDeletar report").build(),
                     (p, inv, type, stack, slot) -> {
                         new ConfirmInventory(player, "§7Deletar report", new ConfirmHandler() {

                             @Override
                             public void onConfirm(boolean confirmed) {
                                 if (confirmed) {
                                     report.expire();
                                     new ReportListInventory(p, 1);
                                 } else {
                                     menu.open(p);
                                 }
                             }
                         }, menu);
                         return false;
                     });

        menu.setItem(33, new ItemBuilder().type(Material.ARROW).name("§aLista de report").build(),
                     (p, inv, type, stack, slot) -> {
                         new ReportListInventory(p, 1);
                         return false;
                     });
    }
}
