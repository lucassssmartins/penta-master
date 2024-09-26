package br.com.pentamc.bungee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.backend.Credentials;
import br.com.pentamc.common.backend.database.mongodb.MongoConnection;
import br.com.pentamc.common.backend.database.redis.RedisDatabase;
import br.com.pentamc.common.controller.PacketController;
import br.com.pentamc.common.controller.PunishManager;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.report.Report;
import br.com.pentamc.common.server.ServerManager;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.common.server.loadbalancer.server.MinigameServer;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import br.com.pentamc.common.utils.DateUtils;
import br.com.pentamc.common.utils.string.MessageBuilder;
import com.google.common.io.ByteStreams;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import br.com.pentamc.bungee.command.BungeeCommandFramework;
import br.com.pentamc.bungee.controller.BotController;
import br.com.pentamc.bungee.controller.BungeePunishManager;
import br.com.pentamc.bungee.controller.BungeeServerManager;
import br.com.pentamc.bungee.controller.GiftcodeController;
import br.com.pentamc.bungee.event.server.update.CompetitiveEvent;
import br.com.pentamc.bungee.listener.ChatListener;
import br.com.pentamc.bungee.listener.LoginListener;
import br.com.pentamc.bungee.listener.MessageListener;
import br.com.pentamc.bungee.listener.PacketListener;
import br.com.pentamc.bungee.listener.ServerListener;
import br.com.pentamc.bungee.listener.StoreListener;
import br.com.pentamc.bungee.networking.packet.BungeePacketHandler;
import br.com.pentamc.bungee.networking.redis.BungeePubSubHandler;
import br.com.pentamc.common.backend.data.ClanData;
import br.com.pentamc.common.backend.data.IpData;
import br.com.pentamc.common.backend.data.PlayerData;
import br.com.pentamc.common.backend.data.PunishData;
import br.com.pentamc.common.backend.data.ReportData;
import br.com.pentamc.common.backend.data.ServerData;
import br.com.pentamc.common.data.impl.ClanDataImpl;
import br.com.pentamc.common.data.impl.IpDataImpl;
import br.com.pentamc.common.data.impl.PlayerDataImpl;
import br.com.pentamc.common.data.impl.PunishDataImpl;
import br.com.pentamc.common.data.impl.ReportDataImpl;
import br.com.pentamc.common.data.impl.ServerDataImpl;

@Getter
public class BungeeMain extends Plugin {

    private static final String BROADCAST_PREFIX = "§b§lPENTA §7§l» ";

    private static final TextComponent[] BROADCAST = new TextComponent[]{
            new MessageBuilder(BROADCAST_PREFIX + "§eAcesse nosso ")
                    .addExtre(new MessageBuilder("§d§lDISCORD")
                            .setClickEvent(ClickEvent.Action.OPEN_URL, "https://" + CommonConst.DISCORD)
                            .create())
                    .addExtre(new TextComponent("§e e fique por dentro das novidades!")).create(),
            new MessageBuilder(BROADCAST_PREFIX + "§eUse §b/report <player>§e para denunciar um jogador!").create(),
            new MessageBuilder(BROADCAST_PREFIX + "§eSiga-nos no Twitter §b@PentaMC_ ").create(),
            new MessageBuilder(BROADCAST_PREFIX
                    + "§eO servidor está em fase §1§lBETA§e, caso encontre algum bug reporte em nosso ")
                    .addExtre(new MessageBuilder("§ediscord!")
                            .setClickEvent(ClickEvent.Action.OPEN_URL, "https://" + CommonConst.DISCORD)
                            .create())
                    .create()};

    @Getter
    private static BungeeMain instance;

    private CommonGeneral general;

    private MongoConnection mongo;
    private RedisDatabase redis;

    private PunishManager punishManager;
    private BotController botController;
    private ServerManager serverManager;

    private PacketController packetController;
    private GiftcodeController giftcodeController;

    private RedisDatabase.PubSubListener pubSubListener;

    private Configuration config;

    @Setter
    private Map<Boolean, Group> maintenance = new HashMap<>();

    private ScheduledTask redisTask;

    @Override
    public void onLoad() {
        general = new CommonGeneral(ProxyServer.getInstance().getLogger());
        instance = this;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEnable() {
        loadConfiguration();

        /**
         * Initializing Database
         */

        try {

            /*
             * Backend Initialize
             */

            mongo = new MongoConnection(new Credentials(
                    getConfig().getString("mongodb.hostname", "127.0.0.1"),
                    getConfig().getString("mongodb.username", "root"),
                    getConfig().getString("mongodb.password", ""),
                    getConfig().getString("mongodb.database", "admin"),
                    27017)
            );

            redis = new RedisDatabase(
                    getConfig().getString("redis.hostname", "127.0.0.1"),
                    getConfig().getString("redis.password", ""),
                    6379
            );

            mongo.connect();
            redis.connect();

            PlayerData playerData = new PlayerDataImpl(mongo, redis);
            ServerData serverData = new ServerDataImpl(mongo, redis);
            ReportData reportData = new ReportDataImpl(mongo, redis);
            ClanData clanData = new ClanDataImpl(mongo, redis);
            PunishData punishData = new PunishDataImpl(mongo);
            IpData ipData = new IpDataImpl(mongo);

            general.setPlayerData(playerData);
            general.setServerData(serverData);
            general.setReportData(reportData);
            general.setClanData(clanData);
            general.setPunishData(punishData);
            general.setIpData(ipData);

            loadRedis(redis);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Logger logger = ProxyServer.getInstance().getLogger();
        Logger newLogger = new Logger("BungeeCord", null) {

            public void log(Level level, String msg, Object param1) {
                if (msg.contains("<->") || msg.contains("->")) {
                    return;
                }

                super.log(level, msg, param1);
            }
        };

        newLogger.setParent(logger);

        /**
         * Initializing Constructor
         */

        general.setCommonPlatform(new BungeePlatform());
        new BungeeCommandFramework(this).loadCommands("br.com.pentamc.bungee.command.register");

        botController = new BotController();
        punishManager = new BungeePunishManager();
        serverManager = new BungeeServerManager();
        giftcodeController = new GiftcodeController();

        packetController = new PacketController();
        packetController.registerHandler(new BungeePacketHandler());

        ProxyServer.getInstance().getServers().remove("lobby");

        /**
         * Server Info
         */

        ProxyServer.getInstance().registerChannel("server:packet");

        ListenerInfo info = getProxy().getConfig().getListeners().iterator().next();
        general.setServerAddress(info.getHost().getHostString() + ":" + info.getHost().getPort());
        general.setServerId(general.getServerData().getServerId(general.getServerAddress()));
        general.setServerType(general.getServerData().getServerType(general.getServerAddress()));

        general.debug("The server has been loaded " + general.getServerAddress() + " (" + general.getServerId() + " - "
                + general.getServerType().toString() + ")");

        general.getServerData().startServer(info.getMaxPlayers());

        general.debug("The server has been sent the start message to redis!");

        for (Entry<String, Map<String, String>> entry : general.getServerData().loadServers().entrySet()) {
            try {
                if (!entry.getValue().containsKey("type")) {
                    continue;
                }

                if (!entry.getValue().containsKey("address")) {
                    continue;
                }

                if (!entry.getValue().containsKey("maxplayers")) {
                    continue;
                }

                if (!entry.getValue().containsKey("onlineplayers")) {
                    continue;
                }

                if (ServerType.valueOf(entry.getValue().get("type").toUpperCase()) == ServerType.NETWORK) {
                    continue;
                }

                ProxiedServer server = getServerManager().addActiveServer(entry.getValue().get("address"),
                        entry.getKey(), ServerType.valueOf(
                                entry.getValue().get("type").toUpperCase()),
                        Integer.valueOf(
                                entry.getValue().get("maxplayers")));

                getServerManager().getServer(entry.getKey())
                        .setOnlinePlayers(general.getServerData().getPlayers(entry.getKey()));
                getServerManager().getServer(entry.getKey())
                        .setJoinEnabled(Boolean.valueOf(entry.getValue().get("joinenabled")));

                if (server instanceof MinigameServer) {
                    MinigameServer minigameServer = (MinigameServer) server;

                    minigameServer.setTime(general.getServerData().getTime(entry.getKey()));
                    minigameServer.setMap(general.getServerData().getMap(entry.getKey()));
                    minigameServer.setState(general.getServerData().getState(entry.getKey()));
                }
            } catch (Exception e) {
            }
        }

        general.debug("The server has been loaded all the servers!");

        for (Report report : general.getReportData().loadReports()) {
            general.getReportManager().loadReport(report);
        }

        general.debug("The server has been loaded all the reports!");

        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            TextComponent message = BROADCAST[CommonConst.RANDOM.nextInt(BROADCAST.length)];

            ProxyServer.getInstance().broadcast(message);
        }, 0, 5, TimeUnit.MINUTES);

        System.setProperty("DEBUG.MONGO", "false");
        System.setProperty("DB.TRACE", "false");

        registerListener();

        /*
         * Competitive event
         */
        getProxy().getScheduler().schedule(this, () -> {
            getProxy().getPluginManager().callEvent(new CompetitiveEvent());
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    public void loadRedis(RedisDatabase redisDatabase) {
        if (redisTask != null) {
            redisTask.cancel();
        }

        redisTask = getProxy().getScheduler().runAsync(this,
                pubSubListener = new RedisDatabase.PubSubListener(redisDatabase,
                        new BungeePubSubHandler(),
                        "server-info",
                        "account-field",
                        "clan-field", "report-field",
                        "report-action",
                        "server-members",
                        "competitive-channel"));
    }

    @Override
    public void onDisable() {
        general.getServerData().stopServer();

        general.getServerData().closeConnection();
        general.getPlayerData().closeConnection();
    }

    private void registerListener() {
        getProxy().getPluginManager().registerListener(this, new LoginListener());
        getProxy().getPluginManager().registerListener(this, new ChatListener());
        getProxy().getPluginManager().registerListener(this, new PacketListener());
        getProxy().getPluginManager().registerListener(this, new MessageListener(serverManager));
        getProxy().getPluginManager().registerListener(this, new ServerListener(serverManager));
        getProxy().getPluginManager().registerListener(this, new StoreListener());
    }

    private void loadConfiguration() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }

            File configFile = new File(getDataFolder(), "config.yml");

            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                    try (InputStream is = getResourceAsStream("config.yml");
                         OutputStream os = new FileOutputStream(configFile)) {
                        ByteStreams.copy(is, os);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Unable to create configuration file", e);
                }
            }

            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void reloadConfig() {
        try {
            File configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists())
                getLogger().info("Config file was deleted.");
            else
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static BungeeMain getPlugin() {
        return instance;
    }
}
