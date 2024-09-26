package br.com.pentamc.common.clan.event.member;

import java.util.List;

import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.clan.Clan;
import br.com.pentamc.common.clan.event.ClanEvent;
import lombok.Getter;

@Getter
public class MemberChatEvent extends ClanEvent {

	private Member member;
	private List<Member> recipients;
	private String message;

	public MemberChatEvent(Clan clan, Member member, List<Member> recipients, String message) {
		super(clan);
		this.member = member;
		this.recipients = recipients;
		this.message = message;
	}

}
