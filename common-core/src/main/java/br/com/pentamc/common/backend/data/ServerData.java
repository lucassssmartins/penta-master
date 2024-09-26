package br.com.pentamc.common.backend.data;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import br.com.pentamc.common.backend.Query;
import br.com.pentamc.common.profile.Profile;
import com.google.gson.JsonElement;

import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.common.server.loadbalancer.server.MinigameState;

public interface ServerData {

	/*
	 * Get Server Info
	 */

	String getServerId(String ipAddress);

	ServerType getServerType(String ipAddress);

	int getTime(String serverId);

	MinigameState getState(String serverId);

	String getMap(String serverId);

	/*
	 * Server Network Info
	 */

	Map<String, Map<String, String>> loadServers();

	Set<UUID> getPlayers(String serverId);

	/*
	 * Network Server Manager
	 */

	void startServer(int maxPlayers);

	void updateStatus(MinigameState state, int time);

	void updateStatus(MinigameState state, String map, int time);

	void setJoinEnabled(boolean bol);

	void stopServer();
	
	void setTotalMembers(int totalMembers);
	
	/*
	 * Player Server Manager
	 */

	void joinPlayer(UUID uuid);

	void addWhitelist(Profile profile);

	void leavePlayer(UUID uuid);

	void removeWhitelist(Profile profile);

	/*
	 * Player Server Info
	 */

	long getPlayerCount(String serverId);

	long getPlayerCount(ServerType serverType);

	/*
	 * Connection
	 */
	
	Query<JsonElement> getQuery();

	void closeConnection();

}
