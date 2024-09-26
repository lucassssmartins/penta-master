package br.com.pentamc.common.data.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.backend.data.ReportData;
import br.com.pentamc.common.backend.database.mongodb.MongoConnection;
import br.com.pentamc.common.backend.database.redis.RedisDatabase;
import br.com.pentamc.common.report.Report;
import br.com.pentamc.common.utils.json.JsonUtils;
import br.com.pentamc.common.utils.supertype.Callback;
import org.bson.Document;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class ReportDataImpl implements ReportData {

	private RedisDatabase redisDatabase;
	private MongoCollection<Document> reportCollection;

	public ReportDataImpl(MongoConnection mongoDatabase, RedisDatabase redisDatabase) {
		com.mongodb.client.MongoDatabase database = mongoDatabase.getDatabase("saintmc-punish");
		reportCollection = database.getCollection("report");
		this.redisDatabase = redisDatabase;
	}

	@Override
	public Collection<Report> loadReports() {
		List<Report> list = new ArrayList<>();

		MongoCursor<Document> mongoCursor = reportCollection.find().iterator();

		while (mongoCursor.hasNext()) {
			list.add(CommonConst.GSON.fromJson(CommonConst.GSON.toJson(mongoCursor.next()), Report.class));
		}

		return list;
	}

	@Override
	public Report loadReport(UUID uniqueId) {
		Document document = reportCollection.find(Filters.eq("uniqueId", uniqueId.toString())).first();

		if (document == null)
			return null;

		return CommonConst.GSON.fromJson(CommonConst.GSON.toJson(document), Report.class);
	}

	@Override
	public void saveReport(Report report) {
		if (reportCollection.find(Filters.eq("uniqueId", report.getPlayerUniqueId().toString())).first() == null)
			reportCollection.insertOne(Document.parse(CommonConst.GSON.toJson(report)));

		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			try (Jedis jedis = redisDatabase.getPool().getResource()) {
				Pipeline pipeline = jedis.pipelined();
				JsonObject publish = new JsonObject();
				publish.addProperty("uniqueId", report.getPlayerUniqueId().toString());
				publish.add("value", JsonUtils.jsonTree(report));
				publish.addProperty("action", "create");
				publish.addProperty("source", CommonGeneral.getInstance().getServerId());
				pipeline.publish("report-action", publish.toString());
				jedis.sync();
			}
		});
	}

	@Override
	public void saveReport(Report report, Callback<Report> callback) {
		Document document = reportCollection.find(Filters.eq("uniqueId", report.getPlayerUniqueId().toString()))
				.first();

		if (document == null) {
			reportCollection.insertOne(Document.parse(CommonConst.GSON.toJson(report)));
			callback.callback(null);
		} else
			callback.callback(CommonConst.GSON.fromJson(CommonConst.GSON.toJson(document), Report.class));

		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			try (Jedis jedis = redisDatabase.getPool().getResource()) {
				Pipeline pipeline = jedis.pipelined();
				JsonObject publish = new JsonObject();
				publish.addProperty("uniqueId", report.getPlayerUniqueId().toString());
				publish.add("value", JsonUtils.jsonTree(report));
				publish.addProperty("action", "create");
				publish.addProperty("source", CommonGeneral.getInstance().getServerId());
				pipeline.publish("report-action", publish.toString());
				jedis.sync();
			}
		});
	}

	@Override
	public void deleteReport(UUID uniqueId) {
		reportCollection.deleteOne(Filters.eq("uniqueId", uniqueId.toString()));

		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			try (Jedis jedis = redisDatabase.getPool().getResource()) {
				Pipeline pipeline = jedis.pipelined();
				JsonObject publish = new JsonObject();
				publish.addProperty("uniqueId", uniqueId.toString());
				publish.addProperty("action", "remove");
				publish.addProperty("source", CommonGeneral.getInstance().getServerId());
				pipeline.publish("report-action", publish.toString());
				jedis.sync();
			}
		});
	}

	@Override
	public void updateReport(Report report, String fieldName) {
		JsonObject object = JsonUtils.jsonTree(report);

		try {

			if (object.has(fieldName)) {
				Object value = JsonUtils.elementToBson(object.get(fieldName));
				reportCollection.updateOne(Filters.eq("uniqueId", report.getPlayerUniqueId().toString()),
						new Document("$set", new Document(fieldName, value)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			try (Jedis jedis = redisDatabase.getPool().getResource()) {
				JsonElement element = object.get(fieldName);
				Pipeline pipe = jedis.pipelined();

				JsonObject json = new JsonObject();
				json.add("uniqueId", new JsonPrimitive(report.getPlayerUniqueId().toString()));
				json.add("source", new JsonPrimitive(CommonGeneral.getInstance().getServerId()));
				json.add("field", new JsonPrimitive(fieldName));
				json.add("value", element);
				pipe.publish("report-field", json.toString());

				pipe.sync();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void updateName(UUID uniqueId, String playerName) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			reportCollection.updateOne(Filters.eq("uniqueId", uniqueId.toString()),
					new Document("$set", new Document("playerName", playerName)));
		});
	}

}
