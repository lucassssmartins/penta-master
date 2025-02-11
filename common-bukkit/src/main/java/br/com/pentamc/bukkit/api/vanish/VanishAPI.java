package br.com.pentamc.bukkit.api.vanish;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.event.vanish.PlayerHideToPlayerEvent;
import br.com.pentamc.bukkit.event.vanish.PlayerShowToPlayerEvent;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.permission.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VanishAPI {
	
	private final static VanishAPI instance = new VanishAPI();

	private Map<UUID, Group> vanishedToGroup;
	private Set<UUID> hideAllPlayers;

	public VanishAPI() {
		vanishedToGroup = new HashMap<>();
		hideAllPlayers = new HashSet<>();
	}

	public void setPlayerVanishToGroup(Player player, Group group) {
		if (group == null)
			vanishedToGroup.remove(player.getUniqueId());
		else
			vanishedToGroup.put(player.getUniqueId(), group);

		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online.getUniqueId().equals(player.getUniqueId()))
				continue;

			Member onlineP = CommonGeneral.getInstance().getMemberManager().getMember(online.getUniqueId());

			if (onlineP == null)
				continue;

			if (group != null && onlineP.getServerGroup().ordinal() <= group.ordinal()) {
				PlayerHideToPlayerEvent event = new PlayerHideToPlayerEvent(player, online);

				Bukkit.getPluginManager().callEvent(event);

				if (event.isCancelled()) {
					if (!online.canSee(player))
						online.showPlayer(player);
				} else if (online.canSee(player))
					online.hidePlayer(player);

				continue;
			}

			PlayerShowToPlayerEvent event = new PlayerShowToPlayerEvent(player, online);
			Bukkit.getPluginManager().callEvent(event);

			if (event.isCancelled()) {
				if (online.canSee(player))
					online.hidePlayer(player);
			} else if (!online.canSee(player))
				online.showPlayer(player);
		}
	}

	public void updateVanishToPlayer(Player player) {
		Member bP = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online.getUniqueId().equals(player.getUniqueId()))
				continue;

			Group group = vanishedToGroup.get(online.getUniqueId());

			if (group != null) {
				if (bP.getServerGroup().ordinal() <= group.ordinal()) {
					PlayerHideToPlayerEvent event = new PlayerHideToPlayerEvent(online, player);
					Bukkit.getPluginManager().callEvent(event);

					if (event.isCancelled()) {
						if (!player.canSee(online))
							player.showPlayer(online);
					} else if (player.canSee(online))
						player.hidePlayer(online);

					continue;
				}
			}

			PlayerShowToPlayerEvent event = new PlayerShowToPlayerEvent(online, player);

			Bukkit.getPluginManager().callEvent(event);

			if (event.isCancelled()) {
				if (player.canSee(online))
					player.hidePlayer(online);
			} else if (!player.canSee(online))
				player.showPlayer(online);
		}
	}

	public Group hidePlayer(Player player) {
		Member bP = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		Group group = bP.getServerGroup().ordinal() - 1 >= 0 ? Group.values()[bP.getServerGroup().ordinal() - 1]
				: Group.MEMBRO;
		setPlayerVanishToGroup(player, group);
		return group;
	}

	public void hideAllPlayers(Player p) {
		this.hideAllPlayers.add(p.getUniqueId());

		for (Player hide : Bukkit.getOnlinePlayers()) {
			if (hide.getUniqueId() != p.getUniqueId()) {
				p.hidePlayer(hide);
			}
		}
	}

	public void updateHide(Player p) {
		Iterator<UUID> iterator = this.hideAllPlayers.iterator();

		while (iterator.hasNext()) {
			Player hide = Bukkit.getPlayer(iterator.next());

			if (hide == null)
				iterator.remove();
			else
				hide.hidePlayer(p);
		}
	}

	public void showPlayer(Player player) {
		setPlayerVanishToGroup(player, null);
	}

	public void updateVanish(Player player) {
		setPlayerVanishToGroup(player, getVanishedToGroup(player.getUniqueId()));
	}

	public Group getVanishedToGroup(UUID uuid) {
		return vanishedToGroup.get(uuid);
	}

	public void removeVanish(Player p) {
		vanishedToGroup.remove(p.getUniqueId());
	}

	public Set<UUID> getHideAllPlayers() {
		return hideAllPlayers;
	}

	public static VanishAPI getInstance() {
		return instance;
	}
}
