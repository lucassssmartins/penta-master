package br.com.pentamc.pvp.event;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class ChallengeGladiatorEvent extends PlayerCancellableEvent {

    private Player target;

    public ChallengeGladiatorEvent(Player player, Player target) {
        super(player);

        this.target = target;
    }
}
