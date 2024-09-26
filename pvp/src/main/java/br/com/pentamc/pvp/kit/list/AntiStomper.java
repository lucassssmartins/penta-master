package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class AntiStomper extends Kit implements Listener {
    public AntiStomper() {
        super(
                "Torne-se imune a stompers!",
                KitType.ANTISTOMPER,
                Material.DIAMOND_HELMET,
                5000,
                Group.BETA,
                0L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return null;
    }
}
