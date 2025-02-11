package br.com.pentamc.common.account.status.types.challenge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.pentamc.common.CommonGeneral;
import lombok.Getter;
import br.com.pentamc.common.account.status.Status;
import br.com.pentamc.common.account.status.StatusType;

@Getter
public class ChallengeStatus implements Status {

	private UUID uniqueId;
	private StatusType statusType;

	private Map<ChallengeType, ChallengeInfo> challengeInfo;

	public ChallengeStatus(ChallengeModel gameModel) {
		this.uniqueId = gameModel.getUniqueId();
		this.statusType = gameModel.getStatusType();
		this.challengeInfo = gameModel.getChallengeInfo();
	}
	
	public ChallengeStatus(UUID uniqueId, StatusType statusType) {
		this.uniqueId = uniqueId;
		this.statusType = statusType;
		
		this.challengeInfo = new HashMap<>();
	}
	
	@Override
	public void setUniqueId(UUID uniqueId) {
		this.uniqueId = uniqueId;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "uniqueId");
	}

	public void addAttemp(ChallengeType challengeType) {
		this.challengeInfo.computeIfAbsent(challengeType, v -> new ChallengeInfo()).addAttemps();
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "challengeInfo");
	}

	public void addWin(ChallengeType challengeType) {
		this.challengeInfo.computeIfAbsent(challengeType, v -> new ChallengeInfo()).addWins();
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "challengeInfo");
	}

	public void setTime(ChallengeType challengeType, int time) {
		this.challengeInfo.computeIfAbsent(challengeType, v -> new ChallengeInfo()).setTime(time);
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "challengeInfo");
	}

	public int getAttemps(ChallengeType challengeType) {
		return challengeInfo.containsKey(challengeType) ? challengeInfo.get(challengeType).getAttemps() : 0;
	}

	public int getWins(ChallengeType challengeType) {
		return challengeInfo.containsKey(challengeType) ? challengeInfo.get(challengeType).getWins() : 0;
	}

	public int getTime(ChallengeType challengeType) {
		return challengeInfo.containsKey(challengeType) ? challengeInfo.get(challengeType).getTime() : 0;
	}

	public boolean hasChallengeType(ChallengeType challengeType) {
		return challengeInfo.containsKey(challengeType);
	}

}
