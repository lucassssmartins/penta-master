package br.com.pentamc.common.data.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.backend.Query;
import br.com.pentamc.common.backend.data.ServerData;
import br.com.pentamc.common.backend.database.mongodb.MongoConnection;
import br.com.pentamc.common.backend.database.mongodb.MongoQuery;
import br.com.pentamc.common.backend.database.redis.RedisDatabase;
import br.com.pentamc.common.data.payload.DataServerMessage;
import br.com.pentamc.common.profile.Profile;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.common.server.loadbalancer.server.MinigameState;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import br.com.pentamc.common.utils.json.JsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class ServerDataImpl implements ServerData {

	private RedisDatabase redisDatabase;
	@Getter
	private Query<JsonElement> query;
	
	public ServerDataImpl(MongoConnection mongoConnection, RedisDatabase redisDatabase) {
		this.query = createDefault(mongoConnection);
		this.redisDatabase = redisDatabase;
	}

	public ServerDataImpl(Query<JsonElement> query, RedisDatabase redisDatabase) {
		this.query = query;
		this.redisDatabase = redisDatabase;
	}

	@Override
	public String getServerId(String ipAddress) {
		try {
			JsonObject found = (JsonObject) query.findOne("address", ipAddress);

			if (found != null) {
				return found.get("hostname").getAsString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ipAddress;
	}

	@Override
	public ServerType getServerType(String ipAddress) {

		try {
			JsonObject found = (JsonObject) query.findOne("address", ipAddress);

			if (found != null) {
				return ServerType.valueOf(found.get("serverType").getAsString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ServerType.NONE;
	}

	@Override
	public int getTime(String serverId) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Map<String, String> m = jedis.hgetAll("server:" + serverId);

			if (m.containsKey("time"))
				return Integer.valueOf(m.get("time"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	@Override
	public MinigameState getState(String serverId) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Map<String, String> m = jedis.hgetAll("server:" + serverId);

			if (m.containsKey("state"))
				return MinigameState.valueOf(m.get("state").toUpperCase());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return MinigameState.NONE;
	}

	@Override
	public String getMap(String serverId) {

		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Map<String, String> m = jedis.hgetAll("server:" + serverId);

			if (m.containsKey("map"))
				return m.get("map");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "Unknown";
	}

	@Override
	public Map<String, Map<String, String>> loadServers() {
		Map<String, Map<String, String>> map = new HashMap<>();
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			String[] str = new String[ServerType.values().length];
			for (int i = 0; i < ServerType.values().length; i++) {
				str[i] = "server:type:" + ServerType.values()[i].toString().toLowerCase();
			}
			for (String server : jedis.sunion(str)) {
				Map<String, String> m = jedis.hgetAll("server:" + server);
				m.put("onlineplayers", getPlayerCount(server) + "");
				map.put(server, m);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
		return map;
	}

	@Override
	public Set<UUID> getPlayers(String serverId) {
		Set<UUID> players = new HashSet<>();
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			for (String uuid : jedis.smembers("server:" + serverId + ":players")) {
				UUID uniqueId = UUID.fromString(uuid);
				players.add(uniqueId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return players;
	}

	@Override
	public void startServer(int maxPlayers) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.sadd("server:type:" + CommonGeneral.getInstance().getServerType().toString().toLowerCase(),
					CommonGeneral.getInstance().getServerId());
			Map<String, String> map = new HashMap<>();
			map.put("type", CommonGeneral.getInstance().getServerType().toString().toLowerCase());
			map.put("maxplayers", maxPlayers + "");
			map.put("joinenabled", "true");
			map.put("address", CommonGeneral.getInstance().getServerAddress());
			pipe.hmset("server:" + CommonGeneral.getInstance().getServerId(), map);
			pipe.del("server:" + CommonGeneral.getInstance().getServerId() + ":players");
			pipe.del("server:" + CommonGeneral.getInstance().getServerId() + ":whitelist");
			ProxiedServer server = new ProxiedServer(CommonGeneral.getInstance().getServerId(),
					CommonGeneral.getInstance().getServerType(), new HashSet<>(), new HashSet<>(), maxPlayers, true);
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<DataServerMessage.StartPayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							DataServerMessage.Action.START, new DataServerMessage.StartPayload(CommonGeneral.getInstance().getServerAddress(), server))));
			pipe.sync();
		}
	}

	@Override
	public void updateStatus(MinigameState state, int time) {
		updateStatus(state, "Unknown", time);
	}

	@Override
	public void updateStatus(MinigameState state, String map, int time) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.hset("server:" + CommonGeneral.getInstance().getServerId(), "map", map);
			pipe.hset("server:" + CommonGeneral.getInstance().getServerId(), "time", Integer.toString(time));
			pipe.hset("server:" + CommonGeneral.getInstance().getServerId(), "state", state.toString().toLowerCase());
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<DataServerMessage.UpdatePayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							DataServerMessage.Action.UPDATE, new DataServerMessage.UpdatePayload(time, map, state))));
			pipe.sync();
		}
	}

	@Override
	public void setJoinEnabled(boolean bol) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.hset("server:" + CommonGeneral.getInstance().getServerId(), "joinenabled", Boolean.toString(bol));
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<DataServerMessage.JoinEnablePayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							DataServerMessage.Action.JOIN_ENABLE, new DataServerMessage.JoinEnablePayload(bol))));
			pipe.sync();
		}
	}

	@Override
	public void stopServer() {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.srem("server:type:" + CommonGeneral.getInstance().getServerType().toString().toLowerCase(),
					CommonGeneral.getInstance().getServerId());
			pipe.del("server:" + CommonGeneral.getInstance().getServerId());
			pipe.del("server:" + CommonGeneral.getInstance().getServerId() + ":players");
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<DataServerMessage.StopPayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							DataServerMessage.Action.STOP, new DataServerMessage.StopPayload(CommonGeneral.getInstance().getServerId()))));
			pipe.sync();
		}
	}

	@Override
	public void setTotalMembers(int totalMembers) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.publish("server-members",
					CommonConst.GSON.toJson(new JsonBuilder().addProperty("totalMembers", totalMembers).build()));
			pipe.sync();
		}
	}

	@Override
	public void joinPlayer(UUID uuid) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.sadd("server:" + CommonGeneral.getInstance().getServerId() + ":players", uuid.toString());
			pipe.publish("server-info",
					CommonConst.GSON
							.toJson(new DataServerMessage<DataServerMessage.JoinPayload>(CommonGeneral.getInstance().getServerId(),
									CommonGeneral.getInstance().getServerType(), DataServerMessage.Action.JOIN, new DataServerMessage.JoinPayload(uuid))));
			pipe.sync();
		}
	}

	@Override
	public void addWhitelist(Profile profile) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.sadd("server:" + CommonGeneral.getInstance().getServerId() + ":whitelist",
					CommonConst.GSON.toJson(profile));
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<DataServerMessage.WhitelistAddPayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							DataServerMessage.Action.WHITELIST_ADD, new DataServerMessage.WhitelistAddPayload(profile))));
			pipe.sync();
		}
	}

	@Override
	public void leavePlayer(UUID uuid) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.srem("server:" + CommonGeneral.getInstance().getServerId() + ":players", uuid.toString());
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<DataServerMessage.LeavePayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							DataServerMessage.Action.LEAVE, new DataServerMessage.LeavePayload(uuid))));
			pipe.sync();
		}
	}

	@Override
	public void removeWhitelist(Profile profile) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.srem("server:" + CommonGeneral.getInstance().getServerId() + ":whitelist",
					CommonConst.GSON.toJson(profile));
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<DataServerMessage.WhitelistRemovePayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							DataServerMessage.Action.WHITELIST_REMOVE, new DataServerMessage.WhitelistRemovePayload(profile))));
			pipe.sync();
		}
	}

	@Override
	public long getPlayerCount(String serverId) {
		long number;
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			number = jedis.scard("server:" + serverId + ":players");
		}
		return number;
	}

	@Override
	public long getPlayerCount(ServerType serverType) {
		long number = 0l;

		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Set<String> servers = jedis.smembers("server:type:" + serverType.toString().toLowerCase());
			for (String serverId : servers) {
				number += jedis.scard("server:" + serverId + ":players");
			}
		}

		return number;
	}

	@Override
	public void closeConnection() {
		redisDatabase.close();
	}

	public static Query<JsonElement> createDefault(MongoConnection mongoConnection) {
		return new MongoQuery(mongoConnection, mongoConnection.getDataBase(), "serverId");
	}

}
