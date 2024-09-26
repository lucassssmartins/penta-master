package br.com.pentamc.pvp.util.world;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Set;

public class WorldUtil {

    public static void setup(World world) {

        world.setPVP(true);
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("naturalRegeneration", "false");
        world.setGameRuleValue("sendCommandFeedback", "false");
        world.setGameRuleValue("logAdminCommands", "false");

        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(Integer.MIN_VALUE);
        world.setThunderDuration(Integer.MIN_VALUE);

        world.setSpawnLocation(0, 71, 0);
        world.setAutoSave(false);
        world.setTime(6000);

        world.getEntities().forEach(Entity::remove);
    }
}