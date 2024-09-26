package br.com.pentamc.pvp.command;

import br.com.pentamc.bukkit.command.BukkitCommandArgs;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.pvp.PvPStatus;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.event.KitEvent;
import br.com.pentamc.pvp.inventory.type.InventoryType;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class KitCommand implements CommandClass {

    @CommandFramework.Command(name = "kit", aliases = {"kit1"})
    public void kitOneCommand(BukkitCommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();

        Player player = commandArgs.getPlayer();
        User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());

        if (args.length < 1) {
            player.sendMessage(new String[] {
                    "§cUso do /" + commandArgs.getLabel() + ":",
                    "§c* /" + commandArgs.getLabel() + " <name>"
            });

            return;
        }

        PvPStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.PVP, PvPStatus.class);

        KitType kit = Arrays.stream(KitType.values())
                .filter(k -> k.getKit().getName().equalsIgnoreCase(args[0]))
                .findFirst()
                .orElse(null);

        if (kit == null) {
            player.sendMessage("§cKit não encontrado.");
            return;
        }

        if (!status.getBattle().containsKit(kit.name())) {
            player.sendMessage("§cVocê não possui este kit.");
            return;
        }

        Bukkit.getPluginManager().callEvent(new KitEvent(user, InventoryType.PRIMARY, kit));
    }

    @CommandFramework.Command(name = "kit2")
    public void kitTwoCommand(BukkitCommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();

        Player player = commandArgs.getPlayer();
        User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());

        if (args.length < 1) {
            player.sendMessage(new String[] {
                    "§cUso do /" + commandArgs.getLabel() + ":",
                    "§c* /" + commandArgs.getLabel() + " <name>"
            });

            return;
        }

        PvPStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.PVP, PvPStatus.class);

        KitType kit = Arrays.stream(KitType.values())
                .filter(k -> k.getKit().getName().equalsIgnoreCase(args[0]))
                .findFirst()
                .orElse(null);

        if (kit == null) {
            player.sendMessage("§cKit não encontrado.");
            return;
        }

        if (!status.getBattle().containsKit(kit.name())) {
            player.sendMessage("§cVocê não possui este kit.");
            return;
        }

        Bukkit.getPluginManager().callEvent(new KitEvent(user, InventoryType.SECONDARY, kit));
    }
}
