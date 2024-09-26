package br.com.pentamc.common.clan.event.member;

import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.clan.Clan;
import br.com.pentamc.common.clan.event.ClanEvent;
import lombok.Getter;

@Getter
public class MemberOnlineEvent extends ClanEvent {
	
	private Member member;
	private boolean online;
	
	public MemberOnlineEvent(Clan clan, Member member, boolean online) {
		super(clan);
		this.member = member;
		this.online = online;
	}

}
