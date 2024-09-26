package br.com.pentamc.common.clan.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import br.com.pentamc.common.clan.Clan;

@Getter
@AllArgsConstructor
public abstract class ClanEvent {
	
	private Clan clan;
	
}
