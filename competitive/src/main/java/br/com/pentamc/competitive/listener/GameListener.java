package br.com.pentamc.competitive.listener;

import org.bukkit.event.Listener;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import lombok.Getter;

/**
 * 
 * Modo recuperação, o tempo para
 * 
 * @author Allan
 *
 */

@Getter
public class GameListener implements Listener {
	
	private GameMain gameMain;
	private GameGeneral gameGeneral;
	
	public GameListener() {
		this.gameMain = GameMain.getInstance();
		this.gameGeneral = GameGeneral.getInstance();
	}
	
	public boolean isPregame() {
		return gameGeneral.getGameState().isPregame();
	}
	
}
