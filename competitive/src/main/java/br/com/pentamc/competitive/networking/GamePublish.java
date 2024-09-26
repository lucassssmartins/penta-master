package br.com.pentamc.competitive.networking;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.competitive.GameConst;
import br.com.pentamc.competitive.GameMain;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

public class GamePublish extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        JsonObject object = JsonParser.parseString(message).getAsJsonObject();

        if (channel.equals("competitive-channel")) {
            String
                    serverId = object.get("serverId").getAsString(),
                    type = object.get("eventType").getAsString(),
                    skit = object.get("skit").getAsString();

            if (CommonGeneral.getInstance().getServerId().equals(serverId)) {
                if (type.equalsIgnoreCase("panela")) {
                    GameMain.getInstance().getVarManager().setVar(GameConst.TEAM_STATE, false);
                    GameMain.getInstance().getVarManager().setVar(GameConst.MODE_NAME, "Panela");
                } else {
                    GameMain.getInstance().getVarManager().setVar(GameConst.TEAM_STATE, type.equalsIgnoreCase("dupla"));
                    GameMain.getInstance().getVarManager().setVar(GameConst.MODE_NAME, GameMain.getInstance().getVarManager().getVar(GameConst.TEAM_STATE, false) ? "Dupla" : "Solo");
                }

                GameMain.getInstance().getVarManager().setVar(GameConst.ALLOW_ENTRY, true);
                GameMain.getInstance().getVarManager().setVar(GameConst.SKIT_NAME, skit);

                Bukkit.getScheduler().runTask(GameMain.getInstance(), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "openevento " + type);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skit default " + GameMain.getInstance().getVarManager().getVar(GameConst.SKIT_NAME, "1"));
                });
            }
        }
    }
}
