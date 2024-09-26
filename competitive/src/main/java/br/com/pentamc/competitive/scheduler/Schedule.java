package br.com.pentamc.competitive.scheduler;

import br.com.pentamc.competitive.game.GameState;

public interface Schedule {
	
	void pulse(int time, GameState gameState);

}
