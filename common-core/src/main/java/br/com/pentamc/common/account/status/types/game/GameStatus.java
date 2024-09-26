package br.com.pentamc.common.account.status.types.game;

import java.util.UUID;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.League;
import br.com.pentamc.common.account.status.Status;
import br.com.pentamc.common.account.status.StatusType;
import lombok.Getter;

@Getter
public class GameStatus implements Status {

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

	public GameStatus(GameModel gameModel) {
		this.uniqueId = gameModel.getUniqueId();
		this.statusType = gameModel.getStatusType();

		this.kills = gameModel.getKills();
		this.deaths = gameModel.getDeaths();
		this.killstreak = gameModel.getKillstreak();

		this.games = gameModel.getGames();
		this.losses = gameModel.getLosses();
		this.wins = gameModel.getWins();

		this.winStreak = gameModel.getWinStreak();
		this.maxStreak = gameModel.getMaxStreak();

		this.xp = gameModel.getXp();
		this.league = gameModel.getLeague();
	}

	public GameStatus(UUID uniqueId, StatusType statusType) {
		this.uniqueId = uniqueId;
		this.statusType = statusType;
	}
	
	@Override
	public void setUniqueId(UUID uniqueId) {
		this.uniqueId = uniqueId;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "uniqueId");
	}

	public void setKills(int kills) {
		this.kills = kills;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "kills");
	}

	public void addKill() {
		this.kills++;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "kills");
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "deaths");
	}

	public void addDeath() {
		this.deaths++;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "deaths");
	}

	public void setKillstreak(int killstreak) {
		this.killstreak = killstreak;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "killstreak");
	}

	public void addKillstreak() {
		this.killstreak++;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "killstreak");
	}

	public void resetKillstreak() {
		this.killstreak = 0;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "killstreak");
	}

	public void setMatch(int games) {
		this.games = games;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "games");
	}

	public void addMatch() {
		games++;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "games");
	}

	public int getWins() {
		return wins;
	}

	public void addWin() {
		wins++;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "wins");
	}

	public void setWins(int win) {
		this.wins = win;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "wins");
	}

	public void setXp(int xp) {
		this.xp = xp;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "xp");
	}

	public void addXp(int addon) {
		setXp(getXp() + addon);
	}

	public void removeXp(int remove) {
		setXp(getXp() - remove);
	}

	public void setLeague(League league) {
		this.league = league;
	}

	public int getMaxKillstreak() {
		return maxStreak;
	}

	public int getMatches() {
		return games;
	}

}
