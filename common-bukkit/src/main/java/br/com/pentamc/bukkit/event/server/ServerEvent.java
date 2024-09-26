package br.com.pentamc.bukkit.event.server;

import br.com.pentamc.bukkit.event.NormalEvent;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerEvent extends NormalEvent {
	
	private String serverId;
	private ServerType serverType;
	
	private ProxiedServer proxiedServer;

}
