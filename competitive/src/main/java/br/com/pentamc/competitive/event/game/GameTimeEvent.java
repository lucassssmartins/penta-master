package br.com.pentamc.competitive.event.game;

import br.com.pentamc.competitive.event.GameEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameTimeEvent extends GameEvent {
	
	private int time;

}
