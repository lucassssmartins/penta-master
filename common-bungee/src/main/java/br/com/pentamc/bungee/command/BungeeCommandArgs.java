package br.com.pentamc.bungee.command;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.command.CommandArgs;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import br.com.pentamc.bungee.bungee.BungeeMember;

public class BungeeCommandArgs extends CommandArgs {

	protected BungeeCommandArgs(CommandSender sender, String label, String[] args, int subCommand) {
		super(sender instanceof ProxiedPlayer
				? CommonGeneral.getInstance().getMemberManager().getMember(((ProxiedPlayer) sender).getUniqueId())
				: new BungeeCommandSender(sender), label, args, subCommand);
	}

	@Override
	public boolean isPlayer() {
		return getSender() instanceof Member;
	}

	public ProxiedPlayer getPlayer() {
		if (!isPlayer())
			return null;
		return (ProxiedPlayer) ((BungeeMember) getSender()).getProxiedPlayer();
	}

}
