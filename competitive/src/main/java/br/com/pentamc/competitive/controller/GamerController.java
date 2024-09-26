package br.com.pentamc.competitive.controller;

import java.util.Collection;
import java.util.UUID;

import br.com.pentamc.competitive.constructor.Gamer;
import org.bukkit.entity.Player;

import br.com.pentamc.common.controller.StoreController;

public class GamerController extends StoreController<UUID, Gamer> {
	
	public GamerController() {
		super();
		getStoreConfig().setReplace(false);
	}
	
	public Gamer getGamer(UUID key) {
		return super.getValue(key);
	}
	
	public Gamer getGamer(Player player) {
		return super.getValue(player.getUniqueId());
	}
	
	public void loadGamer(UUID key, Gamer value) {
		super.load(key, value);
	}
	
	public void unloadGamer(UUID key) {
		super.unload(key);
	}

	public Collection<Gamer> getGamers() {
		return getStoreMap().values();
	}

}
