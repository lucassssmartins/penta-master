package br.com.pentamc.bungee.event.server;

import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter
@AllArgsConstructor
public class ServerEvent extends Event {

	private ProxiedServer proxiedServer;

}
