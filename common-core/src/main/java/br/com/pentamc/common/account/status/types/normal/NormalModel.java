package br.com.pentamc.common.account.status.types.normal;

import java.util.UUID;

import br.com.pentamc.common.account.status.StatusType;
import lombok.Getter;

@Getter
public class NormalModel {

	private UUID uniqueId;
	private StatusType statusType;

	private int kills;
	private int deaths;

	private int killstreak;
	private int maxKillstreak;

	public NormalModel(NormalStatus normalStatus) {
		this.uniqueId = normalStatus.getUniqueId();
		this.statusType = normalStatus.getStatusType();

		this.kills = normalStatus.getKills();
		this.deaths = normalStatus.getDeaths();
		this.killstreak = normalStatus.getKillstreak();
		this.maxKillstreak = normalStatus.getMaxKillstreak();
	}
}
