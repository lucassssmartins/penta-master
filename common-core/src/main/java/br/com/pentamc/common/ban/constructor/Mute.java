package br.com.pentamc.common.ban.constructor;

import java.util.UUID;

import br.com.pentamc.common.ban.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mute {

	private Category category;
	private UUID uniqueId;
	private int id = 0;

	private String mutedBy;
	private UUID mutedByUuid;
	private String reason;

	private long muteTime;
	private long muteExpire;
	private long muteDuration;

	private boolean unmuted;
	private String unmutedBy;
	private UUID unmutedByUuid;

	public Mute(Category category,UUID uniqueId, String mutedBy, UUID mutedByUuid, String reason, long muteExpire) {
		this.category = category;
		this.uniqueId = uniqueId;
		this.mutedBy = mutedBy;
		this.mutedByUuid = mutedByUuid;
		this.reason = reason;

		this.muteTime = System.currentTimeMillis();
		this.muteExpire = muteExpire;
		this.muteDuration = muteExpire - System.currentTimeMillis();
	}

	public boolean hasExpired() {
		return !isPermanent() && muteExpire < System.currentTimeMillis();
	}

	public boolean isPermanent() {
		return muteExpire == -1l;
	}

	public void unmute(UUID uniqueId, String userName) {
		unmutedBy = userName;
		unmutedByUuid = uniqueId;
		unmuted = true;
	}

}
