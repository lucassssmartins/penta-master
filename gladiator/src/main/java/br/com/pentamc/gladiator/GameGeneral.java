package br.com.pentamc.gladiator;

import lombok.Getter;
import br.com.pentamc.gladiator.controller.ChallengeController;

@Getter
public class GameGeneral {
	
	@Getter
	private static GameGeneral instance;
	
	private ChallengeController challengeController;
	
	public GameGeneral() {
		instance = this;
	}

	public void onLoad() {
	}

	public void onEnable() {
		challengeController = new ChallengeController();
	}

	public void onDisable() {
		
	}

}
