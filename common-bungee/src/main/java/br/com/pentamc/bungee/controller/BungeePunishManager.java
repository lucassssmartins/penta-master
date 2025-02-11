package br.com.pentamc.bungee.controller;

import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.ban.constructor.Ban;
import br.com.pentamc.common.ban.constructor.Mute;
import br.com.pentamc.common.ban.constructor.Warn;
import br.com.pentamc.common.controller.PunishManager;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.report.Report;
import br.com.pentamc.common.utils.DateUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePunishManager implements PunishManager {

	private Cache<String, Entry<UUID, Ban>> banCache;

	public BungeePunishManager() {
		banCache = CacheBuilder.newBuilder().expireAfterWrite(20L, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Entry<UUID, Ban>>() {
					@Override
					public Entry<UUID, Ban> load(String name) throws Exception {
						return null;
					}
				});
	}

	@Override
	public boolean ban(Member member, Ban ban) {
		Ban activeBan = member.getPunishmentHistory().getActiveBan();

		if (activeBan != null)
			if (activeBan.isPermanent())
				return false;

		member.getPunishmentHistory().ban(ban);
		CommonGeneral.getInstance().getMemberManager().getMembers().forEach(m -> {
			if (m.hasGroupPermission(Group.TRIAL)) {
				if (ban.isPermanent()) {
					m.sendMessage("§cO jogador " + member.getPlayerName() + " foi banido pelo " + ban.getBannedBy()
							+ " por " + ban.getReason() + "!");
				} else {
					TextComponent textComponent = new TextComponent(
							"§cO jogador " + member.getPlayerName() + " foi banido temporariamente pelo "
									+ ban.getBannedBy() + " por " + ban.getReason() + "!");

					textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent
							.fromLegacyText("§fTempo da punição: §c" + DateUtils.getTime(ban.getBanExpire()))));

					m.sendMessage(textComponent);
				}
			} else {
				m.sendMessage("");
				m.sendMessage("§cUm jogador utilizando trapaças em sua sala foi banido.");
				m.sendMessage("");
			}
		});

		if (ban.isPermanent())
			if (member.getLastIpAddress() != null) {
				banCache.put(member.getLastIpAddress(),
						new AbstractMap.SimpleEntry<UUID, Ban>(member.getUniqueId(), ban));
			}

		ban.setId(CommonGeneral.getInstance().getPunishData().getTotalBan());
		CommonGeneral.getInstance().getPunishData().addBan(ban);

		ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(member.getUniqueId());

		if (proxiedPlayer != null)
			proxiedPlayer.disconnect(TextComponent.fromLegacyText(getBanMessage(ban)));

		Report report = CommonGeneral.getInstance().getReportManager().getReport(member.getUniqueId());

		if (report != null)
			report.banPlayer();

		CommonGeneral.getInstance().getPlayerData().updateMember(member, "punishmentHistory");
		return true;
	}

	@Override
	public boolean mute(Member member, Mute mute) {
		Mute activeMute = member.getPunishmentHistory().getActiveMute();

		if (activeMute != null)
			if (activeMute.isPermanent())
				return false;

		member.getPunishmentHistory().mute(mute);

		CommonGeneral.getInstance().getMemberManager().getMembers().stream().forEach(m -> {
			if (m.hasGroupPermission(Group.TRIAL)) {

				if (mute.isPermanent()) {
					m.sendMessage("§cO jogador " + member.getPlayerName() + " foi mutado pelo " + mute.getMutedBy()
							+ " por " + mute.getReason() + "!");
				} else {
					TextComponent textComponent = new TextComponent(
							"§cO jogador " + member.getPlayerName() + " foi mutado temporariamente pelo "
									+ mute.getMutedBy() + " por " + mute.getReason() + "!");

					textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent
							.fromLegacyText("§fTempo da punição: §c" + DateUtils.getTime(mute.getMuteExpire()))));

					m.sendMessage(textComponent);
				}
			} else {
				m.sendMessage("");
				m.sendMessage("§cO jogador " + member.getPlayerName() + " foi mutado do servidor!");
				m.sendMessage("");
			}
		});

		Report report = CommonGeneral.getInstance().getReportManager().getReport(member.getUniqueId());

		if (report != null)
			report.mutePlayer();

		CommonGeneral.getInstance().getPlayerData().updateMember(member, "punishmentHistory");
		return true;
	}

	@Override
	public boolean warn(Member member, Warn warn) {

		return true;
	}

	@Override
	public boolean unban(Member member, UUID uniqueId, String userName, Ban.UnbanReason unbanReason) {
		Ban activeBan = member.getPunishmentHistory().getActiveBan();

		if (activeBan == null)
			return false;

		if (banCache.asMap().containsKey(member.getLastIpAddress()))
			banCache.invalidate(member.getLastIpAddress());

		activeBan.unban(uniqueId, userName, unbanReason);
		CommonGeneral.getInstance().getMemberManager().getMembers().stream()
				.filter(m -> m.hasGroupPermission(Group.MODPLUS)).forEach(m -> {
					m.sendMessage("");
					m.sendMessage("§cO jogador " + member.getPlayerName() + " foi desbanido pelo " + userName
							+ " do servidor!");
					m.sendMessage("");
				});
		CommonGeneral.getInstance().getPlayerData().updateMember(member, "punishmentHistory");

		return true;
	}

	@Override
	public boolean unmute(Member member, UUID uniqueId, String userName) {
		Mute activeMute = member.getPunishmentHistory().getActiveMute();

		if (activeMute == null)
			return false;

		activeMute.unmute(uniqueId, userName);
		CommonGeneral.getInstance().getMemberManager().getMembers().stream()
				.filter(m -> m.hasGroupPermission(Group.MODPLUS)).forEach(m -> {
					m.sendMessage("");
					m.sendMessage("§cO jogador " + member.getPlayerName() + " foi desmutado pelo " + userName
							+ " do servidor!");
					m.sendMessage("");
				});
		CommonGeneral.getInstance().getPlayerData().updateMember(member, "punishmentHistory");

		return true;
	}

	@Override
	public boolean isIpBanned(String ipAddress) {
		return banCache.asMap().containsKey(ipAddress);
	}

	@Override
	public String getBanMessage(Ban ban) {
		return "§cVocê está banido "
				+ (ban.isPermanent() ? "permanentemente" : "temporariamente") + "\n§cMotivo: " + ban.getCategory().getReason()
				+ (ban.isPermanent() ? "" : "\n§cExpira em: " + DateUtils.getTime(ban.getBanExpire()))
				+ "\n§f\n§cBanido injustamente? Peça appeal em " + CommonConst.DISCORD;
	}

	@Override
	public String getMuteMessage(Mute mute) {
		return " §cVocê está mutado " + (mute.isPermanent() ? "permanentemente" : "temporariamente")
				+ " do servidor!"
				+ (mute.isPermanent() ? "" : "\n §cExpira em §e" + DateUtils.getTime(mute.getMuteExpire()));
	}

}
