package br.com.pentamc.pvp.event.death;

import br.com.pentamc.bukkit.event.NormalEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class PlayerKilledEvent extends NormalEvent {

    protected final Player
            target,
            killer;
}
