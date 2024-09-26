package br.com.pentamc.bungee.bungee;

import java.util.UUID;

import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.clan.Clan;
import br.com.pentamc.common.clan.ClanModel;

public class BungeeClan extends Clan {
	
	public BungeeClan(UUID uniqueId, String clanName, String clanAbbreviation, Member owner) {
		super(uniqueId, clanName, clanAbbreviation, owner);
	}

	public BungeeClan(ClanModel clanModel) {
		super(clanModel);
	}

}
