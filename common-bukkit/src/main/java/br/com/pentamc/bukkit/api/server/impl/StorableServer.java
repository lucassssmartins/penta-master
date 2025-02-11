package br.com.pentamc.bukkit.api.server.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.pentamc.common.profile.Profile;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;
import br.com.pentamc.bukkit.api.server.Server;
import br.com.pentamc.bukkit.api.server.chat.ChatState;

public class StorableServer implements Server {

	private List<Profile> whiteList;
	private Map<Profile, Long> blackMap;

	@Getter
	@Setter
	private ChatState chatState = ChatState.ENABLED;

	private boolean whitelist;
	@Getter
	@Setter
	private boolean restoreMode;

	public StorableServer() {
		whiteList = new ArrayList<>();
		blackMap = new HashMap<>();
	}

	public StorableServer(JavaPlugin javaPlugin) {
		whiteList = new ArrayList<>();
		blackMap = new HashMap<>();
	}

	@Override
	public boolean addWhitelist(Profile profile) {
		if (!whiteList.contains(profile)) {
			whiteList.add(profile);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeWhitelist(Profile profile) {
		if (whiteList.contains(profile)) {
			whiteList.remove(profile);
			return true;
		}
		return false;
	}

	@Override
	public boolean hasWhitelist(Profile profile) {
		return whiteList.contains(profile);
	}

	@Override
	public boolean isWhitelist() {
		return whitelist;
	}

	@Override
	public void setWhitelist(boolean whitelistState) {
		this.whitelist = whitelistState;
	}

	@Override
	public List<Profile> getWhiteList() {
		return Collections.unmodifiableList(whiteList);
	}

	@Override
	public boolean isBlackedlist(Profile profile) {
		if (blackMap.containsKey(profile))
			if (blackMap.get(profile) > System.currentTimeMillis())
				return true;
			else
				unblacklist(profile);

		return false;
	}

	@Override
	public long getBlacklistTime(Profile profile) {
		return isBlackedlist(profile) ? blackMap.get(profile) : -1;
	}

	@Override
	public void blacklist(Profile profile, long time) {
		if (!blackMap.containsKey(profile))
			blackMap.put(profile, time);
	}

	@Override
	public void unblacklist(Profile profile) {
		if (blackMap.containsKey(profile))
			blackMap.remove(profile);
	}

	@Override
	public Collection<Profile> getBlackList() {
		return Collections.unmodifiableSet(blackMap.keySet());
	}

}
