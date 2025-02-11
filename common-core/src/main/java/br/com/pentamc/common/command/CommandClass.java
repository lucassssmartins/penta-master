package br.com.pentamc.common.command;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.permission.Group;

/*
 * Forked from https://github.com/mcardy/CommandFramework
 * 
 */

public interface CommandClass {

	default void broadcast(String message, Group group) {
		CommonGeneral.getInstance().getMemberManager().broadcast(message, group);
	}

	default void staffLog(String message, Group group) {
		CommonGeneral.getInstance().getMemberManager().getMembers().stream().filter(
				member -> member.hasGroupPermission(group) && member.getAccountConfiguration().isSeeingStafflog())
				.forEach(member -> member.sendMessage("§8Staff> §f" + message));

		CommonGeneral.getInstance().getCommonPlatform().getConsoleSender().sendMessage("§8Staff> §f" + message);
	}

}
