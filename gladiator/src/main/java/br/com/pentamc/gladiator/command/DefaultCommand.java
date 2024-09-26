package br.com.pentamc.gladiator.command;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.gladiator.GameGeneral;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

import br.com.pentamc.bukkit.command.BukkitCommandArgs;

public class DefaultCommand implements CommandClass {

	@CommandFramework.Command(name = "spawn")
	public void spawnCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();

		if (GameGeneral.getInstance().getChallengeController().containsKey(player)) {
			player.sendMessage("§cVocê está em um Gladiator!");
			return;
		}

		Bukkit.getPluginManager().callEvent(
				new PlayerRespawnEvent(player, BukkitMain.getInstance().getLocationFromConfig("spawn"), false));
	}
}
