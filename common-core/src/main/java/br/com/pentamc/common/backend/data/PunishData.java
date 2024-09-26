package br.com.pentamc.common.backend.data;

import java.util.Collection;
import java.util.UUID;

import br.com.pentamc.common.ban.constructor.Ban;
import br.com.pentamc.common.ban.constructor.Mute;
import br.com.pentamc.common.ban.constructor.Warn;

public interface PunishData {
	
	/*
	 * Ban
	 */
	
	Collection<Ban> loadBan(UUID uniqueId, int limit);
	
	boolean hasBan(UUID uniqueId);
	
	void addBan(Ban ban);
	
	void updateBan(Ban ban, String fieldName);
	
	int getTotalBan();
	
	/*
	 * Mute
	 */
	
	Collection<Mute> loadMute(UUID uniqueId, int limit);
	
	boolean hasMute(UUID uniqueId);
	
	void addMute(Mute mute);
	
	int getTotalMute();
	
	/*
	 * Warn
	 */
	
	Collection<Warn> loadWarn(UUID uniqueId, int limit);
	
	boolean hasWarn(UUID uniqueId);
	
	void addWarn(Warn warn);
	
	int getTotalWarn();
	
}
