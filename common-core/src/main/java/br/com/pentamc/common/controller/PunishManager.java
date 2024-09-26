package br.com.pentamc.common.controller;

import java.util.UUID;

import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.ban.constructor.Ban;
import br.com.pentamc.common.ban.constructor.Mute;
import br.com.pentamc.common.ban.constructor.Warn;
import br.com.pentamc.common.ban.constructor.Ban.UnbanReason;

public interface PunishManager {
	
	/*
	 * Punish
	 */
	
	boolean ban(Member member, Ban ban);
	
	boolean mute(Member member, Mute mute);
	
	boolean warn(Member member, Warn warn);
	
	/*
	 * Remove Punish
	 */
	
	boolean unban(Member member, UUID uniqueId, String userName, UnbanReason unbanReason);
	
	boolean unmute(Member member, UUID uniqueId, String userName);
	
	/*
	 * Check
	 */
	
	boolean isIpBanned(String ipAddress);
	
	/*
	 * Punish Message
	 */
	
	String getBanMessage(Ban ban);
	
	String getMuteMessage(Mute mute);
	
}
