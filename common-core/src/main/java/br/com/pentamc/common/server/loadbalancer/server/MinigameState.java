package br.com.pentamc.common.server.loadbalancer.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum MinigameState {

	WAITING(true), PREGAME(true), STARTING(true), INVINCIBILITY(true), GAMETIME, WINNING, NONE;

	private boolean decrementTime;

	public boolean isPregame() {
		switch (this) {
		case STARTING:
		case WAITING:
		case PREGAME:
			return true;
		default:
			return false;
		}
	}

	public boolean isInvencibility() {
		if (this == INVINCIBILITY)
			return true;
		return false;
	}

	public boolean isGametime() {
		return this == GAMETIME;
	}

	public boolean isEnding() {
		return this == WINNING;
	}

	public boolean isState(MinigameState state) {
		return this == state;
	}

}
