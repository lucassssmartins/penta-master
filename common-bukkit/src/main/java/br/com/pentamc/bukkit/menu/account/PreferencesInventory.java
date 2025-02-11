package br.com.pentamc.bukkit.menu.account;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.bukkit.api.menu.click.ClickType;
import br.com.pentamc.bukkit.api.menu.click.MenuClickHandler;

public class PreferencesInventory {

	private long lastChange;

	public PreferencesInventory(Player player, Class<?> clazz) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		MenuInventory menuInventory = new MenuInventory("§7§nPreferências", 4);

		addItem(menuInventory, member, Config.TELL, 10, "Mensagens privadas", "Permitir receber mensagens privadas.");
		addItem(menuInventory, member, Config.CLANCHAT, 11, "Bate-papo do clan",
				"Permitir receber mensagens do bate-papo do clan.");

		if (clazz != null)
			menuInventory.setItem(31, new ItemBuilder().name("§aVoltar").type(Material.ARROW).build(),
					(p, inv, type, stack, slot) -> {
						new AccountInventory(player, member);

						return false;
					});

		menuInventory.open(player);
	}

	public enum Config {

		TELL, CLANCHAT;

		public boolean getState(Member member) {
			switch (this) {
			case TELL: {
				return member.getAccountConfiguration().isTellEnabled();
			}
			case CLANCHAT: {
				return member.getAccountConfiguration().isClanChatEnabled();
			}
			default:
				return false;
			}
		}

		public void changeState(Member member, boolean newState) {
			switch (this) {
			case TELL: {
				member.getAccountConfiguration().setTellEnabled(newState);
				break;
			}
			case CLANCHAT: {
				member.getAccountConfiguration().setClanChatEnabled(newState);
				break;
			}
			default:
				return;
			}
		}

	}

	private void addItem(MenuInventory menuInventory, Member member, Config config, int slot, String preferenceName,
			String lore) {
		boolean state = config.getState(member);

		MenuClickHandler menuClickHandler = (p, inv, type, stack, s) -> {
			if (System.currentTimeMillis() > lastChange + 500) {
				config.changeState(member, !state);
				addItem(menuInventory, member, config, slot, preferenceName, lore);
//					menuInventory.updateSlot(p, slot);
//					menuInventory.updateSlot(p, slot + 9);
				lastChange = System.currentTimeMillis();
			} else
				p.sendMessage("§cVocê precisa esperar para executar essa ação novamente!");

			return false;
		};

		menuInventory.setItem(slot, new ItemBuilder().name((state ? "§a" : "§c") + preferenceName).lore("§7" + lore)
				.type(Material.PAPER).build(), menuClickHandler);
		menuInventory.setItem(slot + 9,
				new ItemBuilder().name((state ? "§a" : "§c") + preferenceName)
						.lore("§7Clique para " + (state ? "desativar" : "ativar") + ".").type(Material.INK_SACK)
						.durability(state ? 10 : 8).build(),
				menuClickHandler);
	}

}
