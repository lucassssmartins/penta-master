package br.com.pentamc.bukkit.command.register;

import java.util.UUID;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.command.BukkitCommandArgs;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.command.CommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.profile.Profile;
import br.com.pentamc.common.tag.Tag;
import br.com.pentamc.common.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import br.com.pentamc.bukkit.api.player.PlayerAPI;
import br.com.pentamc.bukkit.api.player.TextureFetcher;
import br.com.pentamc.bukkit.api.scoreboard.ScoreboardAPI;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.menu.account.SkinInventory;
import br.com.pentamc.bukkit.menu.account.SkinInventory.MenuType;

public class YoutubeCommand implements CommandClass {

	private static final String[] FAKE_RANDOM = { "broowkk_", "yNegocioNegocio", "YTBERMASTER__", "KillepHG", "Foccus",
			"_Usayy_", "_Xereyy_", "_UseiDrogas_", "_ProerdHG_", "_Dollows_", "_Finalee_", "BrabaoPvP", "brouqui",
			"YanDavii", "abreuzinpvp", "Vooei", "Surfaaay", "uDeathadder", "MouseG0D", "ThePrinceHG", "LGostosoNooT",
			"GANGMEMBERXITO", "XITOCONTRAISPOPI", "MEBANIRAMNOCOGU" };

	@CommandFramework.Command(name = "fake", aliases = { "nick" }, groupToUse = Group.BETA, runAsync = true)
	public void fakeCommand(CommandArgs args) {
		if (!args.isPlayer())
			return;

		Player player = ((BukkitMember) args.getSender()).getPlayer();
		Member member = (Member) args.getSender();

		if (args.getArgs().length != 1) {
			player.sendMessage("§cUso /fake <player> para trocar de nick!");
			return;
		}

		boolean remove = args.getArgs()[0].equals("#") || member.getPlayerName().equals(args.getArgs()[0]);

		if (remove && !member.isUsingFake()) {
			player.sendMessage("§cVocê não está usando fake!");
			return;
		}

		boolean random = args.getArgs()[0].equalsIgnoreCase("random");

		String f = remove ? member.getPlayerName()
				: random ? FAKE_RANDOM[CommonConst.RANDOM.nextInt(FAKE_RANDOM.length)] : args.getArgs()[0];

		if (!remove) {
			if (Bukkit.getPlayer(f) != null) {
				if (args.getArgs()[0].equalsIgnoreCase("random"))
					while (Bukkit.getPlayer(f) != null)
						f = random ? FAKE_RANDOM[CommonConst.RANDOM.nextInt(FAKE_RANDOM.length)] : args.getArgs()[0];
				else
					player.sendMessage("§cO nickname do seu fake está online!");

				return;
			}
		}

		if (!PlayerAPI.validateName(f)) {
			player.sendMessage("§cO nickname que você colocou está inválido!");
			return;
		}

		if (!member.hasGroupPermission(Group.YOUTUBERPLUS))
			if (member.isOnCooldown("fakeCommand") && !remove) {
				member.sendMessage("§cVocê precisa esperar " + DateUtils.getTime(member.getCooldown("fakeCommand"))
						+ " para trocar de fake novamente!");
				return;
			}

		String fakeName = f;

		player.sendMessage("§eSeu pedido está sendo carregado, aguarde!");
		UUID uuid = CommonGeneral.getInstance().getMojangFetcher().requestUuid(fakeName);

		if (!remove && uuid != null) {
			player.sendMessage("§cO jogador existe na mojang!");
			return;
		}

		String playerName = fakeName;
		WrappedSignedProperty property = BukkitMain.getInstance().getSkinManager().getSkin(uuid);

		new BukkitRunnable() {

			@Override
			public void run() {
				if (remove) {
					ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(player);

					if (property == null)
						PlayerAPI.changePlayerSkin(player, playerName, member.getUniqueId(), false);
					else
						PlayerAPI.changePlayerSkin(player, property, false);

					player.sendMessage("§cO seu fake foi removido!");
				} else {
					player.sendMessage("§aO seu fake foi alterado para " + playerName + "!");
					player.sendMessage("§aUse /fake # para remover seu fake!");
					member.setCooldown("fakeCommand",
							member.hasGroupPermission(Group.ADMIN) ? System.currentTimeMillis() + (1000)
									: System.currentTimeMillis() + (1000 * 60));
				}

				PlayerAPI.changePlayerName(player, playerName);

				member.setTag(remove ? member.getTag() : Tag.MEMBRO);
				member.setFakeName(remove ? member.getPlayerName() : playerName);
			}

		}.runTask(BukkitMain.getInstance());
	}

	@CommandFramework.Command(name = "changeskin", aliases = { "skin" }, groupToUse = Group.VIP, runAsync = true)
	public void changeskinCommand(BukkitCommandArgs args) {
		if (!args.isPlayer())
			return;

		Player player = args.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (args.getArgs().length != 1) {
			new BukkitRunnable() {

				@Override
				public void run() {
					new SkinInventory(player, member, MenuType.GENERAL);
				}
			}.runTask(BukkitMain.getInstance());
			return;
		}

		boolean remove = args.getArgs()[0].equals("#") || member.getPlayerName().equals(args.getArgs()[0]);
		String playerName = remove ? member.getPlayerName() : args.getArgs()[0];

		if (remove && !member.hasSkin()) {
			player.sendMessage("§cVocê não está usando nenhuma skin customizada!");
			return;
		}

		if (!remove && !member.hasGroupPermission(Group.TRIAL) && member.isOnCooldown("changeskinCommand")) {
			player.sendMessage("§cVocê precisa esperar " + DateUtils.getTime(member.getCooldown("changeskinCommand"))
					+ " para trocar de skin novamente!");
			return;
		}

		if (!PlayerAPI.validateName(playerName) && !remove) {
			player.sendMessage("§cO nickname que você colocou está inválido!");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getMojangFetcher().requestUuid(playerName);

		if (uuid == null) {
			player.sendMessage("§cO jogador não existe!");
			return;
		}

		WrappedSignedProperty property = TextureFetcher.loadTexture(
				new WrappedGameProfile(uuid, playerName.equals("#") ? member.getPlayerName() : playerName));

		new BukkitRunnable() {

			@Override
			public void run() {
				PlayerAPI.changePlayerSkin(player, property, true);
				player.sendMessage("§aSua skin foi alterada "
						+ (remove ? "para a §asua skin original" : "para a do " + playerName) + "!");

				member.setSkinProfile(new Profile(remove ? member.getPlayerName() : playerName,
						remove ? member.getUniqueId() : uuid));

				if (!remove)
					member.setCooldown("changeskinCommand",
							member.hasGroupPermission(Group.VIP) ? System.currentTimeMillis() + (1000 * 60 * 3)
									: System.currentTimeMillis() + (1000 * 60 * 10));
			}

		}.runTask(BukkitMain.getInstance());
	}

}
