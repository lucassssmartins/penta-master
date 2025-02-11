
package br.com.pentamc.bukkit.command.register;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.command.CommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.common.command.CommandSender;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.profile.Profile;
import br.com.pentamc.common.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Joiner;

import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.event.player.TeleportAllEvent;
import br.com.pentamc.bukkit.event.restore.RestoreEvent;
import br.com.pentamc.bukkit.event.restore.RestoreInitEvent;
import br.com.pentamc.bukkit.event.restore.RestoreStopEvent;
import br.com.pentamc.bukkit.event.teleport.PlayerTeleportCommandEvent;
import br.com.pentamc.bukkit.event.teleport.PlayerTeleportCommandEvent.TeleportResult;

@SuppressWarnings("deprecation")
public class ModeratorCommand implements CommandClass {

	private DecimalFormat locationFormater = new DecimalFormat("######.##");

	@CommandFramework.Command(name = "gamemode", aliases = { "gm" }, groupToUse = Group.MOD)
	public void gamemodeCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage("§cUse /gamemode <gamemode> para alterar seu gamemode.");
			return;
		}

		GameMode gamemode = null;

		try {
			gamemode = GameMode.valueOf(args[0].toUpperCase());
		} catch (Exception e) {
			try {
				gamemode = GameMode.getByValue(Integer.parseInt(args[0]));
			} catch (Exception ex) {
				player.sendMessage(" §cO gamemode §c\"" + args[0] + "\"§c não foi encontrado.");
				return;
			}
		}

		String gamemodeName = gamemode == GameMode.SURVIVAL ? "Sobrevivência"
				: gamemode == GameMode.ADVENTURE ? "Aventura"
						: gamemode == GameMode.SPECTATOR ? "Espectador" : "Criativo";

		if (args.length == 1) {
			if (player.getGameMode() != gamemode) {
				player.setGameMode(gamemode);
				player.sendMessage(" §aVocê alterou seu gamemode para §a" + gamemodeName + "§a.");
				staffLog("§aO §a" + player.getName() + " §amudou seu gamemode para §a" + gamemodeName + "§a.",
						Group.TRIAL);
			} else {
				player.sendMessage("§cVocê já está nesse gamemode.");
			}

			return;
		}

		Player target = Bukkit.getPlayer(args[1]);

		if (target == null) {
			player.sendMessage(" §cO jogador §c\"" + args[1] + "\"§c não existe.");
			return;
		}

		if (target.getGameMode() != gamemode) {
			target.setGameMode(gamemode);
			player.sendMessage(
					" §aVocê alterou gamemode de §a" + target.getName() + "§a para §a" + gamemodeName + "§a.");
			staffLog("§aO §a" + player.getName() + " §amudou o gamemode de §a" + target.getName() + " §apara §a"
					+ gamemodeName + "§a.", Group.TRIAL);
		} else {
			player.sendMessage("§cO §c" + target.getName() + "§c já está nesse gamemode§c.");
		}
	}

	@CommandFramework.Command(name = "restore", groupToUse = Group.MOD)
	public void restoreCommand(CommandArgs cmdArgs) {
		boolean restore = !BukkitMain.getInstance().getServerConfig().isRestoreMode();

		RestoreEvent event = restore
				? new RestoreInitEvent(CommonGeneral.getInstance().getMemberManager().getMembers().stream()
						.map(member -> new Profile(((BukkitMember) member).getPlayerName(),
								((BukkitMember) member).getUniqueId()))
						.collect(Collectors.toList()))
				: new RestoreStopEvent();
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			BukkitMain.getInstance().getServerConfig().setRestoreMode(restore);
			Bukkit.broadcastMessage(
					restore ? "§aO modo restauração foi ativado!" : "§cO modo restauração foi desativado!");
		}
	}

	@CommandFramework.Command(name = "clear", groupToUse = Group.YOUTUBERPLUS)
	public void clear(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			player.getInventory().clear();
			player.getInventory().setArmorContents(new ItemStack[4]);
			player.getActivePotionEffects().clear();
			player.getInventory().setHeldItemSlot(0);
			player.sendMessage("§aVocê limpou o seu inventário!");
			staffLog("O §a" + player.getName() + " §flimpou o seu próprio inventário§f.", Group.TRIAL);
			return;
		}

		Player target = Bukkit.getPlayer(args[0]);

		if (target == null) {
			player.sendMessage(" §cO jogador §c\"" + args[0] + "\"§c não existe.");
			return;
		}

		target.getInventory().clear();
		target.getInventory().setArmorContents(new ItemStack[4]);
		target.getActivePotionEffects().clear();
		target.getInventory().setHeldItemSlot(0);
		target.sendMessage("§aO seu inventário foi limpo pelo " + player.getName() + ".");
		player.sendMessage("§aVocê limpou o inventário de §a" + target.getName() + ".");
		staffLog("§aO §a" + player.getName() + " §alimpou o inventário de " + target.getName() + "§a.", Group.TRIAL);
	}

	@CommandFramework.Command(name = "enchant", usage = "/<command> <enchanment> <level>", groupToUse = Group.MOD)
	public void enchant(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(" §cUtilize §c/enchant <encantamento> <level>§c para encantar algum item.");
			return;
		}

		ItemStack item = player.getItemInHand();

		if (item == null || item.getType() == Material.AIR) {
			player.sendMessage(" §cVocê não está com nada na sua mão para encantar.");
			return;
		}

		Enchantment enchantment = Enchantment.getByName(args[0].toUpperCase());

		if (enchantment == null) {
			Integer id = null;

			try {
				id = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				player.sendMessage(" §cO formato de numero é inválido.");
				return;
			}

			enchantment = Enchantment.getById(id);
		}

		Integer level = 1;

		if (args.length >= 2) {
			try {
				level = Integer.valueOf(args[1]);
			} catch (NumberFormatException e) {
			}

			if (level < 1) {
				player.sendMessage(" §cO nível de encantamento é muito baixo.");
				return;
			}
		}

		item.addUnsafeEnchantment(enchantment, level);
		player.sendMessage(" §aVocê aplicou o encantamento §a" + enchantment.getName() + "§a no nível §a" + level
				+ "§a na sua §a" + item.getType().toString() + "§a.");
		staffLog("O §a" + player.getName() + " §aencantou sua §a" + item.getType().toString() + "§a com §a"
				+ enchantment.getName() + " level " + level + "§a.", Group.TRIAL);
	}

	@CommandFramework.Command(name = "whitelist", groupToUse = Group.MODPLUS, runAsync = true)
	public void whitelistCommand(CommandArgs cmdArgs) {

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(
					"§cUso /whitelist <on:off:list:add:remove> para desativar, ativar, listar, adicionar na lista e remover da lista.");
			return;
		}

		// on/off/list/add/remove

		switch (args[0].toLowerCase()) {
		case "on": {

			if (BukkitMain.getInstance().getServerConfig().isWhitelist()) {
				sender.sendMessage("§cO servidor já está com a whitelist ativada.");
			} else {
				BukkitMain.getInstance().getServerConfig().setWhitelist(true);
				sender.sendMessage("§aVocê ativou a whitelist.");

				new BukkitRunnable() {
					@Override
					public void run() {
						CommonGeneral.getInstance().getServerData().setJoinEnabled(false);
					}
				}.runTaskAsynchronously(BukkitMain.getInstance());
			}

			break;
		}
		case "off": {
			if (!BukkitMain.getInstance().getServerConfig().isWhitelist()) {
				sender.sendMessage("§cO servidor já está com a whitelist desativada!");
			} else {
				BukkitMain.getInstance().getServerConfig().setWhitelist(false);
				sender.sendMessage("§cVocê desativou a whitelist!");

				new BukkitRunnable() {
					@Override
					public void run() {
						CommonGeneral.getInstance().getServerData().setJoinEnabled(true);
					}
				}.runTaskAsynchronously(BukkitMain.getInstance());
			}

			break;
		}
		case "add":
		case "remove": {
			if (args.length == 1) {
				sender.sendMessage("§eUse /whitelist <on:off:list:add:remove>.");
				break;
			}

			if (args[1].equalsIgnoreCase("all")) {

				List<Profile> profileList = new ArrayList<>();

				if (args[0].equalsIgnoreCase("add"))
					for (Member member : CommonGeneral.getInstance().getMemberManager().getMembers()) {
						Profile profile = new Profile(member.getPlayerName(), member.getUniqueId());

						if (BukkitMain.getInstance().getServerConfig().addWhitelist(profile)) {
							profileList.add(profile);

							CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

								@Override
								public void run() {
									CommonGeneral.getInstance().getServerData().addWhitelist(profile);
								}
							});
						}
					}
				else
					for (Member member : CommonGeneral.getInstance().getMemberManager().getMembers()) {
						Profile profile = new Profile(member.getPlayerName(), member.getUniqueId());

						if (BukkitMain.getInstance().getServerConfig().removeWhitelist(profile)) {
							profileList.add(profile);

							CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

								@Override
								public void run() {
									CommonGeneral.getInstance().getServerData().removeWhitelist(profile);
								}
							});
						}
					}

				sender.sendMessage(
						(args[0].equalsIgnoreCase("add") ? "§aVocê adicionou " : "§cVocê removeu ")
								+ (profileList.size() <= 5
										? Joiner.on(", ")
												.join(profileList.stream().map(Profile::getPlayerName)
														.collect(Collectors.toList()))
										: profileList.size() + " jogadores")
								+ " na whitelist!");
				return;
			}

			String playerName = args[1];
			UUID uniqueId = CommonGeneral.getInstance().getUuid(playerName);

			Profile profile = new Profile(playerName, uniqueId);

			if (args[0].equalsIgnoreCase("add")) {
				BukkitMain.getInstance().getServerConfig().addWhitelist(profile);
				sender.sendMessage("§a" + args[1] + " adicionado na whitelist!");

				CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

					@Override
					public void run() {
						CommonGeneral.getInstance().getServerData().addWhitelist(profile);
					}
				});
			} else {
				BukkitMain.getInstance().getServerConfig().removeWhitelist(profile);
				sender.sendMessage("§a" + args[1] + " removido na whitelist!");

				CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

					@Override
					public void run() {
						CommonGeneral.getInstance().getServerData().removeWhitelist(profile);
					}
				});
			}
			break;
		}
		case "list": {
			sender.sendMessage(
					"§7Jogadores na whitelist: " + (BukkitMain.getInstance().getServerConfig().getWhiteList().isEmpty()
							? "§cNinguém está na whitelist"
							: Joiner.on("§f, §a").join(BukkitMain.getInstance().getServerConfig().getWhiteList()
									.stream().map(Profile::getPlayerName).collect(Collectors.toList()))));
			break;
		}
		default: {
			sender.sendMessage(" §eUse §a/whitelist <on:off:list:add:remove>§e. ");
			break;
		}
		}
	}

	@CommandFramework.Command(name = "blacklist", groupToUse = Group.MODPLUS, runAsync = true)
	public void blacklistCommand(CommandArgs cmdArgs) {

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(
					" §eUse /blacklist <add:remove> para um jogador não consegui mais entrar nesse servidor.");
			return;
		}

		// on/off/list/add/remove

		switch (args[0].toLowerCase()) {
		case "add":
		case "remove": {
			if (args.length == 1) {
				sender.sendMessage("§eUse /whitelist <on:off:list:add:remove>. ");
				break;
			}

			String playerName = args[1];
			UUID uniqueId = CommonGeneral.getInstance().getUuid(playerName);

			Profile profile = new Profile(playerName, uniqueId);

			if (args[0].equalsIgnoreCase("add")) {
				long time = System.currentTimeMillis() + (1000 * 60 * 60 * 12);

				if (args.length >= 3)
					time = DateUtils.getTime(args[1]);

				BukkitMain.getInstance().getServerConfig().blacklist(profile, time);
				sender.sendMessage("§a" + args[1] + " adicionado na blacklist!");
			} else {
				BukkitMain.getInstance().getServerConfig().unblacklist(profile);
				sender.sendMessage("§a" + args[1] + " removido na blacklist!");
			}
			break;
		}
		default: {
			sender.sendMessage(
					" §eUse /blacklist <add:remove> para um jogador não consegui mais entrar nesse servidor.");
			break;
		}
		}
	}

	@CommandFramework.Command(name = "effect", usage = "/<command> <effect> <duration> <amplifier>", groupToUse = Group.MOD)
	public void effectCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player sender = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length < 2) {
			sender.sendMessage(
					"§eUtilize /effect <player> <efeito> <duração> <intensidade> para aplicar um efeito em alguém.");
			return;
		}

		Player player = sender.getServer().getPlayer(args[0]);

		if (player == null) {
			sender.sendMessage("§cO jogador §c\"" + args[0] + "\"§c não existe.");
			return;
		}

		if (args[1].equalsIgnoreCase("clear")) {
			for (PotionEffect effect : player.getActivePotionEffects()) {
				player.removePotionEffect(effect.getType());
			}

			sender.sendMessage(" §aO jogador §a" + player.getName() + "§a teve seus efeitos removidos.");
			return;
		}

		PotionEffectType effect = PotionEffectType.getByName(args[1].toUpperCase());

		if (effect == null) {
			Integer potionId = null;

			try {
				potionId = Integer.valueOf(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§cO efeito §c\"" + args[1] + "\"§c não existe.");
				return;
			}

			effect = PotionEffectType.getById(potionId);
		}

		if (effect == null) {
			sender.sendMessage("§cO efeito §c\"" + args[1] + "\"§c não existe.");
			return;
		}

		Integer duration = null;

		try {
			duration = Integer.valueOf(args[2]);
		} catch (NumberFormatException e) {
			sender.sendMessage(" §cO formato de numero é inválido.");
			return;
		}

		Integer amplification = null;

		try {
			amplification = Integer.valueOf(args[3]);
		} catch (NumberFormatException e) {
			sender.sendMessage(" §cO formato de numero é inválido.");
			return;
		}

		if (duration == 0) {
			if (!player.hasPotionEffect(effect)) {
				sender.sendMessage(" §cO jogador não tem o efeito para ele ser removido.");
				return;
			}

			player.removePotionEffect(effect);
			sender.sendMessage("§aO jogador §a" + player.getName() + "§a teve o efeito §a" + effect.getName()
					+ "§a removido.");
			staffLog("§aO §a" + player.getName() + " §alimpou todos os seus efeitos!", Group.TRIAL);
		} else {
			PotionEffect applyEffect = new PotionEffect(effect, duration * 20, amplification);
			player.addPotionEffect(applyEffect, true);
			sender.sendMessage("§aO jogador §a" + player.getName() + "§a teve o efeito §a" + effect.getName()
					+ "§a adicionado §a(" + duration + " segundos e nível " + amplification + ")");
			staffLog("§aO §a" + player.getName() + " §aaplicou o efeito §a" + effect.getName() + "§a(" + duration
					+ " segundos e nível " + amplification + ")§a", Group.TRIAL);
		}
	}

	@CommandFramework.Command(name = "worldteleport", groupToUse = Group.ADMIN, aliases = { "tpworld", "tpworld" }, runAsync = false)
	public void worldteleportCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		if (cmdArgs.getArgs().length == 0) {
			cmdArgs.getSender().sendMessage("§eUtilize /tpworld <world> para mudar de mundo.");
			return;
		}

		World world = Bukkit.getWorld(cmdArgs.getArgs()[0]);

		if (world == null) {
			cmdArgs.getSender().sendMessage("§eO mundo está sendo carregado, aguarde.");

			WorldCreator worldCreator = new WorldCreator(cmdArgs.getArgs()[0].toLowerCase());

			worldCreator.type(WorldType.FLAT);
			worldCreator.generatorSettings("0;0");
			worldCreator.generateStructures(false);

			world = BukkitMain.getInstance().getServer().createWorld(worldCreator);

			world.setDifficulty(Difficulty.EASY);
			world.setGameRuleValue("doDaylightCycle", "false");

			CommonGeneral.getInstance().getLogger()
					.info("The world " + cmdArgs.getArgs()[0] + " has loaded successfully.");
			return;
		}

		((BukkitMember) cmdArgs.getSender()).getPlayer().teleport(new Location(world, 0, 10, 0));
		cmdArgs.getSender().sendMessage(" §aTeletransportado com sucesso.");
	}

	@CommandFramework.Command(name = "teleport", aliases = { "tp", "teleportar" }, runAsync = false)
	public void teleportCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player p = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		TeleportResult result = TeleportResult.NO_PERMISSION;
		String[] args = cmdArgs.getArgs();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		if (!member.hasGroupPermission(Group.TRIAL)) {
			result = TeleportResult.NO_PERMISSION;
		} else {
			if (member.hasGroupPermission(Group.MOD)) {
				result = TeleportResult.ALLOWED;
			} else {
				result = TeleportResult.ONLY_PLAYER_TELEPORT;
			}
		}

		PlayerTeleportCommandEvent event = new PlayerTeleportCommandEvent(p, result);
		Bukkit.getPluginManager().callEvent(event);

		if (event.getResult() == TeleportResult.NO_PERMISSION || event.isCancelled()) {
			p.sendMessage(" ");
			p.sendMessage(" §cVocê não tem permissão para isso.");
			p.sendMessage(" ");
			return;
		}

		if (args.length == 0) {
			p.sendMessage(" §eUtilize /tp <player> <player> para teletransportar jogadores.");
			return;
		}

		if (args.length == 1 || event.getResult() == TeleportResult.ONLY_PLAYER_TELEPORT) {
			Player t = Bukkit.getPlayer(args[0]);

			if (t == null) {
				p.sendMessage("§cjogador §a\"" + args[0] + "\"§a não existe.");
				return;
			}

			p.teleport(t.getLocation());
			p.sendMessage("§aVocê se teletransportou até o §a" + t.getName() + "§a.");
			return;
		}

		if (args.length == 2 && event.getResult() == TeleportResult.ALLOWED) {
			Player player = Bukkit.getPlayer(args[0]);

			if (player == null) {
				p.sendMessage(" §cO jogador §c\"" + args[0] + "\"§c não existe.");
				return;
			}

			Player target = Bukkit.getPlayer(args[1]);

			if (target == null) {
				p.sendMessage(" §cO jogador §c\"" + args[1] + "\"§c não existe.");
				return;
			}

			player.teleport(target);
			p.sendMessage(" §aVocê teletransportou §a" + player.getName() + "§a até §a" + target.getName() + "§a.");
			return;
		}

		if (args.length >= 3 && (event.getResult() == TeleportResult.ONLY_PLAYER_TELEPORT
				|| event.getResult() == TeleportResult.ALLOWED)) {
			if (args.length == 3) {
				Location loc = getLocationBased(p.getLocation(), args[0], args[1], args[2]);

				if (loc == null) {
					p.sendMessage("§cLocalização inválida.");
					return;
				}

				p.teleport(loc);
				p.sendMessage(" §aVocê se teletransportou até §a%x%§f, §a%y%§f, §a%z%§."
						.replace("%x%", locationFormater.format(loc.getX()))
						.replace("%y%", locationFormater.format(loc.getY()))
						.replace("%z%", locationFormater.format(loc.getZ())));
				return;
			}

			Player target = Bukkit.getPlayer(args[1]);

			if (target == null) {
				p.sendMessage(" §cO jogador §c\"" + args[1] + "\"§c não existe.");
				return;
			}

			Location loc = getLocationBased(target.getLocation(), args[1], args[2], args[3]);

			if (loc == null) {
				p.sendMessage(" §cLocalização inválida!");
				return;
			}

			target.teleport(loc);
			p.sendMessage("§aVocê se teletransportou até §a%x%§f, §a%y%§f, §a%z%§a."
					.replace("%x%", locationFormater.format(loc.getX()))
					.replace("%y%", locationFormater.format(loc.getY()))
					.replace("%z%", locationFormater.format(loc.getZ())).replace("%target%", target.getName()));
		}

		// TODO: ALERT STAFFS
	}

	@CommandFramework.Command(name = "teleportall", aliases = { "tpall" }, groupToUse = Group.MOD)
	public void tpallCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			int i = 0;

			for (Player on : Bukkit.getOnlinePlayers()) {
				if (on != null && on.isOnline() && on.getUniqueId() != player.getUniqueId()) {
					on.teleport(player.getLocation());
					on.setFallDistance(0.0F);
					on.sendMessage("§aVocê foi levado até o §a" + player.getName() + ".");
					i++;
				}
			}

			Bukkit.getPluginManager().callEvent(new TeleportAllEvent(player));
			player.sendMessage("§aVocê puxou todos os " + i + " jogadores até você.");
			return;
		}

		Player target = Bukkit.getPlayer(args[0]);

		if (target == null) {
			player.sendMessage("§cO jogador \"" + args[0] + "\" não existe!");
			return;
		}

		Iterator<? extends Player> iterator = Bukkit.getOnlinePlayers().iterator();

		new BukkitRunnable() {

			@Override
			public void run() {
				if (iterator.hasNext()) {
					Player on = iterator.next();

					on.teleport(target.getLocation());
					on.setFallDistance(0.0F);
					on.sendMessage(" §aVocê foi teletransportado até o §a" + target.getName() + "§a.");
				} else {
					cancel();
				}
			}

		}.runTaskTimer(BukkitMain.getInstance(), 0, 10);

		Bukkit.getPluginManager().callEvent(new TeleportAllEvent(target));
		player.sendMessage("§aVocê levou todos os " + Bukkit.getOnlinePlayers().size() + " jogadores até "
				+ target.getName() + ".");
		staffLog("O §a" + player.getName() + " §ateletransportou todos até ", Group.TRIAL);
		return;
	}

	@CommandFramework.Command(name = "teleportallasync", aliases = { "tpallasync", "tpalla" }, groupToUse = Group.MOD)
	public void tpallsyncCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			Iterator<? extends Player> iterator = Bukkit.getOnlinePlayers().iterator();
			Location location = player.getLocation();

			new BukkitRunnable() {

				@Override
				public void run() {
					if (iterator.hasNext()) {
						Player on = iterator.next();

						if (on.isOnline()) {

							on.teleport(location);
							on.setFallDistance(0.0F);
							on.sendMessage("§aVocê foi levado até o §a" + player.getName() + "§a.");
						}
					} else
						cancel();
				}

			}.runTaskTimer(BukkitMain.getInstance(), 0, 3);

			Bukkit.getPluginManager().callEvent(new TeleportAllEvent(player));

			player.sendMessage("§aVocê levou todos os " + Bukkit.getOnlinePlayers().size() + " jogadores até você.");

			staffLog("§aO §a" + player.getName() + " §ateletransportou todos até ele mesmo§a!", Group.TRIAL);
			return;
		}

		Player target = Bukkit.getPlayer(args[0]);

		if (target == null) {
			player.sendMessage("§cO jogador \"" + args[0] + "\" não existe.");
			return;
		}

		Iterator<? extends Player> iterator = Bukkit.getOnlinePlayers().iterator();
		Location location = target.getLocation();

		new BukkitRunnable() {

			@Override
			public void run() {
				if (iterator.hasNext()) {
					Player on = iterator.next();

					if (on.isOnline()) {
						on.teleport(location);
						on.setFallDistance(0.0F);
						on.sendMessage("§aVocê foi levado até o " + target.getName() + "!");
					}
				} else
					cancel();
			}

		}.runTaskTimer(BukkitMain.getInstance(), 0, 3);

		Bukkit.getPluginManager().callEvent(new TeleportAllEvent(target));
		player.sendMessage("§aVocê levou todos os " + Bukkit.getOnlinePlayers().size() + " jogadores até "
				+ target.getName() + "!");

		staffLog("O §a" + player.getName() + " §ateletransportou todos até §a" + target.getName() + "§a!", Group.TRIAL);
		return;
	}

	@CommandFramework.Command(name = "kick", aliases = { "kickar" }, groupToUse = Group.TRIAL)
	public void kick(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length < 1) {
			sender.sendMessage("§cUso /" + cmdArgs.getLabel() + " <player> <motivo> para kickar alguém.");
			return;
		}

		Player target = BukkitMain.getInstance().getServer().getPlayer(args[0]);

		if (target == null) {
			sender.sendMessage("§cO jogador \"" + args[0] + "\" não está online!");
			return;
		}

		boolean hasReason = false;
		StringBuilder builder = new StringBuilder();
		if (args.length > 1) {
			hasReason = true;
			for (int i = 1; i < args.length; i++) {
				String espaco = " ";
				if (i >= args.length - 1)
					espaco = "";
				builder.append(args[i] + espaco);
			}
		}

		if (!hasReason)
			builder.append("Sem motivo");

		for (Player player : Bukkit.getOnlinePlayers()) {
			Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

			if (!member.hasGroupPermission(Group.YOUTUBERPLUS))
				continue;

			player.sendMessage("§c\n§cO jogador " + target.getName() + " foi kickado pelo " + sender.getName() + " por "
					+ builder.toString() + "\n§c ");
		}

		target.kickPlayer("§4§l" + CommonConst.KICK_PREFIX
				+ "\n§c\n§cSua conta foi expulsa do servidor!\n§f\n§7Motivo: §f" + builder.toString().trim());
	}

	private Location getLocationBased(Location loc, String argX, String argY, String argZ) {
		double x = 0;
		double y = 0;
		double z = 0;
		if (!argX.startsWith("~")) {
			try {
				x = Integer.parseInt(argX);
			} catch (Exception e) {
				return null;
			}
		} else {
			x = loc.getX();
			try {
				x += Integer.parseInt(argX.substring(1, argX.length()));
			} catch (Exception e) {
			}
		}
		if (!argY.startsWith("~")) {
			try {
				y = Integer.parseInt(argY);
			} catch (Exception e) {
				return null;
			}
		} else {
			y = loc.getY();
			try {
				y += Integer.parseInt(argY.substring(1, argY.length()));
			} catch (Exception e) {
			}
		}
		if (!argZ.startsWith("~")) {
			try {
				z = Integer.parseInt(argZ);
			} catch (Exception e) {
				return null;
			}
		} else {
			z = loc.getZ();
			try {
				z += Integer.parseInt(argZ.substring(1, argZ.length()));
			} catch (Exception e) {
			}
		}

		Location l = loc.clone();
		l.setX(x);
		l.setY(y);
		l.setZ(z);
		return l;
	}
}
