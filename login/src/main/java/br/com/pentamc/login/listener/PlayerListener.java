package br.com.pentamc.login.listener;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.configuration.LoginConfiguration;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.tag.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.api.scoreboard.Score;
import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import br.com.pentamc.bukkit.api.tablist.Tablist;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.login.LoginMain;

public class PlayerListener implements Listener {

	private static final Scoreboard SCOREBOARD = new SimpleScoreboard("§b§lLOGIN");
	private static final Tablist TABLIST = new Tablist("\n§b§l" + CommonConst.SERVER_NAME.toUpperCase()
			+ "\n§f\n§7Nome: §f%name% §7- §7Grupo: %group%\n§f                                                 §f",
			"\n§a" + CommonConst.SITE + "\n§a" + CommonConst.DISCORD.replace("http://", "") + "\n§f ") {

		@Override
		public String[] replace(Player player, String header, String footer) {
			Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

			header = header.replace("%group%", member.getGroup() == Group.MEMBRO ? "§7§lMEMBRO"
					: Tag.valueOf(member.getGroup().name()).getPrefix());
			header = header.replace("%name%", member.getPlayerName());

			footer = footer.replace("%name%", member.getPlayerName());
			footer = footer.replace(".br/", "");

			return new String[] { header, footer };
		}

	};

	{
		SCOREBOARD.blankLine(8);
		SCOREBOARD.setScore(7, new Score("§7Logue-se usando o", "2"));
		SCOREBOARD.setScore(6, new Score("§7/login <senha>", "1"));
		SCOREBOARD.blankLine(5);
		SCOREBOARD.setScore(4, new Score("§7Registre-se usando", "3"));
		SCOREBOARD.setScore(3, new Score("§7/register <senha>", "4"));
		SCOREBOARD.blankLine(2);
		SCOREBOARD.setScore(1, new Score("§a§o" + CommonConst.SITE, "site"));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		player.sendMessage("§f");
		player.sendMessage("§f");
		event.setJoinMessage(null);

		player.teleport(BukkitMain.getInstance().getLocationFromConfig("spawn"));
		SCOREBOARD.createScoreboard(player);
		TABLIST.addViewer(player);

		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		new BukkitRunnable() {

			@Override
			public void run() {
				if (member.getLoginConfiguration().getAccountType() == LoginConfiguration.AccountType.ORIGINAL)
					member.setTag(LoginMain.ORIGINAL_TAG, true);
				else
					member.setTag(LoginMain.LOGGING_TAG, true);
			}
		}.runTaskLater(LoginMain.getInstance(), 20l);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		TABLIST.removeViewer(event.getPlayer());
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.VOID)
			event.getEntity().teleport(BukkitMain.getInstance().getLocationFromConfig("spawn"));

		event.setCancelled(true);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		event.setCancelled(true);
	}

}
