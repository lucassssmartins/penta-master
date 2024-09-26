package br.com.pentamc.bukkit.menu.account.punish;

import br.com.pentamc.bukkit.menu.account.AccountInventory;
import br.com.pentamc.common.account.Member;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;

public class PunishmentInventory {

	public PunishmentInventory(Player player, Member member) {
		MenuInventory menu = new MenuInventory("§7§nPunições de " + member.getPlayerName(), 7);

		menu.setItem(13, new ItemBuilder().type(Material.SKULL_ITEM).durability(3).name("§a" + member.getPlayerName())
				.skin(member.getPlayerName()).build());

		menu.setItem(30,
				new ItemBuilder().name("§aHistórico de banimentos")
						.lore("", "§7Veja a lista de banimentos", "",
								"§8" + member.getPunishmentHistory().getBanList().size() + " banimentos registrados!",
								"§eClique aqui para ver.")
						.type(Material.PAPER).build(),
				(p, inv, type, stack, slot) -> {
					new BanListInventory(player, member, 1);
					return false;
				});
		menu.setItem(31,
				new ItemBuilder().name("§aHistórico de silenciamentos")
						.lore("", "§7Veja a lista de silenciamentos", "",
								"§8" + member.getPunishmentHistory().getMuteList().size() + " mutes registrados!",
								"§eClique aqui para ver.")
						.type(Material.PAPER).build());
		menu.setItem(32,
				new ItemBuilder().name("§aHistórico de avisos")
						.lore("", "§7Veja a lista de avisos", "",
								"§8" + member.getPunishmentHistory().getWarnList().size() + " avisos registrados!",
								"§eClique aqui para ver.")
						.type(Material.PAPER).build());

		menu.setItem(49, new ItemBuilder().name("§aVoltar").type(Material.ARROW).build(),
				(p, inv, type, stack, slot) -> {
					new AccountInventory(player, member);
					return false;
				});

		menu.open(player);
	}

}
