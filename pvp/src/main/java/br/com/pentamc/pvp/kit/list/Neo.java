package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Neo extends Kit {
    public Neo() {
        super(
                "Torne-se imune a projeteis e gladiadores!",
                KitType.NEO,
                Material.ARROW,
                4300,
                Group.BETA,
                0L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return null;
    }
}
