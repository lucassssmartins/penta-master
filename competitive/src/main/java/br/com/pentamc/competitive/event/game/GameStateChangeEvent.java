package br.com.pentamc.competitive.event.game;

import br.com.pentamc.competitive.event.GameEvent;
import br.com.pentamc.competitive.game.GameState;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameStateChangeEvent extends GameEvent {
	
	private GameState fromState;
	private GameState toState;

}
