package br.com.pentamc.competitive.menu.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.kit.Kit;
import br.com.pentamc.competitive.kit.KitType;
import br.com.pentamc.common.CommonConst;
import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.common.utils.string.NameUtils;

public class InfoInventory {

	public InfoInventory(Player player, Kit kit, KitType kitType) {

		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
		MenuInventory menu = new MenuInventory("§7Kit " + NameUtils.formatString(kit.getName()), 6, true);
		
		boolean hasKit = gamer.hasKit(kit.getName());

		menu.setItem(13, new ItemBuilder().name((hasKit ? "§a" : "§c") + NameUtils.formatString(kit.getName()))
				.type(kit.getKitIcon().getType()).lore(!hasKit ? "\n§c§cCompre em: §e" + CommonConst.STORE : "").durability(kit.getKitIcon().getDurability()).build());
		
		menu.setItem(29, new ItemBuilder().name("§fPreço: §710k").type(Material.EMERALD).build());
		menu.setItem(31, new ItemBuilder().name("§fDescrição: §7").lore("§7" + kit.getDescription()).type(Material.NAME_TAG).build());
		menu.setItem(33, new ItemBuilder().name("§fDescrição: §7" + kit.getDescription()).type(Material.NAME_TAG).build());

		menu.open(player);
	}

}
