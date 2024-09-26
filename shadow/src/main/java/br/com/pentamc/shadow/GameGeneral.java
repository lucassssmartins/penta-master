package br.com.pentamc.shadow;

import br.com.pentamc.shadow.controller.ChallengeController;
import lombok.Getter;

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
