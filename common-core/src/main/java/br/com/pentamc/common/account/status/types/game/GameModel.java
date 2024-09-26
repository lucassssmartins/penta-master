package br.com.pentamc.common.account.status.types.game;

import java.util.UUID;

import br.com.pentamc.common.account.League;
import br.com.pentamc.common.account.status.StatusType;
import lombok.Getter;

@Getter
public class GameModel {
	
	private UUID uniqueId;
	private StatusType statusType;
	
	private int kills;
	private int deaths;
	private int killstreak;
	
	private int games;
	private int losses;
	private int wins;
	
	private int winStreak;
	private int maxStreak;

	private int xp;
	private League league = League.VOID;
	
	public GameModel(GameStatus gameStatus) {
		this.uniqueId = gameStatus.getUniqueId();
		this.statusType = gameStatus.getStatusType();
		
		this.kills = gameStatus.getKills();
		this.deaths = gameStatus.getDeaths();
		this.killstreak = gameStatus.getKillstreak();
		
		this.games = gameStatus.getGames();
		this.losses = gameStatus.getLosses();
		this.wins = gameStatus.getWins();
		
		this.winStreak = gameStatus.getWinStreak();
		this.maxStreak = gameStatus.getMaxStreak();

		this.xp = gameStatus.getXp();
		this.league = gameStatus.getLeague();
	}

}
