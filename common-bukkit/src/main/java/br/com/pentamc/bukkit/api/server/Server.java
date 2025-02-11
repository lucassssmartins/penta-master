package br.com.pentamc.bukkit.api.server;

import java.util.Collection;
import java.util.List;

import br.com.pentamc.common.profile.Profile;
import br.com.pentamc.bukkit.api.server.chat.ChatState;

/**
 * 
 * Class to configure the actual server
 * 
 * @author yandv
 *
 */

public interface Server {
	
	/**
	 * 
	 * Add the profile from Whitelist
	 * 
	 * @return
	 */
	
	boolean addWhitelist(Profile profile);
	
	/**
	 * 
	 * Remove the profile from Whitelist
	 * 
	 * @return
	 */
	
	boolean removeWhitelist(Profile profile);
	
	boolean hasWhitelist(Profile profile);
	
	/**
	 * 
	 * Return if the whitelist is current enabled
	 * 
	 * @return
	 */
	
	boolean isWhitelist();
	
	/**
	 * 
	 * Change the state of whitelist
	 * 
	 * @param whiteliteState
	 * @return
	 */
	
	void setWhitelist(boolean whitelistState);
	
	/**
	 * 
	 * Return the actual profiles in WhiteList
	 * 
	 * @return
	 */
	
	List<Profile> getWhiteList();
	
	/**
	 * 
	 * Blacklist a profile
	 * 
	 * @param profile
	 * @param time
	 */
	
	boolean isBlackedlist(Profile profile);
	
	/**
	 * 
	 * Return the long of profile
	 * 
	 * @param profile
	 * @return
	 */
	
	long getBlacklistTime(Profile profile);
	
	/**
	 * 
	 * Blacklist a profile
	 * 
	 * @param profile
	 * @param time
	 */
	
	void blacklist(Profile profile, long time);
	
	/**
	 * 
	 * Blacklist a profile
	 * 
	 * @param profile
	 * @param time
	 */
	
	void unblacklist(Profile profile);
	
	/**
	 * Return immutable collection of Profile in BlackList
	 * 
	 * @return
	 */
	
	Collection<Profile> getBlackList();
	
	/**
	 * 
	 * Change the actual server chatState
	 * 
	 * @param chatState
	 */
	
	void setChatState(ChatState chatState);
	
	/**
	 * 
	 * Return the actual chatState
	 * 
	 * @return
	 */
	
	ChatState getChatState();
	
	/**
	 * 
	 * Return the actual restoreMode
	 * 
	 * @return
	 */
	
	boolean isRestoreMode();
	
	/**
	 * 
	 * Change the restoreMode state
	 * 
	 * @return
	 */
	
	void setRestoreMode(boolean restoreMode);
	
}
