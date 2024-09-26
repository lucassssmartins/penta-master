package br.com.pentamc.pvp.util.structure;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Structure {

    void spawn();
    void remove();

    List<ItemStack> getItems();
}
