package br.com.pentamc.pvp.inventory;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.bukkit.api.menu.MenuItem;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.pvp.PvPStatus;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.event.KitEvent;
import br.com.pentamc.pvp.inventory.type.InventoryType;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class KitInventory extends MenuInventory {

    protected final User user;
    protected final InventoryType inventoryType;

    protected int page;

    public KitInventory(Player player, InventoryType inventoryType, int page) {
        super("Selecionar kit " + (inventoryType.ordinal() + 1), 6, true);

        user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());
        this.inventoryType = inventoryType;
        this.page = page;

        handle();
        open(player);
    }

    public void handle() {
        clear();

        BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(user.getUniqueId());
        PvPStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(user.getUniqueId(), StatusType.PVP, PvPStatus.class);

        List<MenuItem> items = new ArrayList<>();
        List<KitType> kits = Arrays.stream(KitType.values()).sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        KitType kitNone = KitType.NONE;
        int indexOfKitToMove = kits.indexOf(kitNone);

        if (indexOfKitToMove != -1)
            Collections.rotate(kits, -indexOfKitToMove);

        for (KitType kitType : kits) {
            Kit kit = kitType.getKit();

            switch (inventoryType) {
                case PRIMARY: {
                    ItemStack stack = new ItemBuilder()
                            .type(kit.getIcon())
                            .name("§a" + kit.getName())
                            .lore(
                                    "§7" + kit.getLore() +
                                         "\n\n" +
                                         "§aClique para selecionar!"
                            ).build();

                    items.add(new MenuItem(stack, (p, inv, type, stack1, slot) -> {
                        close(p);

                        Bukkit.getPluginManager().callEvent(new KitEvent(user, inventoryType, kitType));
                        return false;
                    }));

                    break;
                }

                case SECONDARY: {
                    if (status.getBattle().containsKit(kit.getName()) || member.hasGroupPermission(kit.getGroup()) || GameMain.getPlugin().getKitRotation().contains(kitType)) {
                        ItemStack stack = new ItemBuilder()
                                .type(kit.getIcon())
                                .name("§a" + kit.getName())
                                .lore(
                                        "§7" + kit.getLore() +
                                        "\n\n" +
                                        "§aVOCÊ POSSUI" +
                                        "\n\n" +
                                        "§aClique para selecionar!"
                                ).build();

                        items.add(new MenuItem(stack, (p, inv, type, stack1, slot) -> {
                            close(p);

                            Bukkit.getPluginManager().callEvent(new KitEvent(user, inventoryType, kitType));
                            return false;
                        }));
                    } else {
                        ItemStack stack = new ItemBuilder()
                                .type(Material.STAINED_GLASS_PANE)
                                .durability(14)
                                .name("§a" + kit.getName())
                                .lore(
                                        "§7" + kit.getLore() +
                                        "\n\n" +
                                        "§cVOCÊ NÃO POSSUI"
                                ).build();

                        items.add(new MenuItem(stack, (p, inv, type, stack1, slot) -> false));
                    }
                }
            }
        }


        if (page != 1) {
            setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page - 1)).build(),
                    (p, inventory, clickType, item, slot) -> {
                        new KitInventory(p, inventoryType, page - 1);
                        return false;
                    }), 45);
        }

        int pageStart = 0;
        int pageEnd = 21;

        if (page > 1) {
            pageStart = ((page - 1) * 21);
            pageEnd = (page * 21);
        }

        if (pageEnd > items.size()) {
            pageEnd = items.size();
        }

        int w = 10;

        for (int i = pageStart; i < pageEnd; i++) {
            MenuItem item = items.get(i);
            setItem(item, w);

            if (w % 9 == 7) {
                w += 3;
                continue;
            }

            w += 1;
        }

        if (Math.ceil(items.size() / 21) + 1 > page) {
            setItem(
                    new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(),
                            (p, inventory, clickType, item, slot) -> {
                                new KitInventory(p, inventoryType, page + 1);
                                return false;
                            }), 53);
        }
    }
}
