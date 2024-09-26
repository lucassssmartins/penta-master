
package br.com.pentamc.bukkit.command.register;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.command.BukkitCommandArgs;
import br.com.pentamc.common.account.League;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.MemberModel;
import br.com.pentamc.common.account.MemberVoid;
import br.com.pentamc.common.account.medal.Medal;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.ban.constructor.Mute;
import br.com.pentamc.common.clan.enums.ClanDisplayType;
import br.com.pentamc.common.command.CommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.common.command.CommandSender;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.profile.Profile;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.common.tag.Tag;
import br.com.pentamc.common.utils.DateUtils;
import br.com.pentamc.common.utils.string.MessageBuilder;
import br.com.pentamc.common.utils.string.NameUtils;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.menu.account.AccountInventory;
import br.com.pentamc.bukkit.menu.account.PreferencesInventory;
import br.com.pentamc.bukkit.menu.account.StatusInventory;

public class AccountCommand implements CommandClass {

	@CommandFramework.Command(name = "preferences", aliases = { "pref", "preferencias", "prefs" })
	public void preferencesCommand(BukkitCommandArgs cmdArgs) {
		if (cmdArgs.isPlayer())
			new PreferencesInventory(cmdArgs.getPlayer(), null);
	}

	@CommandFramework.Command(name = "status", aliases = { "stats"})
	public void statusCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			return;
		}

		String[] args = cmdArgs.getArgs();
		Player human = cmdArgs.getPlayer();

		if (args.length < 1) {
			Member member = CommonGeneral.getInstance().getMemberManager().getMember(human.getUniqueId());

			if (member != null) {
				new StatusInventory(human, member, StatusInventory.StatsCategory.PRINCIPAL);
			}

			return;
		}

		Player targetHuman = cmdArgs.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(targetHuman.getUniqueId());

		if (member != null) {
			new StatusInventory(human, member, StatusInventory.StatsCategory.PRINCIPAL);
		}
	}

	@CommandFramework.Command(name = "account", aliases = { "acc", "info", "perfil", "profile" }, runAsync = true)
	public void accountCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player sender = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		Member player;

		if (args.length == 0) {
			player = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());
		} else {
			UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

			if (uuid == null) {
				sender.sendMessage("§cO jogador " + args[0] + " não existe!");
				return;
			}

			player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

			if (player == null) {
				try {
					MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

					if (loaded == null) {
						sender.sendMessage("§cO jogador " + args[0] + " nunca entrou no servidor!");
						return;
					}

					player = new MemberVoid(loaded);
				} catch (Exception e) {
					e.printStackTrace();
					sender.sendMessage("§cNão foi possível pegar as informações do jogador " + args[0] + "!");
					return;
				}
			}

			if (!player.getUniqueId().equals(sender.getUniqueId()))
				if (!Member.hasGroupPermission(sender.getUniqueId(), Group.TRIAL)) {
					sender.sendMessage("§cVocê não pode ver o perfil de outros jogadores!");
					return;
				}
		}

		new AccountInventory(sender, player);
	}

	@CommandFramework.Command(name = "tag", runAsync = true)
	public void tagCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		if (!BukkitMain.getInstance().isTagControl()) {
			cmdArgs.getSender().sendMessage("§cO comando não está ativado nesse servidor!");
			return;
		}

		BukkitMember player = (BukkitMember) cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			TextComponent message = new TextComponent("§aSuas tags: ");

			int max = player.getTags().size() * 2;
			int i = max - 1;

			for (Tag t : player.getTags()) {
				if (i < max - 1) {
					message.addExtra(new TextComponent("§f, "));
					i -= 1;
				}

				message.addExtra(
						new MessageBuilder(t == Tag.MEMBRO ? "§7Membro" : t.getStrippedTag() + " ")
								.setHoverEvent(
										new HoverEvent(Action.SHOW_TEXT,
												new TextComponent[] { new TextComponent("§fExemplo: " + t.getPrefix()
														+ (t == Tag.MEMBRO ? "" : " ") + player.getPlayerName()
														+ "\n\n§aClique para selecionar!") }))
								.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
										"/tag " + t.getName()))
								.create());
				i -= 1;
			}

			player.sendMessage(message);
			return;
		}

		if (args[0].equalsIgnoreCase("chroma")) {
			if (player.hasGroupPermission(Group.ADMIN) || player.hasPermission("tag.chroma")) {
				player.setChroma(!player.isChroma());
				player.setTag(player.getTag());
				player.sendMessage(
						player.isChroma() ? "§aO modo chroma foi ativado!" : "§cO modo chroma foi desativado!");
				return;
			}
		}

		if (args[0].equalsIgnoreCase("default") || args[0].equalsIgnoreCase("normal")) {
			if (player.setTag(player.getDefaultTag()))
				player.sendMessage("§aVocê voltou para sua tag padrão!");
			return;
		}

		Tag tag = Tag.getByName(args[0]);

		if (tag == null) {
			player.sendMessage("§cA tag " + args[0] + " não existe!");
			return;
		}

		if (player.hasTag(tag)) {
			if (!player.getTag().equals(tag)) {
				if (player.setTag(tag)) {
					player.sendMessage("§aVocê alterou sua tag para "
							+ (tag == Tag.MEMBRO ? "§7§lMEMBRO" : tag.getPrefix()) + "§a.");
				}
			} else {
				player.sendMessage("§cVocê já está usando essa tag!");
			}
		} else {
			player.sendMessage("§cComando não encontrado.");
		}
	}

	@CommandFramework.Command(name = "medal", aliases = { "medals", "medalha", "medalhas" })
	public void medalCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		String[] args = cmdArgs.getArgs();
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(cmdArgs.getSender().getUniqueId());

		if (args.length == 0) {
			if (member.getMedalList().isEmpty()) {
				member.sendMessage("§cVocê pode adquirir medalhas em nossa loja, loja.pentamc.com.br");
			} else {
				TextComponent textComponent = new MessageBuilder(" §aMedalhas disponíveis: ").create();

				for (int x = 0; x < member.getMedalList().size(); x++) {
					Medal medal = member.getMedalList().get(x);

					if (medal == null || medal == Medal.NONE)
						continue;

					textComponent
							.addExtra(
									new MessageBuilder("" + medal.getChatColor() + medal.getMedalIcon())
											.setHoverEvent(new HoverEvent(Action.SHOW_TEXT,
													new ComponentBuilder(
															"" + medal.getChatColor() + medal.getMedalName()).create()))
											.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
													"/medal " + medal.name()))
											.create());

					if (x + 1 != member.getMedalList().size()) {
						textComponent.addExtra("§f, ");
					}
				}

				member.sendMessage(textComponent);
			}
			return;
		}

		Medal medal = Medal.getMedalByName(args[0]);

		if (medal == null) {
			if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("nenhuma")) {
				member.sendMessage("§aSua medalha foi removida!");
				member.setMedal(Medal.NONE);
				member.setTag(member.getTag());
				return;
			}

			member.sendMessage("§cA medalha " + args[0] + " não existe!");
			return;
		}

		if (member.getMedalList().contains(medal)) {
			member.sendMessage("§eVocê selecionou a medalha " + medal.getMedalName() + "§e.");
			member.setMedal(medal);
			member.setTag(member.getTag());
		} else
			member.sendMessage("§cVocê não possui esta medalha.");
	}

	@CommandFramework.Command(name = "clandisplaytag", runAsync = true)
	public void clandisplaytagCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		String[] args = cmdArgs.getArgs();
		BukkitMember player = (BukkitMember) cmdArgs.getSender();

		if (args.length == 0) {
			player.sendMessage(" §eUse /" + cmdArgs.getLabel() + " <"
					+ Joiner.on(':')
							.join(Arrays.asList(ClanDisplayType.values()).stream().map(cl -> cl.name().toLowerCase())
									.collect(Collectors.toList()))
					+ ">§e para mudar o estado da sua tag do clan no tab!");
			return;
		}

		ClanDisplayType clanDisplayType = null;

		try {
			clanDisplayType = ClanDisplayType.valueOf(args[0].toUpperCase());
		} catch (Exception ex) {
			player.sendMessage("§cO estado " + args[0] + " não foi encontrado. Tente: "
					+ Joiner.on(',').join(Arrays.asList(ClanDisplayType.values()).stream()
							.map(cl -> cl.name().toLowerCase()).collect(Collectors.toList())));
			return;
		}

		player.getAccountConfiguration().setClanDisplayType(clanDisplayType);
		player.sendMessage("§aO estado da sua tag de clan foi alterado para "
				+ NameUtils.formatString(clanDisplayType.name()) + "!");
		player.setTag(player.getTag());
	}

	@CommandFramework.Command(name = "rank", aliases = { "liga", "ranking" })
	public void rankCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		StatusType statusType = CommonGeneral.getInstance().getServerType() == ServerType.ONEXONE ? StatusType.SHADOW : StatusType.HG;

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		player.sendMessage("");
		player.sendMessage("§aSeu rank atual é " + player.getLeague(statusType).getColor() + player.getLeague(statusType).getSymbol() + " "
				+ player.getLeague(statusType).getName());
		player.sendMessage("§aSeu xp §b" + player.getXp(statusType));

		if (player.getLeague(statusType) == League.values()[League.values().length - 1]) {
			player.sendMessage("");
			player.sendMessage("§aVocê está no maior rank do servidor");
			player.sendMessage("§aContinue ganhando XP para ficar no topo do ranking");
		} else {
			player.sendMessage("");
			player.sendMessage("§aPróximo rank §e" + player.getLeague(statusType).getNextLeague().getColor()
					+ player.getLeague(statusType).getNextLeague().getSymbol() + " "
					+ player.getLeague(statusType).getNextLeague().getName());
			player.sendMessage(
					"§aXP necessário para o próximo rank §b" + (player.getLeague(statusType).getNextLeague().getMaxXp() - player.getXp(statusType)));
		}
	}

	@CommandFramework.Command(name = "ranklist", aliases = { "leaguelist", "ligalist", "ligas", "leagues", "ranks", "rankings" })
	public void leagueListCommand(BukkitCommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		TextComponent component = new TextComponent("§aLista de ligas: ");

		for (League league : League.values()) {
			TextComponent textComponent = new TextComponent(league.getColor() + league.getSymbol());

			textComponent.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(league.getColor() + league.getName()).create()));
			component.addExtra(textComponent);

			if (league != League.values()[League.values().length - 1]) {
				component.addExtra("§f, ");
			} else {
				component.addExtra("§f.");
			}
		}

		player.spigot().sendMessage(component);
	}

	@CommandFramework.Command(name = "money", aliases = { "coins" }, runAsync = true)
	public void moneyCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Member member = (Member) cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			member.sendMessage("§aVocê possui " + member.getMoney() + " coins!");
			return;
		}

		switch (args[0].toLowerCase()) {
		case "doar":
		case "give":
			if (args.length <= 2) {
				member.sendMessage("§cUso /" + cmdArgs.getLabel()
						+ " give <player> <money> para enviar money para algum jogador!");
			} else {
				UUID uuid = CommonGeneral.getInstance().getUuid(args[1]);

				if (uuid == null) {
					member.sendMessage("§cO jogador " + args[1] + " não existe!");
					return;
				}

				Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

				if (player == null) {
					try {
						MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

						if (loaded == null) {
							member.sendMessage("§cO jogador " + args[1] + " nunca entrou no servidor!");
							return;
						}

						player = new MemberVoid(loaded);
					} catch (Exception e) {
						e.printStackTrace();
						member.sendMessage("§cNão foi possível pegar as informações do jogador " + args[1] + "!");
						return;
					}
				}

				Integer money = null;

				try {
					money = Integer.valueOf(args[2]);
				} catch (NumberFormatException exception) {
					member.sendMessage("§cValor inválido!");
					return;
				}

				if (money <= 100) {
					member.sendMessage("§cVocê só pode enviar no minímo 100 coins!");
					return;
				}

				if (money > member.getMoney()) {
					member.sendMessage("§cVocê não possui " + money + " coins!");
					return;
				}

				player.addMoney(money);
				player.sendMessage("§aVocê recebeu " + money + " coins de " + member.getName() + "!");

				member.sendMessage("§aVocê deu " + money + " para o " + player.getName() + "!");
				member.removeMoney(money);
			}

			break;
		default:
			member.sendMessage("§aVocê possui " + member.getMoney() + " coins!");
			break;
		}
	}

	@CommandFramework.Command(name = "reply", usage = "/<command> <message>", aliases = { "r" })
	public void replyCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage(" §cVocê precisa ser um jogador para executar este comando;");
			return;
		}

		BukkitMember sender = (BukkitMember) cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §eVocê deve utilizar §e/" + cmdArgs.getLabel()
					+ " <mensagem>§e, para enviar uma mensagem privada.");
			return;
		}

		if (sender.getLastTell() == null) {
			sender.sendMessage(" §cVocê não tem tell para responder.");
			return;
		}

		Mute mute = sender.getPunishmentHistory().getActiveMute();

		if (mute != null) {
			sender.sendMessage("§4§l> §fVocê está mutado "
					+ (mute.isPermanent() ? "permanentemente" : "temporariamente") + " do servidor!"
					+ (mute.isPermanent() ? "" : "\n §4§l> §fExpira em §e" + DateUtils.getTime(mute.getMuteExpire())));
			return;
		}

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getLastTell().getUniqueId());

		if (member == null) {
			sender.sendMessage(" §cO jogador " + sender.getLastTell().getPlayerName() + "§c está offline.");
			sender.setLastTell(null);
			return;
		}

		if (!Member.getMember(sender.getUniqueId()).getAccountConfiguration().isTellEnabled()) {
			sender.sendMessage(" §cSeu tell está desativado.");
			return;
		}

		if (!member.getAccountConfiguration().isTellEnabled())
			if (!sender.hasGroupPermission(Group.TRIAL)) {
				sender.sendMessage("§cO tell desse jogador está desativado.");
				return;
			}

		String message = "";

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < args.length; i++)
			sb.append(args[i]).append(" ");

		message = sb.toString();

		if (!member.getAccountConfiguration().isTellEnabled())
			sender.setLastTell(null);

		sender.sendMessage("§7[§e" + cmdArgs.getPlayer().getName() + " §7» §e"
				+ (member.isUsingFake() ? member.getFakeName() : member.getPlayerName()) + "§7] §f" + message);
		member.sendMessage("§7[§e" + cmdArgs.getPlayer().getName() + " §7» §e"
				+ (member.isUsingFake() ? member.getFakeName() : member.getPlayerName()) + "§7] §f" + message);
	}

	@CommandFramework.Command(name = "givexp", usage = "/<command> <message>", groupToUse = Group.ADMIN)
	public void givexpCommand(BukkitCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length <= 1) {
			sender.sendMessage("§cComando não encontrado.");
			return;
		}

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(args[0]);

		if (member == null) {
			sender.sendMessage("§cO jogador não está online!");
			return;
		}

		try {
			StatusType statusType = CommonGeneral.getInstance().getServerType() == ServerType.ONEXONE ? StatusType.SHADOW : StatusType.HG;

			member.addXp(statusType, Integer.valueOf(args[1]));
			sender.sendMessage(
					"§aO jogador " + member.getPlayerName() + " recebeu " + Integer.valueOf(args[1]) + "xp!");
		} catch (Exception ex) {
			sender.sendMessage("§cFormato de xp inválido!");
		}
	}

	@CommandFramework.Command(name = "removexp", usage = "/<command> <message>", groupToUse = Group.ADMIN)
	public void removexpCommand(BukkitCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length <= 1) {
			sender.sendMessage("§cComando não encontrado.");
			return;
		}

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(args[0]);

		if (member == null) {
			sender.sendMessage("§cO jogador não está online.");
			return;
		}

		try {
			StatusType statusType = CommonGeneral.getInstance().getServerType() == ServerType.ONEXONE ? StatusType.SHADOW : StatusType.HG;

			member.removeXp(statusType, Integer.valueOf(args[1]));
			sender.sendMessage(
					"§cO jogador " + member.getPlayerName() + " recebeu -" + Integer.valueOf(args[1]) + "xp!");
		} catch (Exception ex) {
			sender.sendMessage("§cFormato de xp inválido!");
		}
	}

	@CommandFramework.Command(name = "tell", usage = "/<command> <message>", aliases = { "msg" })
	public void tellCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage(" §cVocê precisa ser um §ajogador §fpara executar este comando.");
			return;
		}

		BukkitMember sender = (BukkitMember) cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		Mute mute = sender.getPunishmentHistory().getActiveMute();

		if (mute != null) {
			sender.sendMessage("§cVocê está mutado "
					+ (mute.isPermanent() ? "permanentemente" : "temporariamente") + " do servidor!"
					+ (mute.isPermanent() ? "" : "\n §cExpira em §e" + DateUtils.getTime(mute.getMuteExpire())));
			return;
		}

		if (args.length == 0) {
			sender.sendMessage(" §eVocê deve utilizar /" + cmdArgs.getLabel()
					+ " <mensagem>§e, para enviar uma mensagem privada.");
			return;
		}

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("on")) {
				Member member = CommonGeneral.getInstance().getMemberManager()
						.getMember(cmdArgs.getPlayer().getUniqueId());

				if (member.getAccountConfiguration().isTellEnabled()) {
					sender.sendMessage(" §cSeu tell já está ativado.");
					return;
				}

				member.getAccountConfiguration().setTellEnabled(!member.getAccountConfiguration().isTellEnabled());
				sender.sendMessage(" §aVocê ativou o seu tell.");
			} else if (args[0].equalsIgnoreCase("off")) {
				Member member = CommonGeneral.getInstance().getMemberManager()
						.getMember(cmdArgs.getPlayer().getUniqueId());

				if (!member.getAccountConfiguration().isTellEnabled()) {
					sender.sendMessage(" §cSeu tell já está desativado.");
					return;
				}

				member.getAccountConfiguration().setTellEnabled(!member.getAccountConfiguration().isTellEnabled());
				sender.sendMessage(" §aVocê desativou o seu tell.");
			} else {
				sender.sendMessage(" §eVocê deve utilizar /" + cmdArgs.getLabel()
						+ " <mensagem>§e, para enviar uma mensagem privada.");
			}

			return;
		}

		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMemberByFake(args[0]);

		if (member == null) {
			member = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(args[0]);

			if (member != null && member.isUsingFake()
					&& !Member.hasGroupPermission(sender.getUniqueId(), Group.TRIAL)) {
				sender.sendMessage(" §cO jogador " + args[0] + "§cestá offline.");
				return;
			}
		}

		if (member == null) {
			sender.sendMessage(" §cO jogador " + args[0] + "§c está offline.");
			return;
		}

		if (!Member.getMember(sender.getUniqueId()).getAccountConfiguration().isTellEnabled()) {
			sender.sendMessage(" §cSeu tell está desativado.");
			return;
		}

		if (!member.getAccountConfiguration().isTellEnabled())
			if (!sender.hasGroupPermission(Group.TRIAL)) {
				sender.sendMessage("§cO tell desse jogador está desativado.");
				return;
			}

		boolean fake = false;

		if (member.isUsingFake())
			fake = true;

		String message = "";

		StringBuilder sb = new StringBuilder();

		for (int i = 1; i < args.length; i++)
			sb.append(args[i]).append(" ");

		message = sb.toString();

		sender.sendMessage("§7[§e" + cmdArgs.getPlayer().getName() + " §7» §e"
				+ (fake ? member.getFakeName() : member.getPlayerName()) + "§7] §f" + message);
		member.sendMessage("§7[§e" + cmdArgs.getPlayer().getName() + " §7» §e"
				+ (fake ? member.getFakeName() : member.getPlayerName()) + "§7] §f" + message);
		member.setLastTell(Profile.fromMember(sender));
	}

}
