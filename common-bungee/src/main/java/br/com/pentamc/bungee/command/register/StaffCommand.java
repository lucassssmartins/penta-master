package br.com.pentamc.bungee.command.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bungee.BungeeMain;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.command.CommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.common.command.CommandSender;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.tag.Tag;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffCommand implements CommandClass {

	@CommandFramework.Command(name = "glist", groupToUse = Group.TRIAL, usage = "/<command>", aliases = { "onlines", "online" })
	public void glistCommand(CommandArgs cmdArgs) {
		cmdArgs.getSender().sendMessage("§aTemos " + ProxyServer.getInstance().getOnlineCount() + " jogadores online!");
		cmdArgs.getSender().sendMessage("");
		ProxyServer.getInstance().getServers().values().stream()
				.sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).forEach(info -> cmdArgs.getSender()
						.sendMessage("§9" + info.getName() + " §7-§a " + info.getPlayers().size() + " online"));
		cmdArgs.getSender().sendMessage("");
	}

	@CommandFramework.Command(name = "broadcast", groupToUse = Group.MODPLUS, usage = "/<command> <mesage>", aliases = { "bc", "aviso",
			"alert" })
	public void broadcastCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("§aUso /" + cmdArgs.getLabel() + " <mensagem> para mandar uma mensagem para todos.");
			return;
		}

		String msg = "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++)
			sb.append(args[i]).append(" ");
		msg = sb.toString();

		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(" "));
		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§c§lAVISO §f" + msg.replace("&", "§")));
		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(" "));
		CommonGeneral.getInstance().getMemberManager()
				.broadcast("§7O " + sender.getName() + " enviou uma mensagem global!", Group.TRIAL);
	}

	@CommandFramework.Command(name = "maintence", aliases = { "manutencao" }, groupToUse = Group.ADMIN)
	public void maintenceCommand(CommandArgs cmdArgs) {
		CommandSender player = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		Group group = BungeeMain.getInstance().getMaintenance().get(true);

		if (args.length < 1) {
			if (group != null) {
				BungeeMain.getInstance().getMaintenance().clear();

				ProxyServer.getInstance().broadcast("§cO servidor saiu do modo manutenção.");
			}

			player.sendMessage("§cUse: /" + cmdArgs.getLabel() + " <min-group-entry>");
			return;
		}

		group = Arrays.stream(Group.values()).filter(groups -> groups.name().equalsIgnoreCase(args[0])).findFirst().orElse(null);

		if (group == null) {
			player.sendMessage("§cO grupo inserido não existe!");
			return;
		}

		BungeeMain.getInstance().getMaintenance().clear();
		BungeeMain.getInstance().getMaintenance().put(true, group);

		ProxyServer.getInstance().broadcast("§aO servidor entrou em modo manutenção.");
	}

	@CommandFramework.Command(name = "staffchat", groupToUse = Group.TRIAL, usage = "/<command>", aliases = { "sc" })
	public void staffchatCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		CommandSender sender = cmdArgs.getSender();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());

		if (cmdArgs.getArgs().length == 1) {
			if (cmdArgs.getArgs()[0].equalsIgnoreCase("on")) {
				if (member.getAccountConfiguration().isSeeingStaffchat()) {
					member.sendMessage("§cO staffchat já está ativado!");
				} else {
					member.getAccountConfiguration().setSeeingStaffchat(true);
					member.sendMessage("§aVocê agora vê o staffchat!");
				}

				return;
			} else if (cmdArgs.getArgs()[0].equalsIgnoreCase("off")) {
				if (!member.getAccountConfiguration().isSeeingStaffchat()) {
					member.sendMessage("§cO staffchat já está desativado!");
				} else {
					member.getAccountConfiguration().setSeeingStaffchat(false);
					member.sendMessage("§cVocê agora não vê mais o staffchat!");
				}

				return;
			}
		}

		member.getAccountConfiguration().setStaffChatEnabled(!member.getAccountConfiguration().isStaffChatEnabled());
		sender.sendMessage(
				" §aVocê " + (member.getAccountConfiguration().isStaffChatEnabled() ? "entrou no" : "saiu do")
						+ " §astaff-chat§a.");

		if (member.getAccountConfiguration().isStaffChatEnabled())
			if (!member.getAccountConfiguration().isSeeingStaffchat())
				member.getAccountConfiguration().setSeeingStaffchat(true);
	}

	@CommandFramework.Command(name = "stafflog", groupToUse = Group.TRIAL, usage = "/<command>", aliases = { "sl" })
	public void stafflogCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		if (cmdArgs.getArgs().length == 0) {
			member.getAccountConfiguration().setSeeingStafflog(!member.getAccountConfiguration().isSeeingStafflog());
			member.sendMessage(
					" §aAgora você " + (member.getAccountConfiguration().isSeeingStafflog() ? "vê" : "não vê mais")
							+ " §aas logs dos staffs§a.");
			return;
		}
	}

	@CommandFramework.Command(name = "fakelist", runAsync = true, groupToUse = Group.TRIAL, usage = "/<command> <player> <server>")
	public void fakelistCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();

		sender.sendMessage("§aJogadores usando fake: ");
		sender.sendMessage(" ");

		CommonGeneral.getInstance().getMemberManager().getMembers().stream().filter(member -> member.isUsingFake())
				.forEach(member -> sender.sendMessage(" §fO jogador §a" + member.getPlayerName()
						+ "§f está usando o fake §e" + member.getFakeName() + "§f."));
	}

	@CommandFramework.Command(name = "stafflist", runAsync = true, groupToUse = Group.MOD, usage = "/<command> <player> <server>")
	public void stafflistCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();

		sender.sendMessage("§aStaff online: ");
		sender.sendMessage(" ");

		CommonGeneral.getInstance().getMemberManager().getMembers().stream()
				.filter(member -> member.hasGroupPermission(Group.TRIAL))
				.sorted((o1, o2) -> o1.getServerGroup().compareTo(o2.getServerGroup()))
				.forEach(member -> sender.sendMessage("§a" + member.getPlayerName() + " §8- §f"
						+ Tag.valueOf(member.getServerGroup().name()).getPrefix()));
	}

	@CommandFramework.Command(name = "find", groupToUse = Group.TRIAL, usage = "/<command> <player>")
	public void findCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §cUse /find <player> para localizar algum jogador.");
			return;
		}

		ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

		if (target == null) {
			sender.sendMessage(" §cO jogador §c" + args[0] + "§c está offline.");
			return;
		}

		TextComponent txt = new TextComponent(" §aO jogador §a" + target.getName() + " §aestá no servidor §a"
				+ target.getServer().getInfo().getName().toUpperCase() + "§a.");
		TextComponent text = new TextComponent(" §7(Clique aqui para teletransportar-se)");

		text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + target.getName()));
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aClique aqui.")));

		sender.sendMessage(new BaseComponent[] { txt, text });
	}

	@CommandFramework.Completer(name = "find", aliases = { "report" })
	public List<String> serverCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 1) {
			List<String> playerList = new ArrayList<>();

			try {
				for (String playerName : ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName)
						.collect(Collectors.toList()))
					if (cmdArgs.getArgs()[0].toLowerCase().startsWith(playerName))
						playerList.add(cmdArgs.getArgs()[0]);
			} catch (Exception ex) {
				for (String playerName : ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName)
						.collect(Collectors.toList()))
					playerList.add(playerName);
			}

			return playerList;
		}

		return new ArrayList<>();
	}

}
