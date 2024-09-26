package br.com.pentamc.pvp.event;

import br.com.pentamc.bukkit.event.NormalEvent;
import br.com.pentamc.pvp.inventory.type.InventoryType;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KitEvent extends NormalEvent {

    private final User user;
    private final InventoryType location;
    private final KitType type;
}
