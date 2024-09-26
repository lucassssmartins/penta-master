package br.com.pentamc.common.account.status.types.challenge;

import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import br.com.pentamc.common.account.status.StatusType;

@Getter
public class ChallengeModel {

	private UUID uniqueId;
	private StatusType statusType;

	private Map<ChallengeType, ChallengeInfo> challengeInfo;

	public ChallengeModel(ChallengeStatus challengeStatus) {
		this.uniqueId = challengeStatus.getUniqueId();
		this.statusType = challengeStatus.getStatusType();
		this.challengeInfo = challengeStatus.getChallengeInfo();
	}

}
