package br.com.pentamc.common.data.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.backend.Query;
import br.com.pentamc.common.backend.data.ClanData;
import br.com.pentamc.common.backend.database.mongodb.MongoConnection;
import br.com.pentamc.common.backend.database.mongodb.MongoQuery;
import br.com.pentamc.common.backend.database.redis.RedisDatabase;
import br.com.pentamc.common.utils.json.JsonBuilder;
import br.com.pentamc.common.utils.json.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import br.com.pentamc.common.clan.Clan;
import br.com.pentamc.common.clan.ClanModel;

public class ClanDataImpl implements ClanData {

	private RedisDatabase redisDatabase;
	private Query<JsonElement> query;

	public ClanDataImpl(MongoConnection mongoConnection, RedisDatabase redisDatabase) {
		this.query = createDefault(mongoConnection);
		this.redisDatabase = redisDatabase;
	}

	@Override
	public void createClan(Clan clan) {
		ClanModel clanModel = new ClanModel(clan);
		boolean needCreate = query.findOne("uniqueId", clanModel.getUniqueId().toString()) == null;

		if (needCreate)
			query.create(new String[] { CommonConst.GSON.toJson(clanModel) });

		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

			@Override
			public void run() {
				try (Jedis jedis = redisDatabase.getPool().getResource()) {
					jedis.hmset("clan:" + clanModel.getUniqueId().toString(), JsonUtils.objectToMap(clanModel));
				}
			}
		});
	}

	@Override
	public ClanModel loadClan(UUID uniqueId) {
		ClanModel clanModel = CommonGeneral.getInstance().getClanManager().getClanAsModel(uniqueId);

		if (clanModel == null) {
			JsonElement found = query.findOne("uniqueId", uniqueId.toString());

			if (found != null) {
				clanModel = CommonConst.GSON.fromJson(CommonConst.GSON.toJson(found), ClanModel.class);
			}
		}

		return clanModel;
	}

	@Override
	public ClanModel loadClan(String clanName) {
		ClanModel clanModel = CommonGeneral.getInstance().getClanManager().getClanAsModel(clanName, true);

		if (clanModel == null) {
			JsonElement found = query.findOne("clanName",
					Pattern.compile("^" + clanName + "$", Pattern.CASE_INSENSITIVE));

			if (found != null) {
				clanModel = CommonConst.GSON.fromJson(CommonConst.GSON.toJson(found), ClanModel.class);
			}
		}

		return clanModel;
	}

	@Override
	public ClanModel loadClanByAbbreviation(String clanAbbreviation) {
		ClanModel clanModel = CommonGeneral.getInstance().getClanManager()
				.getClanAsModelByAbbreviation(clanAbbreviation, true);

		if (clanModel == null) {
			JsonElement found = query.findOne("clanAbbreviation",
					Pattern.compile("^" + clanAbbreviation + "$", Pattern.CASE_INSENSITIVE));

			if (found != null) {
				clanModel = CommonConst.GSON.fromJson(CommonConst.GSON.toJson(found), ClanModel.class);
			}
		}

		return clanModel;
	}

	@Override
	public void updateClan(Clan clan, String fieldName) {
		ClanModel clanModel = new ClanModel(clan);
		JsonObject object = JsonUtils.jsonTree(clanModel);

		if (object.has(fieldName)) {
			query.updateOne("uniqueId", clan.getUniqueId().toString(),
					new JsonBuilder().addProperty("fieldName", fieldName).add("value", object.get(fieldName)).build());
		}

		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

			@Override
			public void run() {
				JsonObject tree = CommonConst.GSON.toJsonTree(clan).getAsJsonObject();

				if (tree.has(fieldName)) {
					JsonElement element = tree.get(fieldName);
					try (Jedis jedis = redisDatabase.getPool().getResource()) {
						Pipeline pipe = jedis.pipelined();
						jedis.hset("clan:" + clan.getUniqueId().toString(), fieldName, JsonUtils.elementToString(element));

						JsonObject json = new JsonObject();
						json.add("uniqueId", new JsonPrimitive(clan.getUniqueId().toString()));
						json.add("source", new JsonPrimitive(CommonGeneral.getInstance().getServerId()));
						json.add("field", new JsonPrimitive(fieldName));
						json.add("value", element);
						pipe.publish("account-field", json.toString());

						pipe.sync();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void deleteClan(Clan clan) {
		query.deleteOne("uniqueId", clan.getUniqueId().toString());
	}

	@Override
	public Collection<ClanModel> ranking(String fieldName, int limit, int order) {
		List<ClanModel> list = new ArrayList<>();

		for (JsonElement element : query.ranking(fieldName, order, limit)) {
			list.add(CommonConst.GSON.fromJson(CommonConst.GSON.toJson(element), ClanModel.class));
		}

		return list;
	}

	public static Query<JsonElement> createDefault(MongoConnection mongoConnection) {
		return new MongoQuery(mongoConnection, mongoConnection.getDataBase(), "clan");
	}

}
