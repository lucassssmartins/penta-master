package br.com.pentamc.bungee.listener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bungee.BungeeMain;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.configuration.LoginConfiguration;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.server.ServerManager;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.common.server.loadbalancer.server.MinigameState;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import br.com.pentamc.common.utils.ip.Session;
import br.com.pentamc.common.utils.string.MessageBuilder;
import br.com.pentamc.common.utils.string.StringCenter;
import br.com.pentamc.common.utils.string.StringUtils;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.AccountType;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.SearchServerEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import br.com.pentamc.bungee.bungee.BungeeMember;
import br.com.pentamc.bungee.event.server.ServerUpdateEvent;
import br.com.pentamc.bungee.event.server.update.CompetitiveEvent;

@SuppressWarnings("deprecation")
public class ServerListener implements Listener {

	private static final String MOTD_HEADER = StringCenter.centered("§b§lPENTA §7» §a" + CommonConst.STORE, 127);
	private static final String SERVER_NOT_FOUND = StringCenter.centered("§4§nServidor não encontrado!", 127);

	private static final String[] MOTD_LIST = new String[] { StringCenter.centered("§6§lVENHA JOGAR!", 127) };

	private ServerManager manager;

	public ServerListener(ServerManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPostLogin(PostLoginEvent event) {
		BungeeMain.getInstance().getServerManager().setTotalMembers(ProxyServer.getInstance().getOnlineCount());
		CommonGeneral.getInstance().getServerData().setTotalMembers(ProxyServer.getInstance().getOnlineCount());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		BungeeMain.getInstance().getServerManager().setTotalMembers(ProxyServer.getInstance().getOnlineCount() - 1);
		CommonGeneral.getInstance().getServerData().setTotalMembers(ProxyServer.getInstance().getOnlineCount() - 1);
	}

	@EventHandler
	public void onServerKick(ServerKickEvent event) {
		ProxiedPlayer player = event.getPlayer();
		ProxiedServer kickedFrom = manager.getServer(event.getKickedFrom().getName());

		if (kickedFrom == null) {
			player.disconnect(event.getKickReasonComponent());
			return;
		}

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (member == null) {
			player.disconnect(event.getKickReasonComponent());
			return;
		}

		ProxiedServer fallbackServer = member.getLoginConfiguration().isLogged()
				? manager.getBalancer(kickedFrom.getServerType().getServerLobby()).next()
				: null;

		if (fallbackServer == null || fallbackServer.getServerInfo() == null) {
			event.getPlayer().disconnect(event.getKickReasonComponent());
			return;
		}

		if (kickedFrom.getServerType() == fallbackServer.getServerType()) {
			player.disconnect(event.getKickReasonComponent());
			return;
		}

		String message = event.getKickReason();

		for (String m : message.split("\n")) {
			player.sendMessage(TextComponent.fromLegacyText(m.replace("\n", "")));
		}

		event.setCancelled(true);
		event.setCancelServer(fallbackServer.getServerInfo());
	}

	@EventHandler
	public void onSearchServer(SearchServerEvent event) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());
		boolean logged = event.getPlayer().getAccountType() == AccountType.CRACKED
				? member.getLoginConfiguration().isLogged()
				: true;

		if (event.getPlayer().getAccountType() == AccountType.CRACKED) {
			String ipAddress = event.getPlayer().getAddress().getHostString();

			if (member.getLoginConfiguration()
					.getAccountType() == LoginConfiguration.AccountType.CRACKED) {

				Session session = member.getLoginConfiguration().getSession(ipAddress);

				if (session != null) {
					if (session.hasExpired()) {
						logged = false;
						member.sendMessage("§cSua sessão expirou!");
						member.getLoginConfiguration().removeSession(ipAddress);
					} else {
						logged = true;
						member.getLoginConfiguration().login(ipAddress);
						member.sendMessage("§aAutenticado automaticamente pelo servidor!");
					}
				}
			}
		}

		Entry<ProxiedServer, ServerType> entry = searchServer(event.getPlayer(), logged, true);

		ProxiedServer server = entry.getKey();

		if (entry.getValue() == ServerType.HUNGERGAMES)
			if (server == null || server.getServerInfo() == null) {
				server = searchServer(event.getPlayer(), logged, false).getKey();
				event.getPlayer().sendMessage("§cNenhum servidor de Comp disponível!");
			}

		if (server == null || server.getServerInfo() == null) {
			event.setCancelled(true);
			event.setCancelMessage("§cEsta sala está restrita aos membros da equipe.");

			return;
		}

		if (server.isFull()) {
			event.setCancelled(true);
			event.setCancelMessage("§cEsta sala está lotada.");
			return;
		}

		event.setServer(server.getServerInfo());
		System.out.println(CommonConst.GSON.toJson(server));
	}

	/*
	 * ServerConnectRequest
	 */

	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		BungeeMember player = (BungeeMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (player == null)
			return;

		ProxiedServer server = manager.getServer(event.getTarget().getName());

		if (server.getServerType() == ServerType.LOGIN) {
			if (player.getLoginConfiguration()
					.getAccountType() == LoginConfiguration.AccountType.ORIGINAL
					&& !player.hasGroupPermission(Group.MODPLUS)) {

				ProxiedServer proxiedServer = manager.getBalancer(ServerType.LOBBY).next();

				if (proxiedServer == null || proxiedServer.getServerInfo() == null) {
					event.getPlayer().disconnect("§cEsta sala está restrita aos membros da equipe.");
					event.setCancelled(true);
				} else {
					event.getPlayer().connect(proxiedServer.getServerInfo());
				}
			}

			return;
		}

		if (!player.getLoginConfiguration().isLogged()) {
			if (CommonGeneral.getInstance().isLoginServer() ? server.getServerType() == ServerType.LOGIN
					: server.getServerType() == ServerType.LOBBY) {
				return;
			}

			ProxiedServer proxiedServer = manager.getBalancer(ServerType.LOGIN).next();

			if (proxiedServer == null || proxiedServer.getServerInfo() == null) {
				event.getPlayer().disconnect("§cEsta sala está restrita aos membros da equipe.");
				event.setCancelled(true);
			} else {
				event.getPlayer().connect(proxiedServer.getServerInfo());
			}
			return;
		}

		String message = "§aSucesso!";

		if (server.isFull() && !player.hasGroupPermission(Group.VIP)) {
			event.setCancelled(true);
			message = "§cEsta sala está lotada.";
		}

		if (event.isCancelled())
			if (event.getPlayer().getServer() == null || event.getPlayer().getServer().getInfo() == null)
				event.getPlayer().disconnect(message);
			else
				player.sendMessage(message);
		else
			event.setTarget(server.getServerInfo());
	}

	@EventHandler(priority = 127)
	public void onProxyPing(ProxyPingEvent event) {
		ServerPing serverPing = event.getResponse();

		String serverIp = getServerIp(event.getConnection());
		ProxiedServer server = manager.getServer(serverIp);

		serverPing.getPlayers().setMax(900);
		serverPing.getPlayers().setOnline(ProxyServer.getInstance().getOnlineCount());

		if (server == null) {
			serverPing.getPlayers()
					.setSample(new PlayerInfo[] { new PlayerInfo("§e" + CommonConst.SITE, UUID.randomUUID()) });

			String customMotd;

			try (Jedis jedis = BungeeMain.getInstance().getRedis().getPool().getResource()) {
				if (jedis.exists("custom-motd")) {
					customMotd = StringCenter.centered(jedis.get("custom-motd"));
				} else {
					customMotd = MOTD_LIST[CommonConst.RANDOM.nextInt(MOTD_LIST.length)];
				}
			}

			serverPing.setDescription(
					MOTD_HEADER + "\n" +
							customMotd.replace("&", "§")
			);

			return;
		}

		event.registerIntent(BungeeMain.getPlugin());
		server.getServerInfo().ping((realPing, throwable) -> {
			if (throwable == null) {
				serverPing.getPlayers().setMax(realPing.getPlayers().getMax());
				serverPing.getPlayers().setOnline(realPing.getPlayers().getOnline());
				serverPing.setDescription(realPing.getDescription());
			} else {
				serverPing.getPlayers().setSample(
						new PlayerInfo[] { new PlayerInfo("§e" + CommonConst.WEBSITE, UUID.randomUUID()) });

				String customMotd;

				try (Jedis jedis = BungeeMain.getInstance().getRedis().getPool().getResource()) {
					if (jedis.exists("custom-motd")) {
						customMotd = StringCenter.centered(jedis.get("custom-motd"));
					} else {
						customMotd = MOTD_LIST[CommonConst.RANDOM.nextInt(MOTD_LIST.length)];
					}
				}

				serverPing.setDescription(
						MOTD_HEADER + "\n" +
								customMotd.replace("&", "§")
				);
			}

			event.completeIntent(BungeeMain.getPlugin());
		});
	}

	private Entry<ProxiedServer, ServerType> searchServer(ProxiedPlayer player, boolean logged, boolean minigame) {
		String serverId = getServerIp(player.getPendingConnection());

		if (!logged)
			return new AbstractMap.SimpleEntry<>(
					manager.getBalancer(
							CommonGeneral.getInstance().isLoginServer() ? ServerType.LOGIN : ServerType.LOBBY).next(),
					CommonGeneral.getInstance().isLoginServer() ? ServerType.LOGIN : ServerType.LOBBY);

		if (minigame)
			if (serverId.toLowerCase().startsWith("hg"))
				return new AbstractMap.SimpleEntry<>(manager.getBalancer(ServerType.HUNGERGAMES).next(),
						ServerType.HUNGERGAMES);

		return new AbstractMap.SimpleEntry<>(
				manager.getServer(serverId) == null ? manager.getBalancer(ServerType.LOBBY).next()
						: manager.getServer(serverId),
				manager.getServer(serverId) == null ? ServerType.LOBBY : manager.getServer(serverId).getServerType());
	}

	private long lastLog = 0;

	@EventHandler
	public void competitiveServer(CompetitiveEvent event) {
		Calendar brazilianCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));

		int dayOfWeek = brazilianCalendar.get(Calendar.DAY_OF_WEEK);

		String[] daysOfWeek = {"sunday", "week", "week", "week", "week", "week", "saturday"};
		String dayName = daysOfWeek[dayOfWeek - 1];

		int
				hour = brazilianCalendar.get(Calendar.HOUR_OF_DAY),
				minute = brazilianCalendar.get(Calendar.MINUTE),
				second = brazilianCalendar.get(Calendar.SECOND);

		String
				week = "",
				eventType = null,
				skit = null;

		try {
			eventType = BungeeMain.getInstance().getConfig().getString("competitive." + dayName + "." + hour + "-" + minute + ".type");
			skit = BungeeMain.getInstance().getConfig().getString("competitive." + dayName + "." + hour + "-" + minute + ".skit");
		} catch (Exception ex) {
			throw new RuntimeException("§cErro ao carregar o evento competitivo do horário " + hour + ":" + minute + "!", ex);
		}

		if (BungeeMain.getPlugin().getConfig().contains("competitive." + dayName + "." + hour + "-" + minute) && second == 0) {
			if (eventType == null || eventType.isEmpty()) {
				if (eventType != null && eventType.isEmpty()) {
					return;
				}

				ProxiedServer server = manager.getBalancer(ServerType.HUNGERGAMES).next();

				if (server == null)
					return;

				String finalEventType = eventType;
				String finalSkit = skit;

				CompletableFuture.runAsync(() -> {
					try (Jedis jedis = BungeeMain.getPlugin().getRedis().getPool().getResource()) {
						JsonObject object = new JsonObject();

						object.addProperty("serverId", server.getServerId());
						object.addProperty("eventType", finalEventType);
						object.addProperty("skit", finalSkit);

						jedis.publish("competitive-channel", object.toString());
						jedis.sync();
					}
				});
			}
		}
	}

	private String getServerIp(PendingConnection con) {
		if (con == null || con.getVirtualHost() == null)
			return "";

		return con.getVirtualHost().getHostName().toLowerCase();
	}
}
