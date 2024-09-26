package br.com.pentamc.common.clan.event.member;

import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.clan.Clan;
import br.com.pentamc.common.clan.event.ClanEvent;
import lombok.Getter;

@Getter
public class MemberLeaveEvent extends ClanEvent {
	
	private Member member;

	public MemberLeaveEvent(Clan clan, Member member) {
		super(clan);
		this.member = member;
	}

}
