package br.com.pentamc.bukkit.command.register;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.command.CommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.common.command.CommandSender;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.server.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.server.chat.ChatState;
import br.com.pentamc.bukkit.api.vanish.AdminMode;
import br.com.pentamc.bukkit.api.vanish.VanishAPI;
import br.com.pentamc.bukkit.bukkit.BukkitMember;

import java.util.List;
import java.util.stream.Collectors;

public class StaffCommand implements CommandClass {

	@CommandFramework.Command(name = "say", groupToUse = Group.TRIAL, usage = "/<command> <mesage>")
	public void broadcastCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("§cUso /" + cmdArgs.getLabel() + " <mensagem> para mandar uma mensagem para todos.");
			return;
		}

		String msg = "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++)
			sb.append(args[i]).append(" ");
		msg = sb.toString();

		Bukkit.broadcastMessage("§b§lPENTA §7§l» §f" + msg.replace("&", "§"));
	}

	@CommandFramework.Completer(name = "setspawn")
	public List<String> setspawnCompleter(CommandArgs cmdArgs) {
		return BukkitMain.getInstance().getLocations().keySet().stream().map(String::toLowerCase)
				.filter(string -> cmdArgs.getArgs().length == 0 ? true : string.startsWith(cmdArgs.getArgs()[cmdArgs.getArgs().length - 1].toLowerCase()))
				.collect(Collectors.toList());
	}

	@CommandFramework.Command(name = "setspawn", groupToUse = Group.TRIAL)
	public void setspawnCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player p = ((BukkitMember) cmdArgs.getSender()).getPlayer();

		if (CommonGeneral.getInstance().getServerType() == ServerType.HUNGERGAMES)
			if (!Member.hasGroupPermission(p.getUniqueId(), Group.MODPLUS)
					&& !Member.isGroup(p.getUniqueId(), Group.TRIAL)) {
				p.sendMessage(" §cVocê não tem permissão para executar esse comando.");
				return;
			} else if (!Member.hasGroupPermission(p.getUniqueId(), Group.ADMIN)
					&& !Member.isGroup(p.getUniqueId(), Group.TRIAL)) {
				p.sendMessage(" §cVocê não tem permissão para executar esse comando.");
				return;
			}

		String[] a = cmdArgs.getArgs();

		if (a.length == 0) {
			p.sendMessage(" §eUse /setwarp <warpName>§e para setar uma warp.");
			return;
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int x = 0; x < a.length; x++) {
			stringBuilder.append(a[x]).append(" ");
		}

		String configName = a[0];
		BukkitMain.getInstance().registerLocationInConfig(p.getLocation(), configName);
		p.sendMessage(" §aVocê setou a warp §a" + configName + "§a!");
	}

	@CommandFramework.Command(name = "admin", aliases = { "adm", "vanish", "v" }, groupToUse = Group.TRIAL)
	public void admin(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player p = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		String[] args = cmdArgs.getArgs();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		if (args.length != 0) {
			if (args[0].equalsIgnoreCase("items")) {
				member.getAccountConfiguration().setAdminItems(!member.getAccountConfiguration().isAdminItems());
				member.sendMessage("§eOs items do admin foram "
						+ (member.getAccountConfiguration().isAdminItems() ? "§aativado" : "§cdesativado§e!"));

				if (AdminMode.getInstance().isAdmin(p)) {
					AdminMode.getInstance().setPlayer(p, member);
					new BukkitRunnable() {

						@Override
						public void run() {
							AdminMode.getInstance().setAdmin(p, member);
						}
					}.runTaskLater(BukkitMain.getInstance(), 5l);
				}

				return;
			} else if (args[0].equalsIgnoreCase("join")) {
				member.getAccountConfiguration().setAdminOnJoin(!member.getAccountConfiguration().isAdminOnJoin());
				member.sendMessage("§9§l> §fO entrar no admin ao mudar de servidor foi "
						+ (member.getAccountConfiguration().isAdminOnJoin() ? "§aativado" : "§cdesativado§f!"));

				if (AdminMode.getInstance().isAdmin(p)) {
					AdminMode.getInstance().setPlayer(p, member);
					new BukkitRunnable() {

						@Override
						public void run() {
							AdminMode.getInstance().setAdmin(p, member);
						}

					}.runTaskLater(BukkitMain.getInstance(), 5l);
				}

				return;
			}
		}

		if (AdminMode.getInstance().isAdmin(p)) {
			AdminMode.getInstance().setPlayer(p, member);
		} else {
			AdminMode.getInstance().setAdmin(p, member);
		}
	}

	@CommandFramework.Command(name = "updatevanish", groupToUse = Group.TRIAL)
	public void updatevanish(CommandArgs args) {
		if (args.isPlayer()) {
			VanishAPI.getInstance().updateVanishToPlayer(((BukkitMember) args.getSender()).getPlayer());
		}
	}

	@CommandFramework.Command(name = "visible", aliases = { "vis", "visivel" }, groupToUse = Group.TRIAL)
	public void visibleCommand(CommandArgs args) {
		if (!args.isPlayer())
			return;

		Player p = ((BukkitMember) args.getSender()).getPlayer();
		VanishAPI.getInstance().showPlayer(p);
		p.sendMessage("\n §a* §fVocê está visível para todos os jogadores!\n§f");
	}

	@CommandFramework.Command(name = "invisible", aliases = { "invis", "invisivel" }, groupToUse = Group.TRIAL)
	public void invisibleCommand(CommandArgs args) {
		if (!args.isPlayer()) {
			return;
		}

		Player p = ((BukkitMember) args.getSender()).getPlayer();
		Member bP = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());
		Group group = Group.MEMBRO;

		if (args.getArgs().length > 0) {
			try {
				group = Group.valueOf(args.getArgs()[0].toUpperCase());
			} catch (Exception e) {
				p.sendMessage(" §cO grupo §c" + group.name() + "§c não existe.");
				return;
			}
			if (group.ordinal() >= bP.getServerGroup().ordinal()) {
				p.sendMessage(" §cO grupo §c" + group.name() + "§c não está disponível para você.");
				return;
			}
		} else
			group = VanishAPI.getInstance().hidePlayer(p);

		VanishAPI.getInstance().setPlayerVanishToGroup(p, group);
		p.sendMessage("\n §dVocê está invisivel para §d" + group.toString() + " e inferiores§d!\n§d");
	}

	@CommandFramework.Command(name = "inventorysee", aliases = { "invsee", "inv" }, groupToUse = Group.TRIAL)
	public void inventorysee(CommandArgs args) {
		if (!args.isPlayer())
			return;

		Player p = ((BukkitMember) args.getSender()).getPlayer();

		if (args.getArgs().length == 0) {
			p.sendMessage(" §eUse §e/" + args.getLabel() + " <player>§e para abrir o inventário de alguém!");
			return;
		}

		Player t = Bukkit.getPlayer(args.getArgs()[0]);

		if (t == null) {
			p.sendMessage(" §cO jogador §c\"" + args.getArgs()[0] + "\"§c não existe!");
			return;
		}

		p.sendMessage(" §aVocê abriu o inventário de §a" + t.getName() + "§a.");
		p.openInventory(t.getInventory());
		staffLog("O §a" + p.getName() + " §aabriu o inventário de §a" + t.getName() + "§a!", Group.MODPLUS);
	}

	@CommandFramework.Command(name = "chat", groupToUse = Group.MOD)
	public void chatCommand(CommandArgs args) {
		CommandSender sender = args.getSender();

		if (args.getArgs().length == 0) {
			sender.sendMessage(" §eUse /chat <on:off> para ativar ou desativar o chat.");
			return;
		}

		if (args.getArgs()[0].equalsIgnoreCase("on")) {
			if (BukkitMain.getInstance().getServerConfig().getChatState().isEnabled()) {
				sender.sendMessage(" §aO chat já está ativado!");
				return;
			}

			BukkitMain.getInstance().getServerConfig().setChatState(ChatState.ENABLED);
			sender.sendMessage(" §aO chat está disponível para todos!");
			CommonGeneral.getInstance().getMemberManager().broadcast("§7" + sender.getName() + " ativou o chat!",
					Group.TRIAL);
		} else if (args.getArgs()[0].equalsIgnoreCase("off")) {
			if (!BukkitMain.getInstance().getServerConfig().getChatState().isEnabled()) {
				sender.sendMessage(" §cO chat já está desativado!");
				return;
			}

			BukkitMain.getInstance().getServerConfig()
					.setChatState(CommonGeneral.getInstance().getServerType() == ServerType.LOBBY ? ChatState.YOUTUBER
							: ChatState.STAFF);
			sender.sendMessage(" §aO chat agora está disponível somente para §a"
					+ BukkitMain.getInstance().getServerConfig().getChatState().name() + "§a!");
			CommonGeneral.getInstance().getMemberManager().broadcast("§7" + sender.getName() + " desativou o chat!",
					Group.TRIAL);
		} else {
			ChatState chatState = null;

			try {
				chatState = ChatState.valueOf(args.getArgs()[0].toUpperCase());
			} catch (Exception ex) {
			}

			if (chatState == null) {
				sender.sendMessage(" §eUse /chat <on:off> para ativar ou desativar o chat!");
				return;
			}

			BukkitMain.getInstance().getServerConfig().setChatState(chatState);
			sender.sendMessage(chatState == ChatState.ENABLED ? " §aO chat está disponível para todos!"
					: " §aO chat agora está disponível somente para §a"
							+ BukkitMain.getInstance().getServerConfig().getChatState().getAvailableTo() + "§a!");
			return;
		}
	}

	@CommandFramework.Command(name = "skull", groupToUse = Group.MODPLUS)
	public void eventoCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		BukkitMember sender = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(cmdArgs.getSender().getUniqueId());

		if (cmdArgs.getArgs().length == 0) {
			sender.sendMessage(" §eUse /skull <playerName> para receber a cabeça.");
			return;
		}

		sender.getPlayer().getInventory().addItem(new ItemBuilder().type(Material.SKULL_ITEM).durability(3)
				.name("§a" + cmdArgs.getArgs()[0]).skin(cmdArgs.getArgs()[0]).build());
		sender.sendMessage("§aHead of " + sender.getName());
	}

	@CommandFramework.Command(name = "clearchat", aliases = { "limparchat", "cc" }, groupToUse = Group.TRIAL)
	public void clearchat(CommandArgs args) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			for (int i = 0; i < 100; i++)
				p.sendMessage("");

			p.sendMessage("\n");
		}

		staffLog("O §a" + args.getSender().getName() + " §alimpou o chat§a!", Group.MODPLUS);
	}

}
