package br.com.pentamc.common.clan;

import java.util.UUID;

import br.com.pentamc.common.account.Member;

public class ClanVoid extends Clan {
	
	public ClanVoid(UUID uniqueId, String clanName, String clanAbbreviation, Member owner) {
		super(uniqueId, clanName, clanAbbreviation, owner);
	}

	public ClanVoid(ClanModel clanModel) {
		super(clanModel);
	}


}
