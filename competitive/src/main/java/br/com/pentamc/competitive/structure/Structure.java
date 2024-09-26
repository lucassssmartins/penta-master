package br.com.pentamc.competitive.structure;

import org.bukkit.Location;

public interface Structure {
	
	Location findPlace();
	
	void spawn(Location location);
	
}
