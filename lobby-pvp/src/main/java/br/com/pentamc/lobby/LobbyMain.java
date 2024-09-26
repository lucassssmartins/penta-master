package br.com.pentamc.lobby;

import br.com.pentamc.lobby.listener.CharacterListener;
import br.com.pentamc.lobby.listener.PlayerListener;
import br.com.pentamc.lobby.listener.ScoreboardListener;
import br.com.pentamc.lobby.menu.server.ServerInventory;
import lombok.Getter;

public class LobbyMain extends LobbyPlatform {

	@Getter
	private static LobbyMain instance;

	@Override
	public void onLoad() {
		super.onLoad();
		instance = this;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		ServerInventory.LOBBY_HG = true;
		getServer().getPluginManager().registerEvents(new CharacterListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

}
