package br.com.pentamc.bukkit.api.cooldown.event;

import br.com.pentamc.bukkit.api.cooldown.types.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CooldownStartEvent extends CooldownEvent implements Cancellable {
	
    private boolean cancelled;

    public CooldownStartEvent(Player player, Cooldown cooldown) {
        super(player, cooldown);
    }

}
