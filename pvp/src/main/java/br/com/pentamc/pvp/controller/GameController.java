package br.com.pentamc.pvp.controller;

import br.com.pentamc.common.controller.StoreController;
import br.com.pentamc.common.utils.ClassGetter;
import br.com.pentamc.pvp.game.Game;
import br.com.pentamc.pvp.game.type.GameType;
import br.com.pentamc.pvp.util.world.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.plugin.Plugin;

public class GameController extends StoreController<GameType, Game> {

    public void registry(Plugin plugin, String packageName) {
        plugin.getLogger().warning("> Preparations for loading the games...");

        int count = 0;

        for (Class<?> clazz : ClassGetter.getClassesForPackage(plugin.getClass(), packageName))
            if (Game.class.isAssignableFrom(clazz))
                try {
                    Game game = (Game) clazz.newInstance();
                    GameType type = game.getType();

                    WorldCreator creator = new WorldCreator(type.getWorldName());

                    creator.generateStructures(false);
                    creator.type(WorldType.NORMAL);

                    World world = Bukkit.createWorld(creator);

                    WorldUtil.setup(world);

                    load(type, game);

                    count++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

        plugin.getLogger().warning("> " + count + " games loaded!");
    }
}
