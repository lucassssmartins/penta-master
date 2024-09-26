package br.com.pentamc.pvp;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.command.BukkitCommandFramework;
import br.com.pentamc.pvp.controller.CombatController;
import br.com.pentamc.pvp.controller.GameController;
import br.com.pentamc.pvp.controller.GladiatorController;
import br.com.pentamc.pvp.controller.UserController;
import br.com.pentamc.pvp.hologram.GameRankingHologram;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.listener.ListenerLoader;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameMain extends JavaPlugin {
    @Getter
    private static GameMain plugin;

    protected GameController gameController;
    protected UserController userController;
    protected CombatController combatController;
    protected GladiatorController gladiatorController;

    protected List<KitType> kitRotation;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        plugin = this;
    }

    @Override
    public void onEnable() {
        gameController = new GameController();
        userController = new UserController();
        combatController = new CombatController();
        gladiatorController = new GladiatorController();

        kitRotation = new ArrayList<>();

        gameController.registry(this, "br.com.pentamc.pvp.game.list");

        for (String kit : getConfig().getStringList("kit-rotation"))
            kitRotation.add(KitType.valueOf(kit.toUpperCase()));

        ListenerLoader listener = new ListenerLoader(this, "br.com.pentamc.pvp");
        BukkitCommandFramework command = BukkitCommandFramework.INSTANCE;
        GameRankingHologram hologram = new GameRankingHologram();

        listener.registry();
        command.loadCommands(getClass(), "br.com.pentamc.pvp.command");
        hologram.registry();
    }

    @Override
    public void onDisable() {
        try (Jedis jedis = BukkitMain.getInstance().getRedis().getPool().getResource()) {
            jedis.del("route:*");
        }
    }
}
