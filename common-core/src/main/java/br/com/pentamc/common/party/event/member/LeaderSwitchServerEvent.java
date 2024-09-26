package br.com.pentamc.common.party.event.member;

import br.com.pentamc.common.account.Member;
import lombok.Getter;
import br.com.pentamc.common.party.Party;
import br.com.pentamc.common.party.event.PartyEvent;

@Getter
public class LeaderSwitchServerEvent extends PartyEvent {
	
	private Member leader;

	public LeaderSwitchServerEvent(Party clan, Member leader) {
		super(clan);
		this.leader = leader;
	}

}
