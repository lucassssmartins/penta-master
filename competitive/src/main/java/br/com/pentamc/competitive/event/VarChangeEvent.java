package br.com.pentamc.competitive.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import br.com.pentamc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class VarChangeEvent extends NormalEvent {

	private String varName;
	private String oldValue;
	private String newValue;

}
