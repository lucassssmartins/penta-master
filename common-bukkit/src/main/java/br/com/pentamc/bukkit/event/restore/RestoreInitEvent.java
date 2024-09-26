package br.com.pentamc.bukkit.event.restore;

import java.util.List;

import br.com.pentamc.common.profile.Profile;
import lombok.Getter;

@Getter
public class RestoreInitEvent extends RestoreEvent {
	
	private List<Profile> profileList;

	public RestoreInitEvent(List<Profile> profileList) {
		this.profileList = profileList;
	}

}
