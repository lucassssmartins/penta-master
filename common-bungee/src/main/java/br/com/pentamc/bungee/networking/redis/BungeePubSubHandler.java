package br.com.pentamc.bungee.networking.redis;

import java.lang.reflect.Field;
import java.util.UUID;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bungee.BungeeMain;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.clan.Clan;
import br.com.pentamc.common.data.payload.DataServerMessage;
import br.com.pentamc.common.report.Report;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.common.server.loadbalancer.server.MinigameServer;
import br.com.pentamc.common.server.loadbalancer.server.MinigameState;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import br.com.pentamc.common.utils.reflection.Reflection;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.JedisPubSub;
import br.com.pentamc.bungee.bungee.BungeeMember;
import br.com.pentamc.bungee.event.player.PlayerUpdateFieldEvent;
import br.com.pentamc.bungee.event.server.ServerUpdateEvent;

public class BungeePubSubHandler extends JedisPubSub {

	@Override
	public void onMessage(String channel, String message) {
		if (!(message.startsWith("{") && message.endsWith("}")))
			return;

		JsonObject jsonObject = (JsonObject) new JsonParser().parse(message);

		if (!jsonObject.has("source")
				|| jsonObject.get("source").getAsString().equalsIgnoreCase(CommonGeneral.getInstance().getServerId()))
			return;

		switch (channel) {
		case "clan-field": {
			UUID uuid = UUID.fromString(jsonObject.get("uniqueId").getAsString());
			Clan report = CommonGeneral.getInstance().getClanManager().getClan(uuid);

			if (report == null)
				break;

			try {
				Field field = Reflection.getField(Clan.class, jsonObject.get("field").getAsString());
				field.setAccessible(true);
				Object object = CommonConst.GSON.fromJson(jsonObject.get("value"), field.getGenericType());
				field.set(report, object);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case "report-field": {
			UUID uuid = UUID.fromString(jsonObject.get("uniqueId").getAsString());
			Report report = CommonGeneral.getInstance().getReportManager().getReport(uuid);

			if (report == null)
				break;

			try {
				Field field = Reflection.getField(Report.class, jsonObject.get("field").getAsString());
				field.setAccessible(true);
				Object object = CommonConst.GSON.fromJson(jsonObject.get("value"), field.getGenericType());
				field.set(report, object);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case "report-action": {
			UUID playerUuid = UUID.fromString(jsonObject.get("uniqueId").getAsString());
			String action = jsonObject.get("action").getAsString();

			switch (action) {
			case "remove": {
				if (CommonGeneral.getInstance().getReportManager().getReport(playerUuid) != null)
					CommonGeneral.getInstance().getReportManager().unloadReport(playerUuid);
				break;
			}
			default:
				break;
			}
			break;
		}
		case "account-field": {
			UUID uuid = UUID.fromString(jsonObject.getAsJsonPrimitive("uniqueId").getAsString());
			ProxiedPlayer p = BungeeMain.getPlugin().getProxy().getPlayer(uuid);

			if (p == null)
				return;

			Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

			if (player == null)
				return;

			try {
				Field f = Reflection.getField(Member.class, jsonObject.get("field").getAsString());
				Object oldObject = f.get(player);
				Object object = CommonConst.GSON.fromJson(jsonObject.get("value"), f.getGenericType());
				f.setAccessible(true);
				f.set(player, object);

				ProxyServer.getInstance().getPluginManager()
						.callEvent(new PlayerUpdateFieldEvent((BungeeMember) player, f.getName(), oldObject, object));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			break;
		}
		case "server-info": {
			String source = jsonObject.get("source").getAsString();

			ServerType sourceType = ServerType.valueOf(jsonObject.get("serverType").getAsString());
			DataServerMessage.Action action = DataServerMessage.Action.valueOf(jsonObject.get("action").getAsString());

			if (sourceType == ServerType.NETWORK)
				break;

			switch (action) {
			case JOIN: {
				DataServerMessage<DataServerMessage.JoinPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.JoinPayload>>() {
						}.getType());
				ProxiedServer server = BungeeMain.getPlugin().getServerManager().getServer(source);

				if (server == null)
					return;

				server.joinPlayer(payload.getPayload().getUniqueId());
				break;
			}
			case LEAVE: {
				DataServerMessage<DataServerMessage.LeavePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.LeavePayload>>() {
						}.getType());
				ProxiedServer server = BungeeMain.getPlugin().getServerManager().getServer(source);

				if (server == null)
					return;

				server.leavePlayer(payload.getPayload().getUniqueId());
				break;
			}
			case JOIN_ENABLE: {
				DataServerMessage<DataServerMessage.JoinEnablePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.JoinEnablePayload>>() {
						}.getType());
				ProxiedServer server = BungeeMain.getPlugin().getServerManager().getServer(source);

				if (server == null)
					return;

				server.setJoinEnabled(payload.getPayload().isEnable());
				break;
			}
			case START: {
				DataServerMessage<DataServerMessage.StartPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.StartPayload>>() {
						}.getType());
				BungeeMain.getPlugin().getServerManager().addActiveServer(payload.getPayload().getServerAddress(),
						payload.getPayload().getServer().getServerId(), sourceType,
						payload.getPayload().getServer().getMaxPlayers());
				break;
			}
			case STOP: {
				DataServerMessage<DataServerMessage.StopPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.StopPayload>>() {
						}.getType());

				if (sourceType == ServerType.NETWORK) {
					break;
				}

				BungeeMain.getPlugin().getServerManager().removeActiveServer(payload.getPayload().getServerId());
				break;
			}
			case UPDATE: {
				DataServerMessage<DataServerMessage.UpdatePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.UpdatePayload>>() {
						}.getType());
				ProxiedServer server = BungeeMain.getPlugin().getServerManager().getServer(source);

				if (server == null)
					return;

				if (server instanceof MinigameServer) {
					MinigameServer minigame = ((MinigameServer) server);
					
					MinigameState lastState = minigame.getState();
					
					minigame.setState(payload.getPayload().getState());
					minigame.setTime(payload.getPayload().getTime());
					minigame.setMap(payload.getPayload().getMap());

					ProxyServer.getInstance().getPluginManager()
							.callEvent(new ServerUpdateEvent(minigame, payload.getPayload().getMap(),
									payload.getPayload().getTime(), lastState, payload.getPayload().getState()));
				}
				break;
			}
			case WHITELIST_ADD: {
				DataServerMessage<DataServerMessage.WhitelistAddPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.WhitelistAddPayload>>() {
						}.getType());

				ProxiedServer server = BungeeMain.getPlugin().getServerManager().getServer(source);

				if (server == null)
					return;

				server.addWhitelist(payload.getPayload().getProfile());
				break;
			}
			case WHITELIST_REMOVE: {
				DataServerMessage<DataServerMessage.WhitelistRemovePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.WhitelistRemovePayload>>() {
						}.getType());

				ProxiedServer server = BungeeMain.getPlugin().getServerManager().getServer(source);

				if (server == null)
					return;

				server.removeWhitelist(payload.getPayload().getProfile());
				break;
			}
			default:
				break;
			}
			break;
		}
		}
	}

}
