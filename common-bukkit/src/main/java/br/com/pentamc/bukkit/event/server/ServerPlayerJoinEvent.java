package br.com.pentamc.bukkit.event.server;

import java.util.UUID;

import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import lombok.Getter;

@Getter
public class ServerPlayerJoinEvent extends ServerEvent {
	
	private UUID uniqueId;

	public ServerPlayerJoinEvent(UUID uniqueId, String serverId, ServerType serverType, ProxiedServer proxiedServer) {
		super(serverId, serverType, proxiedServer);
		this.uniqueId = uniqueId;
	}

}
