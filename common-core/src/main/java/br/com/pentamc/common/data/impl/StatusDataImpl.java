package br.com.pentamc.common.data.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.account.status.Status;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.challenge.ChallengeModel;
import br.com.pentamc.common.account.status.types.challenge.ChallengeStatus;
import br.com.pentamc.common.account.status.types.combat.CombatModel;
import br.com.pentamc.common.account.status.types.combat.CombatStatus;
import br.com.pentamc.common.account.status.types.game.GameModel;
import br.com.pentamc.common.account.status.types.game.GameStatus;
import br.com.pentamc.common.account.status.types.game.pvp.PvPModel;
import br.com.pentamc.common.account.status.types.game.pvp.PvPStatus;
import br.com.pentamc.common.account.status.types.normal.NormalModel;
import br.com.pentamc.common.account.status.types.normal.NormalStatus;
import br.com.pentamc.common.backend.data.StatusData;
import br.com.pentamc.common.backend.database.mongodb.MongoConnection;
import br.com.pentamc.common.utils.json.JsonUtils;
import org.bson.Document;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class StatusDataImpl implements StatusData {

    private com.mongodb.client.MongoDatabase database;

    public StatusDataImpl(MongoConnection mongoDatabase) {
        database = mongoDatabase.getDatabase(mongoDatabase.getDataBase() + "-database");
    }

    @Override
    public Status loadStatus(UUID uniqueId, StatusType statusType) {
        Document document = database.getCollection(statusType.getMongoCollection())
                                    .find(Filters.eq("uniqueId", uniqueId.toString())).first();

        if (document == null)
            return null;

        return CommonConst.GSON.fromJson(CommonConst.GSON.toJson(document), statusType.getStatusClass());
    }

    @Override
    public void saveStatus(Status status) {
        MongoCollection<Document> collection = database.getCollection(status.getStatusType().getMongoCollection());

        if (status instanceof GameStatus) {
            GameModel gameModel = new GameModel((GameStatus) status);

            if (collection.find(Filters.eq("uniqueId", gameModel.getUniqueId().toString())).first() == null) {
                collection.insertOne(Document.parse(CommonConst.GSON.toJson(gameModel)));
            }
        } else if (status instanceof NormalStatus) {
            NormalModel normalModel = new NormalModel((NormalStatus) status);

            if (collection.find(Filters.eq("uniqueId", normalModel.getUniqueId().toString())).first() == null) {
                collection.insertOne(Document.parse(CommonConst.GSON.toJson(normalModel)));
            }
        } else if (status instanceof ChallengeStatus) {
            ChallengeModel challengeModel = new ChallengeModel((ChallengeStatus) status);

            if (collection.find(Filters.eq("uniqueId", challengeModel.getUniqueId().toString())).first() == null) {
                collection.insertOne(Document.parse(CommonConst.GSON.toJson(challengeModel)));
            }
        } else if (status instanceof CombatStatus) {
            CombatModel combatModel = new CombatModel((CombatStatus) status);

            if (collection.find(Filters.eq("uniqueId", combatModel.getUniqueId().toString())).first() == null) {
                collection.insertOne(Document.parse(CommonConst.GSON.toJson(combatModel)));
            }
        } else if (status instanceof PvPStatus) {
            PvPModel pvpModel = new PvPModel((PvPStatus) status);

            if (collection.find(Filters.eq("uniqueId", pvpModel.getUniqueId().toString())).first() == null) {
                collection.insertOne(Document.parse(CommonConst.GSON.toJson(pvpModel)));
            }
        } else {
            new NoSuchElementException("Cannot define the type of StatusModel");
        }
    }

    @Override
    public void updateStatus(Status status, String fieldName) {
        MongoCollection<Document> collection = database.getCollection(status.getStatusType().getMongoCollection());

        if (status instanceof GameStatus) {
            try {
                GameModel gameModel = new GameModel((GameStatus) status);
                JsonObject object = JsonUtils.jsonTree(gameModel);

                if (object.has(fieldName)) {
                    Object value = JsonUtils.elementToBson(object.get(fieldName));
                    collection.updateOne(Filters.eq("uniqueId", gameModel.getUniqueId().toString()),
                                         new Document("$set", new Document(fieldName, value)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (status instanceof NormalStatus) {
            try {
                NormalModel normalModel = new NormalModel((NormalStatus) status);
                JsonObject object = JsonUtils.jsonTree(normalModel);

                if (object.has(fieldName)) {
                    Object value = JsonUtils.elementToBson(object.get(fieldName));
                    collection.updateOne(Filters.eq("uniqueId", normalModel.getUniqueId().toString()),
                                         new Document("$set", new Document(fieldName, value)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (status instanceof ChallengeStatus) {
            try {
                ChallengeModel challenegModel = new ChallengeModel((ChallengeStatus) status);
                JsonObject object = JsonUtils.jsonTree(challenegModel);

                if (object.has(fieldName)) {
                    Object value = JsonUtils.elementToBson(object.get(fieldName));
                    collection.updateOne(Filters.eq("uniqueId", challenegModel.getUniqueId().toString()),
                                         new Document("$set", new Document(fieldName, value)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }  else if (status instanceof CombatStatus) {
            try {
                CombatModel combatModel = new CombatModel((CombatStatus) status);
                JsonObject object = JsonUtils.jsonTree(combatModel);

                if (object.has(fieldName)) {
                    Object value = JsonUtils.elementToBson(object.get(fieldName));
                    collection.updateOne(Filters.eq("uniqueId", combatModel.getUniqueId().toString()),
                            new Document("$set", new Document(fieldName, value)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (status instanceof PvPStatus) {
            try {
                PvPModel combatModel = new PvPModel((PvPStatus) status);
                JsonObject object = JsonUtils.jsonTree(combatModel);

                if (object.has(fieldName)) {
                    Object value = JsonUtils.elementToBson(object.get(fieldName));
                    collection.updateOne(Filters.eq("uniqueId", combatModel.getUniqueId().toString()),
                            new Document("$set", new Document(fieldName, value)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            new NoSuchElementException("Cannot define the type of StatusModel");
        }
    }

    @Override
    public <T extends Status> Collection<T> ranking(StatusType statusType, String fieldName, Class<T> clazz) {
        MongoCollection<Document> collection = database.getCollection(statusType.getMongoCollection());

        MongoCursor<Document> mongo = collection.find().sort(Filters.eq(fieldName, -1)).limit(100).iterator();
        List<T> memberList = new ArrayList<>();

        while (mongo.hasNext()) {
            memberList.add(CommonConst.GSON.fromJson(CommonConst.GSON.toJson(mongo.next()), clazz));
        }

        return memberList;
    }

    @Override
    public void deleteStatus(UUID uniqueId, StatusType status) {
        database.getCollection(status.getMongoCollection()).deleteOne(Filters.eq("uniqueId", uniqueId.toString()));
    }
}
