package br.com.pentamc.bukkit.menu.account;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.bukkit.api.menu.click.ClickType;
import br.com.pentamc.bukkit.api.menu.click.MenuClickHandler;
import br.com.pentamc.bukkit.menu.account.PreferencesInventory;
import br.com.pentamc.bukkit.menu.account.SkinInventory;
import br.com.pentamc.bukkit.menu.account.SkinInventory.MenuType;
import br.com.pentamc.bukkit.menu.account.StatusInventory;

public class AccountInventory {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public AccountInventory(Player owner, Member member) {
		MenuInventory inv = new MenuInventory("Perfil", 5);

		inv.setItem(13, new ItemBuilder().type(Material.SKULL_ITEM).durability(3).skin(member.getName())
				.name(member.getGroup() == Group.MEMBRO ? "§7" + member.getName() :
						Tag.getByName(member.getGroup().toString()).getPrefix() + " " +
								member.getName()).lore("", "§7Primeiro login: §f" + DATE_FORMAT.format(
						new Date(member.getFirstLogin())), "§7Ultimo login: §f" +
						DATE_FORMAT.format(new Date(member.getFirstLogin())))
				.build());

		inv.setItem(30, new ItemBuilder().type(Material.PAPER).name("§aVer estatísticas")
						.lore(
								"§7Veja suas estatísticas",
								"§7nos jogos.",
								"",
								"§eClique para ver!"
						).build(),
				(player1, inv1, type, stack, slot) -> {
					new StatusInventory(player1, member, StatusInventory.StatsCategory.PRINCIPAL);
					return false;
				});

		inv.setItem(31, new ItemBuilder().type(Material.ITEM_FRAME).name("§aSua skin")
						.lore(
								"§7Clique aqui para alterar",
								"§7sua aparência.",
								"",
								"§eClique para ver!"
						).build(),
				(player1, inv1, type, stack, slot) -> {
					if (owner.getUniqueId().equals(member.getUniqueId())) {
						new SkinInventory(owner, member, MenuType.GENERAL);
					} else {
						owner.sendMessage("§cVocê não pode alterar a skin de outro jogador.");
					}

					return false;
				});

		inv.setItem(32, new ItemBuilder().type(Material.REDSTONE_COMPARATOR).name("§aPreferências")
						.lore(
								"§7Customize sua performance",
								"§7de acordo com suas preferências.",
								"",
								"§eClique para ver!"
						).build(),
				new MenuClickHandler() {

					@Override
					public boolean onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (owner.getUniqueId().equals(member.getUniqueId())) {
							new PreferencesInventory(owner, getClass());
						} else {
							owner.sendMessage("§cVocê não pode alterar as preferências de outro jogador.");
						}

						return false;
					}
				});

		inv.open(owner);
	}
}
