package br.com.pentamc.competitive.scheduler;

import br.com.pentamc.competitive.GameConst;
import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.event.game.GameInvincibilityEndEvent;
import br.com.pentamc.competitive.event.game.GameStartEvent;
import br.com.pentamc.competitive.event.game.GameStateChangeEvent;
import br.com.pentamc.competitive.event.player.PlayerItemReceiveEvent;
import br.com.pentamc.competitive.scheduler.types.GameScheduler;
import br.com.pentamc.competitive.scheduler.types.InvincibilityScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.kit.KitType;
import br.com.pentamc.competitive.listener.register.BlockListener;
import br.com.pentamc.competitive.listener.register.DeathListener;
import br.com.pentamc.competitive.listener.register.SpectatorListener;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.api.vanish.AdminMode;
import br.com.pentamc.bukkit.event.update.UpdateEvent;
import br.com.pentamc.bukkit.event.update.UpdateEvent.UpdateType;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.permission.Group;

public class SchedulerListener implements Listener {

	private GameGeneral gameGeneral;

	public SchedulerListener(GameGeneral gameGeneral) {
		this.gameGeneral = gameGeneral;
	}

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		if (event.getBlock().getBiome().name().contains("JUNGLE"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;

		gameGeneral.pulse();
	}

	@EventHandler
	public void onGameStateChange(GameStateChangeEvent event) {
		CommonGeneral.getInstance().getLogger().info(event.getFromState().name() + " > " + event.getToState().name());
	}

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		gameGeneral.getSchedulerController().addSchedule(new InvincibilityScheduler());

		GameGeneral.getInstance().getAbilityController().registerAbilityListeners();

		Bukkit.getWorlds().forEach(world -> {
			world.setGameRuleValue("doDaylightCycle", "true");
			world.setTime(0l);
		});

		World world = Bukkit.getWorlds().stream().findFirst().orElse(null);

		world.playSound(new Location(world, 0, 120, 0), Sound.AMBIENCE_THUNDER, 1f, 1f);
		world.playSound(new Location(world, 0, 120, 0), Sound.ENDERDRAGON_GROWL, 1f, 1f);

		GameGeneral.getInstance().getGamerController().getGamers().stream().filter(Gamer::isPlaying)
				.forEach(gamer -> {
					Player player = gamer.getPlayer();

					player.closeInventory();

					player.getInventory().clear();
					player.getInventory().setArmorContents(new ItemStack[4]);

					Bukkit.getPluginManager().callEvent(new PlayerItemReceiveEvent(player));

					if (Member.hasGroupPermission(player.getUniqueId(), Group.VIP) || gamer.isWinner()) {
						if (!gamer.hasKit(KitType.PRIMARY))
							gamer.setNoKit(KitType.PRIMARY);

						if (!gamer.hasKit(KitType.SECONDARY))
							gamer.setNoKit(KitType.SECONDARY);
					}

					if (!AdminMode.getInstance().isAdmin(player)) {
						player.setAllowFlight(false);
						player.setGameMode(org.bukkit.GameMode.SURVIVAL);
					}

					gamer.setGame(GameMain.GAME);
					gamer.getStatus().addMatch();
					player.updateInventory();
				});

		GameMain.getInstance().registerListener(new SpectatorListener());
		GameMain.getInstance().registerListener(new DeathListener());
		GameMain.getInstance().registerListener(new BlockListener());

		GameMain.GAME.setStartPlayers(GameGeneral.getInstance().getPlayersInGame());
		GameMain.GAME.setStartTime(System.currentTimeMillis());

		Bukkit.broadcastMessage("§cO torneio iniciou!");

		for (Player player : Bukkit.getOnlinePlayers()) {
			Gamer gamer = gameGeneral.getGamerController().getGamer(player.getUniqueId());

			if (gamer == null || !gamer.isPlaying())
				continue;

			ItemStack firstSword = null;

			for (ItemStack item : player.getInventory().getContents()) {
				if (item != null && item.getType().toString().contains("SWORD")) {
					firstSword = item;
					break;
				}
			}

			if (firstSword == null)
				continue;

			ItemMeta itemMeta = firstSword.getItemMeta();

			itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
			firstSword.setItemMeta(itemMeta);

			player.getInventory().setItem(player.getInventory().first(firstSword), firstSword);
		}
	}

	@EventHandler
	public void onGameStart(GameInvincibilityEndEvent event) {
		gameGeneral.getSchedulerController().addSchedule(new GameScheduler());
	}
}
