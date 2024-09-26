package br.com.pentamc.bukkit.listener.register;

import java.io.File;
import java.util.*;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.listener.Listener;
import br.com.pentamc.common.account.League;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.profile.Profile;
import br.com.pentamc.common.utils.DateUtils;
import net.minecraft.server.v1_8_R3.BlockObsidian;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import br.com.pentamc.bukkit.api.vanish.AdminMode;
import br.com.pentamc.bukkit.api.vanish.VanishAPI;
import br.com.pentamc.bukkit.event.account.PlayerChangeLeagueEvent;
import br.com.pentamc.bukkit.event.vanish.PlayerShowToPlayerEvent;

public class PlayerListener extends Listener {

	private static final double MAX_OBSIDIAN_DISTANCE = 0.74d;
	private static final double MAX_OBSIDIAN_VELOCITY_DISTANCE = 0.99d;

	private Map<Player, Block> playerBlockMap = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLogin(PlayerLoginEvent event) {
		if (event.getResult() != Result.ALLOWED)
			return;

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (player == null) {
			event.disallow(Result.KICK_OTHER, "§cSua conta não foi carregada!");
			return;
		}

		if (getServerConfig().isWhitelist()) {
			if (player.hasGroupPermission(Group.YOUTUBERPLUS)
					|| getServerConfig().hasWhitelist(new Profile(player.getPlayerName(), player.getUniqueId())))
				event.allow();
			else
				event.disallow(Result.KICK_OTHER, "§cO servidor está disponivel somente para a equipe!");
		} else
			event.allow();

		if (getServerConfig().isBlackedlist(new Profile(player.getPlayerName(), player.getUniqueId())))
			event.disallow(Result.KICK_OTHER,
					"§cVocê está bloqueado de entrar nesse servidor! Expire em " + DateUtils.getTime(getServerConfig()
							.getBlacklistTime(new Profile(player.getPlayerName(), player.getUniqueId()))));
	}

	private List<BlockFace> getDirection(Location location) {
		double rotation = location.getYaw() - 180;

		if (rotation < 0) {
			rotation += 360.0;
		}

		if (0 <= rotation && rotation < 22.5) return Arrays.asList(BlockFace.SOUTH);
		if (22.5 <= rotation && rotation < 67.5) return Arrays.asList(BlockFace.NORTH, BlockFace.EAST);
		if (67.5 <= rotation && rotation < 112.5) return Arrays.asList(BlockFace.EAST);
		if (112.5 <= rotation && rotation < 157.5) return Arrays.asList(BlockFace.SOUTH, BlockFace.EAST);
		if (157.5 <= rotation && rotation < 202.5) return Arrays.asList(BlockFace.SOUTH);
		if (202.5 <= rotation && rotation < 247.5) return Arrays.asList(BlockFace.SOUTH, BlockFace.WEST);
		if (247.5 <= rotation && rotation < 292.5) return Arrays.asList(BlockFace.WEST);
		if (292.5 <= rotation && rotation < 337.5) return Arrays.asList(BlockFace.NORTH, BlockFace.WEST);
		if (337.5 <= rotation && rotation <= 360) return Arrays.asList(BlockFace.NORTH);
		return new ArrayList<>();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoinMonitor(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		VanishAPI.getInstance().updateVanishToPlayer(player);

		Bukkit.getOnlinePlayers().forEach(online -> {
			if (online.getUniqueId().equals(player.getUniqueId()))
				return;

			PlayerShowToPlayerEvent eventCall = new PlayerShowToPlayerEvent(player, online);
			Bukkit.getPluginManager().callEvent(eventCall);

			if (eventCall.isCancelled()) {
				if (online.canSee(player))
					online.hidePlayer(player);
			} else if (!online.canSee(player))
				online.showPlayer(player);
		});

		player.awardAchievement(Achievement.OPEN_INVENTORY);
	}

	@EventHandler
	public void onPlayerShowToPlayer(PlayerShowToPlayerEvent event) {
		if (VanishAPI.getInstance().getHideAllPlayers().contains(event.getToPlayer().getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerChangeLeague(PlayerChangeLeagueEvent event) {
		if (event.getPlayer() != null && event.getNewLeague().ordinal() > event.getOldLeague().ordinal()) {

			if (event.getNewLeague() == League.values()[League.values().length - 1]) {
				Bukkit.broadcastMessage(" ");
				Bukkit.broadcastMessage("§a" + event.getBukkitMember().getPlayerName() + "§a subiu para o rank "
						+ League.values()[League.values().length - 1].getColor()
						+ League.values()[League.values().length - 1].getName() + "§f!");
				Bukkit.broadcastMessage(" ");

				Bukkit.getOnlinePlayers()
						.forEach(player -> player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 0.1f));
			}

			event.getPlayer().sendMessage("§aVocê subiu para o rank " + event.getNewLeague().getColor()
					+ event.getNewLeague().getSymbol() + " " + event.getNewLeague().getName());
		} else if (event.getNewLeague().ordinal() < event.getOldLeague().ordinal()) {
			event.getPlayer().sendMessage("§cVocê desceu para o rank " + event.getNewLeague().getColor()
					+ event.getNewLeague().getSymbol() + " " + event.getNewLeague().getName());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		AdminMode.getInstance().removeAdmin(event.getPlayer());
		VanishAPI.getInstance().removeVanish(event.getPlayer());
		CommonGeneral.getInstance().getStatusManager().unloadStatus(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerKick(PlayerKickEvent event) {
		if (getMain().isRemovePlayerDat())
			removePlayerFile(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (getMain().isRemovePlayerDat())
			removePlayerFile(event.getPlayer().getUniqueId());

		playerBlockMap.remove(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerAchievementAwardedEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void archievement(PlayerAchievementAwardedEvent event) {
		event.setCancelled(true);
	}

	private void removePlayerFile(UUID uuid) {
		World world = Bukkit.getWorlds().get(0);
		File folder = new File(world.getWorldFolder(), "playerdata");

		if (folder.exists() && folder.isDirectory()) {
			File file = new File(folder, uuid.toString() + ".dat");
			Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitMain.getInstance(), () -> {
				if (file.exists() && !file.delete()) {
					removePlayerFile(uuid);
				}
			}, 2L);
		}
	}
}
