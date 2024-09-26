package br.com.pentamc.bungee.command.register;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bungee.BungeeMain;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.command.CommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.common.command.CommandSender;
import br.com.pentamc.common.controller.GiftcodeController;
import br.com.pentamc.common.giftcode.types.KitGiftcode;
import br.com.pentamc.common.giftcode.types.RankGiftcode;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.permission.RankType;
import br.com.pentamc.common.utils.DateUtils;
import br.com.pentamc.common.utils.string.MessageBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class GifcodeCommand implements CommandClass {

	private static final String CHARS_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz12345689";

	@CommandFramework.Command(name = "giftcode", aliases = { "resgatar", "codigo" })
	public void giftcodeCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §cUse §c/" + cmdArgs.getLabel() + " <código>§c para resgatar um código.");

			if (Member.hasGroupPermission(sender.getUniqueId(), Group.ADMIN)) {
				sender.sendMessage(" §cUse §c/" + cmdArgs.getLabel()
						+ " criar rank <rank> <tempo>§c para criar um código de rank.");
				sender.sendMessage(" §cUse §c/" + cmdArgs.getLabel()
						+ " criar kit <kit> <tempo>§c para criar um código de kit.");
				return;
			}
			return;
		}

		if (args[0].equalsIgnoreCase("criar")) {
			if (!cmdArgs.isPlayer() || Member.hasGroupPermission(sender.getUniqueId(), Group.ADMIN)) {
				String code = "";

				do {
					StringBuilder stringBuilder = new StringBuilder();

					for (int x = 1; x <= 15; x++) {
						stringBuilder.append(CHARS_STRING.charAt(CommonConst.RANDOM.nextInt(CHARS_STRING.length())));
						if (x % 5 == 0 && x != 15)
							stringBuilder.append('-');
					}

					code = stringBuilder.toString().trim();
				} while (BungeeMain.getInstance().getGiftcodeController().containsKey(code));

				if (args.length >= 2) {
					if (args[1].equalsIgnoreCase("rank")) {
						if (args.length >= 4) {
							RankType rankType = null;

							try {
								rankType = RankType.valueOf(args[2].toUpperCase());
							} catch (Exception ex) {
								sender.sendMessage("§cO rank " + args[2] + " não existe.");
								return;
							}

							long time = DateUtils.getTime(args[3]);

							if (BungeeMain.getInstance().getGiftcodeController().registerGiftcode(code,
									new RankGiftcode(code, rankType, time))) {
								sender.sendMessage(new MessageBuilder("§aO código " + code + " foi gerado!")
										.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												new ComponentBuilder("§aClique para copiar!").create()))
										.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, code))
										.create());
								CommonGeneral.getInstance().getMemberManager()
										.broadcast("§7O " + sender.getName() + " criou um código de Rank "
												+ rankType.name() + " com a duração de " + DateUtils.getTime(time)
												+ "!", Group.MODPLUS);
							}
						} else
							sender.sendMessage(" §cUse §c/" + cmdArgs.getLabel()
									+ " criar rank <rank> <tempo>§c para criar um código de rank.");
					} else if (args[1].equalsIgnoreCase("kit")) {
						if (args.length >= 3) {
							long time = args.length >= 4 ? DateUtils.getTime(args[3]) : -1l;

							if (BungeeMain.getInstance().getGiftcodeController().registerGiftcode(code,
									new KitGiftcode(code, args[2], time))) {
								sender.sendMessage(new MessageBuilder("§aO código " + code + " foi gerado.")
										.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												new ComponentBuilder("§aClique para copiar!").create()))
										.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, code))
										.create());
								CommonGeneral.getInstance().getMemberManager()
										.broadcast(
												"§7O " + sender.getName() + " criou um código de Kit " + args[2]
														+ " com a duração de " + DateUtils.getTime(time) + ".",
												Group.MODPLUS);
							}
						} else
							sender.sendMessage(" §cUse §c/" + cmdArgs.getLabel()
									+ " criar kit <kit> <tempo>§c para criar um código de kit.");
					}
				}
			}
			return;
		}

		String code = args[0];

		if (args.length >= 2) {
			if (args[1].equalsIgnoreCase("deletar")) {
				if (!cmdArgs.isPlayer() || Member.hasGroupPermission(sender.getUniqueId(), Group.ADMIN)) {
					if (BungeeMain.getInstance().getGiftcodeController().deleteGiftcode(code)) {
						sender.sendMessage("§aO código " + code + " foi deletado com sucesso!");
						CommonGeneral.getInstance().getMemberManager().broadcast(
								"§7O " + sender.getName() + " deletou o código " + code + "!", Group.MODPLUS);
					} else
						sender.sendMessage("§cO código " + code + " não existe.");
				}
				return;
			}
		}

		if (!cmdArgs.isPlayer())
			return;

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());
		GiftcodeController.ExecutionResponse response = BungeeMain.getInstance().getGiftcodeController().execute(member, code);

		switch (response) {
		case ALREADY_USED: {
			member.sendMessage("§cO código " + code + " já foi utilizado.");
			break;
		}
		case SUCCESS: {
			CommonGeneral.getInstance().getMemberManager()
					.broadcast("§7O " + sender.getName() + " resgatou o código " + code + "!", Group.TRIAL);
			break;
		}
		case NOT_FOUND: {
			member.sendMessage("§cO código " + code + " não foi encontrado!");
			break;
		}
		}
		return;
	}

}
