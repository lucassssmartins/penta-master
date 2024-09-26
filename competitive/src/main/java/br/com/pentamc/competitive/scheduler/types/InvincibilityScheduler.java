package br.com.pentamc.competitive.scheduler.types;

import java.util.Arrays;
import java.util.List;

import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.event.game.GameInvincibilityEndEvent;
import br.com.pentamc.competitive.game.Team;
import br.com.pentamc.competitive.utils.ServerConfig;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.game.GameState;
import br.com.pentamc.competitive.listener.register.invincibility.InvincibilityListener;
import br.com.pentamc.common.utils.string.StringUtils;

public class InvincibilityScheduler implements GameSchedule {
	
	private GameGeneral gameGeneral;
	private List<Listener> listenerList;

	public InvincibilityScheduler() {
		this.gameGeneral = GameGeneral.getInstance();
		this.listenerList = Arrays.asList(new InvincibilityListener());
		
		registerListener();
	}

	@Override
	public void pulse(int time, GameState gameState) {

		ServerConfig.getInstance().execute(gameState, time);
		
		if (time <= 5) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.NOTE_PLING, 1f, 1f);
			}
		}
		if ((time % 60 == 0 || (time < 60 && (time % 15 == 0 || time == 10 || time <= 5)))) {
//			for (Player p : Bukkit.getOnlinePlayers()) {
//				p.sendMessage("§eA invencibilidade acaba em §b" + StringUtils.formatTime(time) + "§f!");
//			}
			
			Bukkit.broadcastMessage("§eA invencibilidade acaba em " + StringUtils.formatTime(time) + "!");
		}
		
		if (time <= 0) {
			Bukkit.broadcastMessage("§cA invencibilidade acabou!");

			for (Player player : Bukkit.getOnlinePlayers()) {
				Gamer gamer = GameMain.getInstance().getGeneral().getGamerController().getGamer(player);

				if (gamer == null)
					continue;

				if (!gamer.isPlaying())
					continue;

				if (GameMain.getInstance().getTeamManager().getTeams().stream().anyMatch(teams -> teams.getParticipantsAsGamer().contains(gamer)))
					continue;

				GameMain.getInstance().getTeamManager().joinEmptyTeam(gamer);
			}

			Bukkit.getPluginManager().callEvent(new GameInvincibilityEndEvent());
			gameGeneral.setGameState(GameState.GAMETIME);
			
			unregisterListener();
			unload();
		}
		
	}
	
	@Override
	public void registerListener() {
		listenerList.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, GameMain.getInstance()));
	}

	@Override
	public void unregisterListener() {
		listenerList.forEach(listener -> HandlerList.unregisterAll(listener));
	}

}
