package br.com.pentamc.competitive;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import br.com.pentamc.bukkit.networking.redis.BukkitPubSubHandler;
import br.com.pentamc.common.backend.database.redis.RedisDatabase;
import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.controller.TeamManager;
import br.com.pentamc.competitive.controller.VarManager;
import br.com.pentamc.competitive.game.Game;
import br.com.pentamc.competitive.game.Team;
import br.com.pentamc.competitive.listener.register.*;
import br.com.pentamc.competitive.networking.GamePublish;
import br.com.pentamc.competitive.scheduler.SchedulerListener;
import br.com.pentamc.competitive.utils.ServerConfig;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.command.BukkitCommandFramework;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.common.server.loadbalancer.server.MinigameState;
import br.com.pentamc.common.tag.Tag;
import br.com.pentamc.common.tag.TagWrapper;

import static org.junit.Assert.assertEquals;

@Getter
public class GameMain extends JavaPlugin {

    public static final Game GAME = new Game(0, 90);

    public static final Map<Group, List<String>> KITROTATE;

    static {
        KITROTATE = new HashMap<>();

        KITROTATE.put(Group.MEMBRO, Arrays.asList(
                "surprise", "lumberjack", "miner", "lumberjack", "reaper", "magma", "kaya", "endermage", "worm"
        ));

        KITROTATE.put(Group.VIP, Arrays.asList(
                "snail", "thor", "anchor", "ninja", "stomper", "grappler", "kangaroo", "boxer", "ironman", "gladiator", "endermage", "ultimato"
        ));

        KITROTATE.put(Group.PENTA, Arrays.asList(
                "turtle", "viper", "viking", "tank", "specialist", "poseidon"
        ));
    }

    @Getter
    private static GameMain instance;

    private GameGeneral general;

    private RedisDatabase redis;
    private RedisDatabase.PubSubListener pubSubListener;

    private String roomId;

    private VarManager varManager;
    private TeamManager teamManager;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        instance = this;

        general = new GameGeneral();
        general.onLoad();
    }

    @Override
    public void onEnable() {
        try {
            redis = new RedisDatabase(
                    "127.0.0.1",
                    "",
                    6379
            );

            redis.connect();

            getServer().getScheduler().runTaskAsynchronously(getInstance(),
                    pubSubListener = new RedisDatabase.PubSubListener(redis,
                            new GamePublish(),
                            "competitive-channel"));
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.shutdown();
            return;
        }

        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
            public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
                if (event.getLoginResult() != Result.ALLOWED) {
                    return;
                }

                if (!ServerConfig.getInstance().isJoinEnabled()) {
                    event.disallow(Result.KICK_OTHER,
                                   GameGeneral.getInstance().getGameState().isPregame() ?
                                   "§cO servidor está carregando!" :
                                   "§cO servidor não está permitindo que jogadores entre no momento!");
                }
            }
        };

        varManager = new VarManager();
        teamManager = new TeamManager();

        Bukkit.getPluginManager().registerEvents(listener, getInstance());
        BukkitCommandFramework.INSTANCE.loadCommands(this.getClass(), "br.com.pentamc.competitive.command");
        BukkitMain.getInstance().setRemovePlayerDat(false);

        if (roomId == null) {
            String[] split = CommonGeneral.getInstance().getServerId().split("\\.");

            if (split.length > 1) {
                roomId = split[0].toUpperCase();
            } else {
                roomId = CommonGeneral.getInstance().getServerId();
            }
        }

        loadListener();
        saveResource("cake.png", true);
        saveResource("arena.schematic", true);
        saveResource("arena-final.schematic", true);
        saveResource("arena-wither.schematic", true);
        general.onEnable();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable() {

            public void run() {
                CommonGeneral.getInstance().debug("[World] Initializing the world configuration!");
                World world = getServer().getWorld("world");
                world.setSpawnLocation(0, getServer().getWorlds().get(0).getHighestBlockYAt(0, 0) + 5, 0);

                world.setAutoSave(false);
                ((CraftWorld) world).getHandle().savingDisabled = true;

                CommonGeneral.getInstance().debug("[World] Loading the chunks!");

                long pid = getPID();
                long time = System.currentTimeMillis();

//                try {
//                    for (int x = 0; x <= 28; x++) {
//                        for (int z = 0; z <= 28; z++) {
//                            world.getSpawnLocation().clone().add(x * 16, 0, z * 16).getChunk().load();
//                            world.getSpawnLocation().clone().add(x * -16, 0, z * -16).getChunk().load();
//                            world.getSpawnLocation().clone().add(x * 16, 0, z * -16).getChunk().load();
//                            world.getSpawnLocation().clone().add(x * -16, 0, z * 16).getChunk().load();
//                        }
//
//                        if (x % 2 == 0) {
//                            CommonGeneral.getInstance().debug("[World] " + StringUtils.formatTime(
//                                    (int) ((System.currentTimeMillis() - time) / 1000)) + " have passed! PID: " + pid +
//                                                              " - used mem: " + ((Runtime.getRuntime().totalMemory() -
//                                                                                  Runtime.getRuntime().freeMemory()) /
//                                                                                 2L / 1048576L));
//                        }
//                    }
//                } catch (OutOfMemoryError ex) {
//
//                }

                CommonGeneral.getInstance().debug("[World] All chunks has been loaded!");

                world.setDifficulty(Difficulty.NORMAL);

                if (world.hasStorm()) {
                    world.setStorm(false);
                }

                world.setTime(0L);
                world.setWeatherDuration(999999999);
                world.setGameRuleValue("doDaylightCycle", "false");
                world.setGameRuleValue("announceAdvancements", "false");
                org.bukkit.WorldBorder border = world.getWorldBorder();
                border.setCenter(0, 0);
                border.setSize(800);

                createSquare(new Location(world, 0, 0, 0), Material.BEDROCK, 0, 3, 3);
                createSquare(new Location(world, 0, 1, 0), Material.AIR, 0, 2, 3);
                createSquare(new Location(world, 0, 1, 0), Material.AIR, 0, 1, 3);
                createSquare(new Location(world, 0, 1, 0), Material.AIR, 0, 0, 3);

                CommonGeneral.getInstance().debug("[World] World has been loaded!");

                for (Entity e : world.getEntities())
                    e.remove();

                CommonGeneral.getInstance().getServerData().updateStatus(MinigameState.WAITING, 300);
                ServerConfig.getInstance().setJoinEnabled(true);
                HandlerList.unregisterAll(listener);
            }
        });

        super.onEnable();
    }

    @Override
    public void onDisable() {
        general.onDisable();
        redis.close();
        super.onDisable();
    }

    public boolean isAllowEntry() {
        return getVarManager().getVar(GameConst.ALLOW_ENTRY, false);
    }

    /**
     * Get all alive players in the game
     *
     * @return
     */

    public List<Gamer> getAlivePlayers() {
        return getGeneral().getGamerController().getGamers().stream().filter(Gamer::isPlaying)
                           .collect(Collectors.toList());
    }

    /**
     * Get all alive teams in the game
     *
     * @return
     */

    public List<Team> getAliveTeams() {
        return getTeamManager().getTeams().stream().filter(Team::isAlive).collect(Collectors.toList());
    }

    public boolean isTeamEnabled() {
        return getVarManager().getVar(GameConst.TEAM_STATE, false);
    }

    public int getMaxPlayersPerTeam() {
        return getVarManager().getVar(GameConst.MAX_PLAYERS_PER_TEAM_VAR, 2);
    }

    public void loadListener() {
        Bukkit.getPluginManager().registerEvents(new UpdateListener(), this);
        Bukkit.getPluginManager().registerEvents(new BorderListener(), this);
        Bukkit.getPluginManager().registerEvents(new GamerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);
        Bukkit.getPluginManager().registerEvents(new KitListener(), this);
        Bukkit.getPluginManager().registerEvents(new RestoreListener(), this);
        Bukkit.getPluginManager().registerEvents(new SchedulerListener(getGeneral()), this);
        Bukkit.getPluginManager().registerEvents(new SoupListener(), this);
    }

    public static long getPID() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, getInstance());
    }

    public void sendPlayerToHungerGames(Player p) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Hungergames");
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
    }

    public static GameMain getPlugin() {
        return instance;
    }

    @SuppressWarnings("deprecation")
    public void createSquare(Location location, Material material, int id, int radius, int height) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Location currentLocation = location.clone().add(x, 0, z);

                if (z == radius || z == -radius || x == radius || x == -radius) {
                    for (int y = 1; y <= height; y++) {
                        Location actualLocation = currentLocation.clone().add(0, y, 0);

                        actualLocation.getBlock().setType(material);
                        actualLocation.getBlock().setData((byte) id);
                    }
                }

                currentLocation.getBlock().setType(material);
                currentLocation.getBlock().setData((byte) id);
            }
        }
    }
}
