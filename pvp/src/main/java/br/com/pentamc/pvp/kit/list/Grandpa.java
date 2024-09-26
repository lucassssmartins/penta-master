package br.com.pentamc.pvp.kit.list;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Grandpa extends Kit implements Listener {
    public Grandpa() {
        super(
                "Empurre seus inimigos usando seu graveto!",
                KitType.GRANDPA,
                Material.STICK,
                2500,
                Group.VIP,
                0L
        );
    }

    @Override
    public List<ItemStack> getSpecialItem() {
        return Arrays.asList(
                new ItemBuilder()
                        .type(Material.STICK)
                        .enchantment(Enchantment.KNOCKBACK, 1)
                        .build()
        );
    }
}
