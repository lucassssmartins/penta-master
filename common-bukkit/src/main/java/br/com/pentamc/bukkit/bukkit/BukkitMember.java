package br.com.pentamc.bukkit.bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.account.League;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.MemberModel;
import br.com.pentamc.common.account.configuration.LoginConfiguration;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.GameStatus;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.profile.Profile;
import br.com.pentamc.common.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.bukkit.event.account.PlayerChangeLeagueEvent;
import br.com.pentamc.bukkit.event.account.PlayerChangeTagEvent;
import br.com.pentamc.bukkit.event.account.PlayerTryChangeTagEvent;

@Getter
public class BukkitMember extends Member {

	@Setter
	private transient Player player;
	private transient List<Tag> tags;
	private transient Scoreboard scoreboard;
	@Setter
	private transient boolean buildEnabled;

	@Setter
	private transient boolean cacheOnQuit;
	@Setter
	private transient Profile lastTell;

	public BukkitMember(MemberModel memberModel) {
		super(memberModel);
	}

	public BukkitMember(String playerName, UUID uniqueId, LoginConfiguration.AccountType accountType) {
		super(playerName, uniqueId, accountType);
	}

	@Override
	public void setJoinData(String playerName, String hostString) {
		super.setJoinData(playerName, hostString);
		loadTags();
	}

	@Override
	public boolean hasPermission(String string) {
		if (super.hasPermission(string))
			return true;

		if (player == null)
			return false;

		return player.hasPermission(string.toLowerCase());
	}

	@Override
	public void sendMessage(String message) {
		if (player != null)
			player.sendMessage(message);
	}

	@Override
	public void sendMessage(BaseComponent message) {
		if (player != null)
			player.spigot().sendMessage(message);
	}

	@Override
	public void sendMessage(BaseComponent[] message) {
		if (player != null)
			player.spigot().sendMessage(message);
	}

	public void setScoreboard(Scoreboard scoreboard) {
		if (this.scoreboard == null || this.scoreboard != scoreboard) {
			this.scoreboard = scoreboard;
			this.scoreboard.createScoreboard(getPlayer());
		}
	}

	@Override
	public boolean setTag(Tag tag) {
		if (!BukkitMain.getInstance().isTagControl())
			return false;

		return setTag(tag, false);
	}

	public boolean setTag(Tag tag, boolean forcetag) {
		if (!tags.contains(tag) && !forcetag) {
			tag = getDefaultTag();
		}

		PlayerTryChangeTagEvent event = new PlayerTryChangeTagEvent(player, getTag(), tag, forcetag);
		Bukkit.getPluginManager().callEvent(event);

		tag = event.getNewTag();

		if (!event.isCancelled())
			if (!forcetag) {
				PlayerChangeTagEvent change = new PlayerChangeTagEvent(player, this, getTag(), tag, forcetag);
				Bukkit.getPluginManager().callEvent(change);
				tag = change.getNewTag();
				super.setTag(tag);
			}

		return !event.isCancelled();
	}

	@Override
	public boolean hasGroupPermission(Group groupToUse) {
		return super.hasGroupPermission(groupToUse);
	}

	@Override
	public void setXp(StatusType statusType, int xp) {
		if (statusType.getStatusClass() == GameStatus.class) return;

		super.setXp(statusType, xp);

		if (xp >= getLeague(statusType).getMaxXp()) {
			setLeague(statusType, getLeague(statusType).getNextLeague());
		} else if (getLeague(statusType) != League.values()[0]) {
			if (xp < getLeague(statusType).getPreviousLeague().getMaxXp()) {
				setLeague(statusType, getLeague(statusType).getPreviousLeague());
			}
		}
	}

	@Override
	public void setLeague(StatusType statusType, League liga) {
		PlayerChangeLeagueEvent event = new PlayerChangeLeagueEvent(getPlayer(), this, getLeague(statusType), liga);
		BukkitMain.getInstance().getServer().getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			super.setLeague(statusType, liga);
			setTag(getTag());
		}
	}

	public void loadTags() {
		tags = new ArrayList<>();
		for (Tag tag : Tag.values()) {
			if (super.hasPermission("tag." + tag.getName().toLowerCase())) {
				tags.add(tag);
				continue;
			}

			if (tag.getGroupToUse() == null || tag.getDefaultGroup() == null)
				continue;

			if (tag.isExclusive()) {
				if (tag.getGroupToUse().contains(getServerGroup()) || hasRank(tag.getGroupToUse())
						|| getServerGroup().ordinal() >= Group.ADMIN.ordinal()) {
					tags.add(tag);
				}
				continue;
			}

			if (getServerGroup().ordinal() >= tag.getDefaultGroup().ordinal())
				tags.add(tag);
			else {
				for (Group group : tag.getGroupToUse())
					if (getServerGroup() == group)
						tags.add(tag);
			}
		}

	}

	public Tag getDefaultTag() {
		return tags.get(0);
	}

	public List<Tag> getTags() {
		return tags;
	}

	public boolean hasTag(Tag tag) {
		if (tags.contains(tag))
			return true;

		for (Tag t : tags) {
			if (t.getName().equals(tag.getName()))
				return true;
		}

		return false;
	}

}