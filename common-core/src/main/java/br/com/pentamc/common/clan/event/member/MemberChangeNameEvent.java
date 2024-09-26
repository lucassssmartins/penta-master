package br.com.pentamc.common.clan.event.member;

import br.com.pentamc.common.account.Member;
import lombok.Getter;
import br.com.pentamc.common.clan.Clan;
import br.com.pentamc.common.clan.event.ClanEvent;

@Getter
public class MemberChangeNameEvent extends ClanEvent {
	
	private Member member;

	public MemberChangeNameEvent(Clan clan, Member member) {
		super(clan);
		this.member = member;
	}

}
