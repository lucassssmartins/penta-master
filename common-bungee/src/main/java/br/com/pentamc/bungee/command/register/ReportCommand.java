package br.com.pentamc.bungee.command.register;

import java.util.Arrays;
import java.util.UUID;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bungee.command.BungeeCommandArgs;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.MemberModel;
import br.com.pentamc.common.account.MemberVoid;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.common.command.CommandSender;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.report.Report;
import br.com.pentamc.common.utils.DateUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ReportCommand implements CommandClass {

	@CommandFramework.Command(name = "report", aliases = { "reportar", "rp" }, runAsync = true)
	public void report(BungeeCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());

		if (args.length >= 1) {
			if (Arrays.asList("on", "off").contains(args[0].toLowerCase())
					&& Member.hasGroupPermission(sender.getUniqueId(), Group.YOUTUBERPLUS)) {
				Member member = args.length == 1
						? CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId())
						: CommonGeneral.getInstance().getMemberManager().getMember(args[1]);

				if (member == null) {
					return;
				}

				if (args[0].equalsIgnoreCase("on")) {

					if (member.getAccountConfiguration().isReportEnabled()) {
						member.sendMessage("§cOs reports já estão ativados.");
					} else {
						member.getAccountConfiguration().setReportEnabled(true);
						member.sendMessage("§aVocê agora vê os reports.");
					}

					return;
				} else if (args[0].equalsIgnoreCase("off")) {

					if (!member.getAccountConfiguration().isReportEnabled()) {
						member.sendMessage("§cOs reports já estão desativados.");
					} else {
						member.getAccountConfiguration().setReportEnabled(false);
						member.sendMessage("§cVocê agora não vê mais os reports.");
					}

					return;
				}
			}
		}

		if (args.length <= 1) {
			sender.sendMessage(" §cUse /report <jogador> <motivo> para reportar um jogador.");
			return;
		}

		if (player.isOnCooldown("report-command")) {
			sender.sendMessage(" §cVocê precisa esperar §c"
					+ DateUtils.getTime(player.getCooldown("report-command")) + "§c para reportar novamente.");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage(" §cO jogador §c" + args[0] + "§c não existe.");
			return;
		}

		Member m = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (m == null) {
			m = CommonGeneral.getInstance().getMemberManager().getMemberByFake(args[0]);

			if (m == null) {
				try {
					MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

					if (loaded == null) {
						sender.sendMessage(" §cO jogador §c" + args[0] + "§c nunca entrou no servidor.");
						return;
					}

					m = new MemberVoid(loaded);
				} catch (Exception e) {
					e.printStackTrace();
					sender.sendMessage(" §cNão foi possível pegar as informações do jogador §c" + args[0] + "§c.");
					return;
				}
			}
		}

		if (sender.getUniqueId().equals(m.getUniqueId())) {
			sender.sendMessage(" §cVocê não pode se reportar.");
			return;
		}

		Member target = m;

		if (ProxyServer.getInstance().getPlayer(target.getUniqueId()) == null) {
			sender.sendMessage(" §cO jogador §c" + args[0] + "§c não existe.");
			return;
		}

		Report rp = CommonGeneral.getInstance().getReportManager().getReport(uuid);

		if (rp == null) {
			rp = new Report(target.getUniqueId(), target.getPlayerName());
			CommonGeneral.getInstance().getReportManager().loadReport(rp);
			CommonGeneral.getInstance().getReportData().saveReport(rp);
		}

		final Report report = rp;

		StringBuilder builder = new StringBuilder();

		for (int i = 1; i < args.length; i++) {
			String espaco = " ";

			if (i >= args.length - 1)
				espaco = "";

			builder.append(args[i] + espaco);
		}

		if (report.addReport(sender.getUniqueId(), cmdArgs.getPlayer().getName(),
				CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId()).getReputation(),
				builder.toString())) {
			sender.sendMessage(" §aVocê denunciou o jogador §a" + target.getPlayerName() + "§a por §a"
					+ builder.toString().trim() + "§a.");

			TextComponent text = new TextComponent(TextComponent.fromLegacyText("§a(Clique para se conectar)"));

			text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + report.getPlayerName()));
			text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("§aClique para se teletransportar.").create()));

			CommonGeneral.getInstance().getMemberManager().getMembers().stream()
					.filter(member -> member.hasGroupPermission(Group.TRIAL)
							&& member.getAccountConfiguration().isReportEnabled())
					.forEach(member -> {
						member.sendMessage("§c§lREPORT");
						member.sendMessage(" ");
						member.sendMessage("§fSuspeito: §c" + report.getPlayerName());
						member.sendMessage("§fReportado por: §7" + cmdArgs.getPlayer().getName());
						member.sendMessage("§fMotivo: §7" + builder.toString().trim());
						member.sendMessage("§fServidor: §a" + target.getServerId());
						member.sendMessage(" ");

						member.sendMessage(text);
					});

			player.setCooldown("report-command", System.currentTimeMillis() + 120000l);
		} else {
			sender.sendMessage(" §aVocê denunciou o jogador §a" + target.getPlayerName() + "§a por §a"
					+ builder.toString().trim() + "§a.");
		}
	}

}
