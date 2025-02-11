package br.com.pentamc.competitive.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Joiner;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.abilities.Ability;
import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.kit.Kit;
import br.com.pentamc.competitive.kit.KitType;
import br.com.pentamc.competitive.menu.kit.SelectorInventory;
import br.com.pentamc.competitive.menu.kit.SelectorInventory.OrderType;
import br.com.pentamc.competitive.utils.ServerConfig;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.common.command.CommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework.Command;
import br.com.pentamc.common.command.CommandFramework.Completer;
import br.com.pentamc.common.command.CommandSender;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.utils.string.NameUtils;

public class KitCommand implements CommandClass {

	@Command(name = "kit")
	public void kitCommand(CommandArgs cmdArgs) {


		handleKit(cmdArgs.getSender(), cmdArgs.getArgs(), cmdArgs.getLabel(), KitType.PRIMARY);
	}

	@Command(name = "givekit", groupToUse = Group.MODPLUS)
	public void giveitemKit(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("§cUso /" + cmdArgs.getLabel() + " <all:player> para dar o kit do item!");
			return;
		}

		List<Player> playerList = new ArrayList<>();

		if (args[0].equalsIgnoreCase("all")) {
			playerList = Bukkit.getOnlinePlayers().stream()
					.filter(player -> !GameGeneral.getInstance().getGamerController().getGamer(player).isNotPlaying())
					.collect(Collectors.toList());
		} else {
			Player player = Bukkit.getPlayer(args[0]);

			if (player == null) {
				sender.sendMessage("§cO jogador \"" + args[0] + "\" não existe!");
				return;
			}

			playerList.add(player);
		}

		for (Player player : playerList) {
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

			if (gamer == null)
				continue;

			for (Kit playerKit : gamer.getKitMap().values()) {
				for (Ability ability : playerKit.getAbilities()) {
					for (ItemStack item : ability.getItemList()) {
						player.getInventory().addItem(item);
					}
				}
			}
		}

		sender.sendMessage("§a" + playerList.size() + " receberam o item do kit!");
		CommonGeneral.getInstance().getMemberManager().broadcast("§7O " + sender.getName() + " deu o item do kit para "
				+ (playerList.size() > 5 ? playerList.size() + " jogadores"
						: "o jogador " + Joiner.on(", ")
								.join(playerList.stream().map(Player::getName).collect(Collectors.toList())))
				+ "!", Group.MOD);
	}

	@Command(name = "forcekit", groupToUse = Group.MODPLUS)
	public void forcekitKit(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length < 2) {
			sender.sendMessage("§cUso /" + cmdArgs.getLabel() + " <kit> <all:player> forca o kit do player!");
			return;
		}

		Kit kit = GameGeneral.getInstance().getKitController().getKit(args[0]);

		if (kit == null) {
			sender.sendMessage("§cO kit \"" + args[0] + "\" não existe!");
			return;
		}

		List<Player> playerList = new ArrayList<>();

		if (args[1].equalsIgnoreCase("all")) {
			playerList = Bukkit.getOnlinePlayers().stream()
					.filter(player -> !GameGeneral.getInstance().getGamerController().getGamer(player).isNotPlaying())
					.collect(Collectors.toList());
		} else {
			Player player = Bukkit.getPlayer(args[1]);

			if (player == null) {
				sender.sendMessage("§cO jogador \"" + args[1] + "\" não existe!");
				return;
			}

			playerList.add(player);
		}

		KitType kitType = KitType.PRIMARY;

		for (Player player : playerList) {
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

			if (gamer == null)
				continue;

			GameGeneral.getInstance().getKitController().setKit(player, kit, kitType);
		}

		sender.sendMessage("§a" + playerList.size() + " tiveram o kit alterado para o " + kit.getName() + "!");
		CommonGeneral.getInstance().getMemberManager()
				.broadcast("§7O " + sender.getName() + " forçou o kit " + NameUtils.formatString(kit.getName())
						+ (kitType == KitType.SECONDARY ? " (como secundario)" : "") + " para "
						+ (playerList.size() > 5 ? playerList.size() + " jogadores"
								: "o(s) jogador(es) " + Joiner.on(", ")
										.join(playerList.stream().map(Player::getName).collect(Collectors.toList())))
						+ "!", Group.MOD);
	}

	@Command(name = "defaultkit", groupToUse = Group.MODPLUS)
	public void defaultKit(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("§cUse /" + cmdArgs.getLabel() + " <kit:remove> forca o kit do player!");
			return;
		}

		KitType kitType = KitType.PRIMARY;

		if (args[0].equalsIgnoreCase("remove")) {
			ServerConfig.getInstance().getDefaultKit().remove(kitType);
			return;
		}

		Kit kit = GameGeneral.getInstance().getKitController().getKit(args[0]);

		if (kit == null) {
			sender.sendMessage("§cO kit \"" + args[0] + "\" não existe!");
			return;
		}

		ServerConfig.getInstance().getDefaultKit().put(kitType, kit);
		sender.sendMessage("§aVocê alterou o kit " + (kitType == KitType.PRIMARY ? "1" : "2") + " default para"
				+ NameUtils.formatString(kit.getName()) + "!");
		CommonGeneral.getInstance().getMemberManager()
				.broadcast("§7O " + sender.getName() + " alterou o kit " + (kitType == KitType.PRIMARY ? "1" : "2")
						+ " default para" + NameUtils.formatString(kit.getName()) + "!");
	}

	@Command(name = "togglekit", groupToUse = Group.MODPLUS)
	public void togglekitKit(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length < 2) {
			sender.sendMessage("§cUso /" + cmdArgs.getLabel() + " <kit> <on:off> para ativar/desativar um kit!");
			return;
		}

		List<Kit> kitList = new ArrayList<>();

		if (args[0].equalsIgnoreCase("all")) {
			kitList.addAll(GameGeneral.getInstance().getKitController().getAllKits());
		} else {
			if (args[0].contains(",")) {
				for (String kitName : args[0].split(",")) {
					Kit kit = GameGeneral.getInstance().getKitController().getKit(kitName);

					if (kit == null) {
						sender.sendMessage("§cO kit \"" + args[0] + "\" não existe!");
						return;
					}

					kitList.add(kit);
				}
			} else {
				Kit kit = GameGeneral.getInstance().getKitController().getKit(args[0]);

				if (kit == null) {
					sender.sendMessage("§cO kit \"" + args[0] + "\" não existe!");
					return;
				}

				kitList.add(kit);
			}
		}

		if (!Arrays.asList("on", "off").contains(args[1].toLowerCase())) {
			sender.sendMessage("§cUso /" + cmdArgs.getLabel() + " " + args[0]
					+ " <on:off> para ativar ou desativar algum kit!");
			return;
		}

		boolean enabled = args[1].equalsIgnoreCase("on");

		KitType kitType = KitType.PRIMARY;

		if (!args[0].equalsIgnoreCase("all")) {
			if (ServerConfig.getInstance().isDisabled(kitList.stream().findFirst().orElse(null), kitType)) {
				if (!enabled) {
					sender.sendMessage("§cO kit já está desativado!");
					return;
				}
			} else {
				if (enabled) {
					sender.sendMessage("§aO kit já está ativado!");
					return;
				}
			}
		}

		if (!enabled) {
			for (Gamer gamer : GameGeneral.getInstance().getGamerController().getGamers()) {
				if (kitList.contains(gamer.getKit(kitType))) {
					GameGeneral.getInstance().getKitController().setKit(gamer.getPlayer(), null, kitType);
				}
			}

			for (Kit kit : kitList) {
				ServerConfig.getInstance().disableKit(kit, kitType);
			}

			if (args[0].equalsIgnoreCase("all"))
				GameGeneral.getInstance().getAbilityController().unregisterAbilityListeners();
			else
				for (Kit kit : kitList) {
					for (Ability ability : kit.getAbilities()) {
						GameGeneral.getInstance().getAbilityController().unregisterAbilityListeners(ability);
					}
				}
		} else
			for (Kit kit : kitList) {
				ServerConfig.getInstance().enableKit(kit, kitType);
			}

		List<String> listName = new ArrayList<>();

		for (Kit kit : kitList)
			listName.add(kit.getName());

		sender.sendMessage(" §aVocê " + (enabled ? "ativou" : "desativou") + " "
				+ (args[0].equalsIgnoreCase("all") ? "todos os kits"
						: "os kits " + Joiner.on(", ").join(listName))
				+ " com sucesso.");
		CommonGeneral.getInstance().getMemberManager()
				.broadcast("§7O " + sender.getName() + " " + (enabled ? "ativou" : "desativou")
						+ (args[0].equalsIgnoreCase("all") ? " todos os kits"
								: " o(s) kit(s) §a" + Joiner.on(", ").join(listName))
						+ "§f!", Group.MOD);
	}

	@Completer(name = "kit", aliases = { "togglekit", "forcekit", "givekit", "defaultkit" })
	public List<String> tagCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 1) {
			List<String> list = new ArrayList<>();

			for (Kit kit : GameGeneral.getInstance().getKitController().getAllKits())
				if (kit.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase()))
					list.add(kit.getName());

			return list;
		}

		return new ArrayList<>();
	}

	private void handleKit(CommandSender s, String[] args, String label, KitType kitType) {
		if (!(s.isPlayer()))
			return;

		if (GameMain.getInstance().getGeneral().getGameState().equals(GameState.GAMETIME)) {
			s.sendMessage("§cComando não encontrado.");
			return;
		}

		BukkitMember member = (BukkitMember) s;

		if (args.length == 0) {
			new SelectorInventory(member.getPlayer(), 1, kitType, OrderType.MINE);
			return;
		}

		Kit kit = GameGeneral.getInstance().getKitController().getKit(args[0]);

		if (kit == null) {
			s.sendMessage("§cO kit \"" + args[0] + "\" não existe!");
			return;
		}

		GameGeneral.getInstance().getKitController().selectKit(member.getPlayer(), kit, kitType);
	}
}
