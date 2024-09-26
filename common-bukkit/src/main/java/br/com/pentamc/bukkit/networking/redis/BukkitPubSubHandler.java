package br.com.pentamc.bukkit.networking.redis;

import java.lang.reflect.Field;
import java.util.UUID;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.clan.Clan;
import br.com.pentamc.common.data.payload.DataServerMessage;
import br.com.pentamc.common.report.Report;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.common.server.loadbalancer.server.MinigameServer;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import br.com.pentamc.common.utils.reflection.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import redis.clients.jedis.JedisPubSub;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.event.account.PlayerUpdateFieldEvent;
import br.com.pentamc.bukkit.event.account.PlayerUpdatedFieldEvent;
import br.com.pentamc.bukkit.event.report.ReportReceiveEvent;
import br.com.pentamc.bukkit.event.server.PlayerChangeEvent;
import br.com.pentamc.bukkit.event.server.ServerPlayerJoinEvent;
import br.com.pentamc.bukkit.event.server.ServerPlayerLeaveEvent;

public class BukkitPubSubHandler extends JedisPubSub {

	@Override
	public void onMessage(String channel, String message) {
		JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();

		if (channel.equals("server-members")) {
			BukkitMain.getInstance().getServerManager().setTotalMembers(jsonObject.get("totalMembers").getAsInt());
			Bukkit.getPluginManager().callEvent(new PlayerChangeEvent(jsonObject.get("totalMembers").getAsInt()));
			return;
		}

		switch (channel) {
		case "competitive-channel": {
			System.out.println("NO BUKKIT FOI :(");
			break;
		}

		case "server-info": {
			if (!BukkitMain.getInstance().isServerLog())
				return;

			ServerType sourceType = ServerType.valueOf(jsonObject.get("serverType").getAsString());

			if (sourceType == ServerType.NETWORK)
				return;

			String source = jsonObject.get("source").getAsString();
			DataServerMessage.Action action = DataServerMessage.Action.valueOf(jsonObject.get("action").getAsString());

			switch (action) {
			case JOIN: {
				DataServerMessage<DataServerMessage.JoinPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.JoinPayload>>() {
						}.getType());
				ProxiedServer server = BukkitMain.getInstance().getServerManager().getServer(source);

				if (server == null)
					return;

				server.joinPlayer(payload.getPayload().getUniqueId());
				Bukkit.getPluginManager().callEvent(new ServerPlayerJoinEvent(payload.getPayload().getUniqueId(),
						server.getServerId(), server.getServerType(), server));
				break;
			}
			case LEAVE: {
				DataServerMessage<DataServerMessage.LeavePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.LeavePayload>>() {
						}.getType());

				ProxiedServer server = BukkitMain.getInstance().getServerManager().getServer(source);

				if (server == null)
					return;

				server.leavePlayer(payload.getPayload().getUniqueId());
				Bukkit.getPluginManager().callEvent(new ServerPlayerLeaveEvent(payload.getPayload().getUniqueId(),
						server.getServerId(), server.getServerType(), server));
				break;
			}
			case JOIN_ENABLE: {
				DataServerMessage<DataServerMessage.JoinEnablePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.JoinEnablePayload>>() {
						}.getType());

				ProxiedServer server = BukkitMain.getInstance().getServerManager().getServer(source);

				if (server == null)
					return;

				server.setJoinEnabled(payload.getPayload().isEnable());
				break;
			}
			case START: {
				DataServerMessage<DataServerMessage.StartPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.StartPayload>>() {
						}.getType());
				BukkitMain.getInstance().getServerManager().addActiveServer(payload.getPayload().getServerAddress(),
						payload.getPayload().getServer().getServerId(), sourceType,
						payload.getPayload().getServer().getMaxPlayers());
				break;
			}
			case STOP: {
				DataServerMessage<DataServerMessage.StopPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.StopPayload>>() {
						}.getType());

				BukkitMain.getInstance().getServerManager().removeActiveServer(payload.getPayload().getServerId());
				break;
			}
			case UPDATE: {
				DataServerMessage<DataServerMessage.UpdatePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.UpdatePayload>>() {
						}.getType());
				ProxiedServer server = BukkitMain.getInstance().getServerManager().getServer(source);

				if (server == null)
					return;

				if (server instanceof MinigameServer) {
					((MinigameServer) server).setState(payload.getPayload().getState());
					((MinigameServer) server).setTime(payload.getPayload().getTime());
					((MinigameServer) server).setMap(payload.getPayload().getMap());
				}
				break;
			}
			case WHITELIST_ADD: {
				DataServerMessage<DataServerMessage.WhitelistAddPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.WhitelistAddPayload>>() {
						}.getType());

				if (sourceType == ServerType.NETWORK) {
					break;
				}

				ProxiedServer server = BukkitMain.getInstance().getServerManager().getServer(source);

				if (server == null)
					return;

				server.addWhitelist(payload.getPayload().getProfile());
				break;
			}
			case WHITELIST_REMOVE: {
				DataServerMessage<DataServerMessage.WhitelistRemovePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<DataServerMessage.WhitelistRemovePayload>>() {
						}.getType());

				if (sourceType == ServerType.NETWORK) {
					break;
				}

				ProxiedServer server = BukkitMain.getInstance().getServerManager().getServer(source);

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

			if (action.equalsIgnoreCase("remove")) {
				if (CommonGeneral.getInstance().getReportManager().getReport(playerUuid) != null)
					CommonGeneral.getInstance().getReportManager().unloadReport(playerUuid);
			} else if (action.equalsIgnoreCase("create")) {
				if (!jsonObject.has("value"))
					break;

				Report report = CommonConst.GSON.fromJson(jsonObject.get("value"), Report.class);

				if (CommonGeneral.getInstance().getReportManager().getReport(playerUuid) == null) {
					CommonGeneral.getInstance().getReportManager().loadReport(report);
					CommonGeneral.getInstance().debug("The report of " + report.getPlayerName() + " has been loaded!");

					Bukkit.getPluginManager().callEvent(new ReportReceiveEvent(report));
				}
			}

			break;
		}
		case "account-field": {
			UUID uuid = UUID.fromString(jsonObject.get("uniqueId").getAsString());
			Player p = BukkitMain.getInstance().getServer().getPlayer(uuid);
			BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(uuid);

			if (p != null && player != null) {
				try {
					Field field = getField(Member.class, jsonObject.get("field").getAsString());
					Object oldObject = field.get(player);
					Object object = CommonConst.GSON.fromJson(jsonObject.get("value"), field.getGenericType());
					PlayerUpdateFieldEvent event = new PlayerUpdateFieldEvent(p, player, field.getName(), oldObject,
							object);
					Bukkit.getPluginManager().callEvent(event);

					if (!event.isCancelled()) {
						field.set(player, event.getObject());
						PlayerUpdatedFieldEvent event2 = new PlayerUpdatedFieldEvent(p, player, field.getName(),
								oldObject, object);
						Bukkit.getPluginManager().callEvent(event2);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
		}

	}

	private Field getField(Class<?> clazz, String fieldName) {
		while ((clazz != null) && (clazz != Object.class)) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		return null;
	}

}
