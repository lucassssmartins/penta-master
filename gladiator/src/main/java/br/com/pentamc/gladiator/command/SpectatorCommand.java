package br.com.pentamc.gladiator.command;

import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.gladiator.GameGeneral;
import br.com.pentamc.gladiator.challenge.Challenge;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import br.com.pentamc.bukkit.command.BukkitCommandArgs;

public class SpectatorCommand implements CommandClass {

	@CommandFramework.Command(name = "spectator", aliases = { "espectar", "spec" }, groupToUse = Group.VIP)
	public void spectatorCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(
					" §cUse /" + cmdArgs.getLabel() + " <playerName>§c para espectar o combate de alguém.");
			return;
		}

		Player target = Bukkit.getPlayer(args[0]);

		if (target == null) {
			return;
		}

		if (GameGeneral.getInstance().getChallengeController().containsKey(target)) {
			Challenge challenge = GameGeneral.getInstance().getChallengeController().getValue(target);

			challenge.spectate(player);
		} else {
			player.sendMessage("§cO jogador " + target.getName() + " não está em combate.");
		}
	}
}
