package br.com.pentamc.bukkit.bukkit;

import java.util.UUID;

import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.clan.Clan;
import br.com.pentamc.common.clan.ClanModel;

public class BukkitClan extends Clan {
	
	public BukkitClan(UUID uniqueId, String clanName, String clanAbbreviation, Member owner) {
		super(uniqueId, clanName, clanAbbreviation, owner);
	}

	public BukkitClan(ClanModel clanModel) {
		super(clanModel);
	}
	
}
