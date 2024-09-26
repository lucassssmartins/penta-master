package br.com.pentamc.pvp.user;

import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.pvp.game.Game;
import br.com.pentamc.pvp.kit.type.KitType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class User {

    private final UUID uniqueId;

    private Game game;
    private Scoreboard scoreboard;

    private KitType
            kitOne = KitType.NONE,
            kitTwo = KitType.NONE;

    private boolean isProtected = true;

    public boolean isUsing(KitType kitType) {
        return kitOne.equals(kitType) || kitTwo.equals(kitType);
    }

    public boolean isUsingNeo() {
        return kitOne.equals(KitType.NEO) || kitTwo.equals(KitType.NEO);
    }

    public List<ItemStack> getSpecialItems() {
        List<ItemStack> items = new ArrayList<>();

        if (kitOne.getKit().getSpecialItem() != null)
            items.addAll(kitOne.getKit().getSpecialItem());

        if (kitTwo.getKit().getSpecialItem() != null)
            items.addAll(kitTwo.getKit().getSpecialItem());

        return items;
    }
}
