package br.com.pentamc.login.captcha.types;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.login.LoginMain;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.bukkit.api.menu.click.ClickType;
import br.com.pentamc.bukkit.api.menu.click.MenuClickHandler;
import br.com.pentamc.login.captcha.Captcha;

public class MenuCaptcha extends Captcha {

	private static final String WRONG_HEAD = "http://textures.minecraft.net/texture/fd3cfc239006b257b8b20f85a7bf42026c4ada084c1448d04e0c406ce8a2ea31";
	private static final String RIGHT_HEAD = "http://textures.minecraft.net/texture/5fde3bfce2d8cb724de8556e5ec21b7f15f584684ab785214add164be7624b";

	public MenuCaptcha(Player player, Member member, CaptchaHandler captchaHandler) {
		super(player, member, captchaHandler);
	}

	@Override
	public void start() {
		new BukkitRunnable() {

			@Override
			public void run() {
				MenuInventory menuInventory = new MenuInventory("§7Clique no bloco vermelho", 3);
				menuInventory.setReopenInventory(true);

				ItemStack item = new ItemBuilder().name("§7Cabeça errada, escolha outra").type(Material.SKULL_ITEM)
						.durability(3).skinURL(WRONG_HEAD).build();

				MenuClickHandler wrongClick = new MenuClickHandler() {

					int wrongClicks = 0;

					@Override
					public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (wrongClicks++ > 3) {
							getCaptchaHandler().handle(false);
							menuInventory.setReopenInventory(false);
							p.closeInventory();
							p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 0.2f, 1.0f);
						}
						return false;
					}
				};

				for (int x = 0; x < menuInventory.getInventory().getSize(); x++) {
					menuInventory.setItem(x, item, wrongClick);
				}

				MenuClickHandler rightClick = new MenuClickHandler() {

					@Override
					public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						getCaptchaHandler().handle(true);
						menuInventory.setReopenInventory(false);
						p.closeInventory();
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
						return false;
					}

				};

				menuInventory.setItem(CommonConst.RANDOM.nextInt(menuInventory.getInventory().getSize()),
						new ItemBuilder().name("§cClique para escolher este").type(Material.SKULL_ITEM).durability(3)
								.skinURL(RIGHT_HEAD).build(),
						rightClick);

				menuInventory.open(getPlayer());
			}
		}.runTaskLater(LoginMain.getInstance(), 10l);
	}

}
