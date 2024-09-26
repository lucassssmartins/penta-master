package br.com.pentamc.bungee.event.server;

import br.com.pentamc.common.server.loadbalancer.server.MinigameState;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import lombok.Getter;

@Getter
public class ServerUpdateEvent extends ServerEvent {

	private String map;
	private int time;

	private MinigameState lastState;
	private MinigameState state;

	public ServerUpdateEvent(ProxiedServer proxiedServer, String map, int time, MinigameState lastState,
                             MinigameState state) {
		super(proxiedServer);

		this.map = map;
		this.time = time;
		this.lastState = lastState;
		this.state = state;
	}

}
