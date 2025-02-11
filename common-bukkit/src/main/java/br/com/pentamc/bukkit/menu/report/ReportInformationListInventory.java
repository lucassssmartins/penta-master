package br.com.pentamc.bukkit.menu.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.com.pentamc.common.report.Report;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.bukkit.api.menu.click.ClickType;
import br.com.pentamc.bukkit.api.menu.click.MenuClickHandler;

public class ReportInformationListInventory {
	private static int itemsPerPage = 36;

	public ReportInformationListInventory(Player player, Report report, MenuInventory topInventory, int page) {
		List<Report.ReportInformation> reports = new ArrayList<>(report.getPlayersReason().values());

		Collections.sort(reports, new Comparator<Report.ReportInformation>() {
			@Override
			public int compare(Report.ReportInformation o1, Report.ReportInformation o2) {
				if (o1.getReportTime() > o2.getReportTime())
					return 1;
				else if (o1.getReportTime() == o2.getReportTime())
					return 0;
				return -1;
			}
		});

		MenuInventory menu = new MenuInventory("§7Informações do report", 6);

		if (topInventory != null) {
			menu.setItem(4, new ItemBuilder().type(Material.ARROW).name("§aVoltar").build(), new MenuClickHandler() {

				@Override
				public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
					topInventory.open(p);
					return false;
				}
			});
		}

		int pageStart = 0;
		int pageEnd = itemsPerPage;

		if (page > 1) {
			pageStart = ((page - 1) * itemsPerPage);
			pageEnd = (page * itemsPerPage);
		}

		if (pageEnd > reports.size()) {
			pageEnd = reports.size();
		}

		if (page == 1) {
			menu.setItem(0, new ItemBuilder().type(Material.INK_SACK).durability(8).name("§cPágina anterior").build());
		} else {
			menu.setItem(0, new ItemBuilder().type(Material.INK_SACK).durability(10).name("§aPágina anterior")
					.lore("\n§7Clique para voltar de página").build(), new MenuClickHandler() {
						@Override
						public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
							new ReportInformationListInventory(player, report, topInventory, page - 1);
							return false;
						}
					});
		}

		if (Math.ceil(reports.size() / itemsPerPage) + 1 > page) {
			menu.setItem(8, new ItemBuilder().type(Material.INK_SACK).durability(10).name("§aPágina posterior")
					.lore("\n§7Clique para avançar de página").build(), new MenuClickHandler() {
						@Override
						public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
							new ReportInformationListInventory(player, report, topInventory, page + 1);
							return false;
						}
					});
		} else {
			menu.setItem(8, new ItemBuilder().type(Material.INK_SACK).durability(8).name("§cPágina posterior").build());
		}

		// REPORT LIST

		int w = 9;

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		for (int i = pageStart; i < pageEnd; i++) {
			Report.ReportInformation reportInfo = reports.get(i);
			
			menu.setItem(w,
					new ItemBuilder().type(Material.SKULL_ITEM).name("§e" + reportInfo.getPlayerName())
							.skin(reportInfo.getPlayerName()).durability(3)
							.lore("", "§7Motivo: §f" + reportInfo.getReason(),
									"§7Date: §f" + df.format(new Date(reportInfo.getReportTime())), "",
									"§7Reputação: §f" + reportInfo.getReportLevel())
							.build());

			if (w % 9 == 7) {
				w += 3;
				continue;
			}
			
			w += 1;
		}

		ItemStack nullItem = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(15).name(" ").build();

		for (int i = 0; i < 9; i++) {
			if (menu.getItem(i) == null)
				menu.setItem(i, nullItem);
		}

		menu.open(player);
	}

}
