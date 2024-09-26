package br.com.pentamc.bukkit.command;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.command.CommandArgs;
import br.com.pentamc.common.permission.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.pentamc.bukkit.bukkit.BukkitMember;

public class BukkitCommandArgs extends CommandArgs {

	protected BukkitCommandArgs(CommandSender sender, String label, String[] args, int subCommand) {
		super(sender instanceof Player
				? CommonGeneral.getInstance().getMemberManager().getMember(((Player) sender).getUniqueId())
				: new BukkitCommandSender(sender), label, args, subCommand);
	}

	@Override
	public boolean isPlayer() {
		return getSender() instanceof Member;
	}

	public Player getPlayer() {
		if (!isPlayer())
			return null;
		return (Player) ((BukkitMember) getSender()).getPlayer();
	}

	public int broadcast(String message, Group group) {
		int x = 0;

		for (Member battlePlayer : CommonGeneral.getInstance().getMemberManager().getMembers()) {
			Player player = Bukkit.getPlayer(battlePlayer.getUniqueId());

			if (player == null)
				continue;

			player.sendMessage(message);
			x++;
		}

		return x;
	}

}
