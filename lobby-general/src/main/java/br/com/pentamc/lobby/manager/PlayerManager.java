package br.com.pentamc.lobby.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import lombok.Getter;
import br.com.pentamc.lobby.gamer.Gamer;

@Getter
public class PlayerManager {
	
	private Map<Player, Gamer> gamerMap;
	
	public PlayerManager() {
		gamerMap = new HashMap<>();
	}
	
	public void removeGamer(Player player) {
		if (gamerMap.containsKey(player))
			gamerMap.remove(player);	
	}
	
	public Gamer getGamer(Player player) {
		return gamerMap.computeIfAbsent(player, v -> new Gamer(player));
	}
	
	public Collection<Gamer> getGamers() {
		return gamerMap.values();
	}

}

