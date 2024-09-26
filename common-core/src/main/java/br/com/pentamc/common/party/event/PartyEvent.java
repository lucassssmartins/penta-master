package br.com.pentamc.common.party.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import br.com.pentamc.common.party.Party;

@Getter
@AllArgsConstructor
public abstract class PartyEvent {
	
	private Party clan;
	
}
