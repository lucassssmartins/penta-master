package br.com.pentamc.common.data.payload;

import java.util.UUID;

import br.com.pentamc.common.profile.Profile;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.common.server.loadbalancer.server.MinigameState;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DataServerMessage<T> {

	private final String source;
	private final ServerType serverType;
	private final Action action;
	private final T payload;

	@Getter
	@RequiredArgsConstructor
	public static class StartPayload {
		private final String serverAddress;
		private final ProxiedServer server;
	}

	@Getter
	@RequiredArgsConstructor
	public static class StopPayload {
		private final String serverId;
	}

	@Getter
	@RequiredArgsConstructor
	public static class UpdatePayload {
		private final int time;
		private final String map;
		private final MinigameState state;
	}

	@Getter
	@RequiredArgsConstructor
	public static class JoinEnablePayload {
		private final boolean enable;
	}

	@Getter
	@RequiredArgsConstructor
	public static class JoinPayload {
		private final UUID uniqueId;
	}

	@Getter
	@RequiredArgsConstructor
	public static class LeavePayload {
		private final UUID uniqueId;
	}

	@Getter
	@RequiredArgsConstructor
	public static class WhitelistAddPayload {
		private final Profile profile;
	}

	@Getter
	@RequiredArgsConstructor
	public static class WhitelistRemovePayload {
		private final Profile profile;
	}

	public enum Action {
		START, STOP, UPDATE, JOIN_ENABLE, JOIN, LEAVE, WHITELIST_ADD, WHITELIST_REMOVE
	}

}