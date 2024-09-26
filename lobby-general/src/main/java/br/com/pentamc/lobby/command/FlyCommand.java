package br.com.pentamc.lobby.command;

import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.common.command.CommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.common.permission.Group;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandClass {

	@CommandFramework.Command(name = "fly", groupToUse = Group.VIP)
	public void flyCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();

		player.setAllowFlight(!player.getAllowFlight());
		player.sendMessage(player.getAllowFlight() ? "§aVocê ativou o fly!" : "§cVocê desativou o fly!");

		if (player.getAllowFlight())
			player.setFlying(true);
	}

}
