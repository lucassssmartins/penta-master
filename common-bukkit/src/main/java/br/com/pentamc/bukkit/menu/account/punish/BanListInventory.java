package br.com.pentamc.bukkit.menu.account.punish;

import br.com.pentamc.bukkit.menu.ListInventory;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.ban.constructor.Ban;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.utils.DateUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuItem;

public class BanListInventory extends ListInventory<Ban> {

	public BanListInventory(Player player, Member member, int page) {
		super("§7§nHistórico de banimentos", member.getPunishmentHistory().getBanList(), 6, 28, page, 10,
				new ItemHandler<Ban>() {

					@Override
					public MenuItem handleItem(Ban ban, int index) {

						ItemBuilder itemBuilder = new ItemBuilder();

						itemBuilder.name("§aBanimento #" + (index + 1));
						itemBuilder.type(Material.PAPER);

						itemBuilder.lore("");

						boolean staff = Member.hasGroupPermission(player.getUniqueId(), Group.TRIAL);

						if (staff) {
							itemBuilder.lore("§7Autor: §f" + ban
									.getBannedBy());
						}

						itemBuilder.lore(
										"§7Tempo: §f"
												+ (ban.isPermanent() ? "Permanente"
												: "alguns dias "
												+ (ban.hasExpired() ? "§8(expirado)"
												: "§8(Tempo restante: "
												+ DateUtils.getTime(ban.getBanExpire()))
												+ ")"),
										"§7Motivo: §f" + ban.getCategory().getReason(), "",
										"§8Banimento de ID " + ban.getId());

						itemBuilder.lore(
								ban.isUnbanned() ? "§aBanimento revogado" + (staff ?" pelo " + ban.getUnbannedBy() : "")
										: ban.hasExpired() ? "§eBanimento expirado!" : "§cBanimento ativado!");

						return new MenuItem(itemBuilder
								.build());
					}

				}, "§cNenhum banimento registrado!");

		if (page == 1)
			getMenu().setItem(45, new ItemBuilder().name("§aVoltar!").type(Material.ARROW).build(),
					(p, inv, type, stack, slot) -> {
						new PunishmentInventory(player, member);
						return false;
					});

		openInventory(player);
	}

}
