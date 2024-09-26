package br.com.pentamc.pvp.kit;

import br.com.pentamc.bukkit.api.cooldown.CooldownController;
import br.com.pentamc.bukkit.api.cooldown.types.Cooldown;
import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.utils.string.NameUtils;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.game.type.GameType;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public abstract class Kit {

    private final String
            name = getClass().getSimpleName(),
            lore;

    private final KitType type;
    private final Material icon;

    private final int price;
    private final Group group;

    private final long cooldown;

    public abstract List<ItemStack> getSpecialItem();

    public User getUser(UUID uniqueId) {
        return GameMain.getPlugin().getUserController().getValue(uniqueId);
    }

    public boolean isAvailable(User user) {
        return user.getGame().getType().equals(GameType.BATTLE) && user.isUsing(getType());
    }

    public boolean isUsingNeo(UUID uniqueId) {
        User user = getUser(uniqueId);

        return user != null && user.isUsingNeo();
    }

    public boolean isCooldown(Player player) {
        if (CooldownController.getInstance().hasCooldown(player.getUniqueId(), NameUtils.formatString(getName()))) {
            Cooldown cooldown = CooldownController.getInstance().getCooldown(player.getUniqueId(),NameUtils.formatString(getName()));

            if (cooldown == null)
                return false;

            String message = "§cAguarde " + CommonConst.DECIMAL_FORMAT.format(cooldown.getRemaining())
                    + "s para usar o Kit " + NameUtils.formatString(getName()) + " novamente!";

            player.sendMessage(message);
            return true;
        }

        return false;
    }

    public void addCooldown(Player player, long time) {
        CooldownController.getInstance().addCooldown(player.getUniqueId(), getName(), time);
    }
}