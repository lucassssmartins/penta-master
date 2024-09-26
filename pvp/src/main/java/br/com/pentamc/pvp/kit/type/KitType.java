package br.com.pentamc.pvp.kit.type;

import br.com.pentamc.common.permission.Group;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.list.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum KitType {

    NONE(new Kit("Jogue sem nenhum kit!", null, Material.BARRIER, 0, Group.MEMBRO, 0L) {
        @Override
        public List<ItemStack> getSpecialItem() {
            return null;
        }

        @Override
        public String getName() {
            return "Nenhum";
        }
    }),

    KANGAROO(new Kangaroo()),
    NEO(new Neo()),
    STOMPER(new Stomper()),
    ANTISTOMPER(new AntiStomper()),
    BOXER(new Boxer()),
    SNAIL(new Snail()),
    VIPER(new Viper()),
    MAGMA(new Magma()),
    GLADIATOR(new Gladiator()),
    NINJA(new Ninja()),
    SWITCHER(new Switcher()),
    MONK(new Monk()),
    GRAPPLER(new Grappler()),
    FISHERMAN(new Fisherman()),
    ARCHER(new Archer()),
    SPECIALIST(new Specialist()),
    THOR(new Thor()),
    METEOR(new Meteor()),
    HULK(new Hulk()),
    VACUUM(new Vacuum()),
    GRANDPA(new Grandpa());

    private final Kit kit;
}