package br.com.pentamc.pvp.inventory;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.bukkit.api.menu.MenuItem;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.pvp.PvPStatus;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.kit.type.KitType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ShopInventory extends MenuInventory {

    protected final Player player;
    protected int page;

    public ShopInventory(Player player, int page) {
        super("Loja de kits", 6);

        this.player = player;
        this.page = page;

        handle();
    }

    public void handle() {
        clear();

        BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
        PvPStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.PVP, PvPStatus.class);

        List<MenuItem> items = new ArrayList<>();
        List<KitType> kits = Arrays.stream(KitType.values()).sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        for (KitType type : kits) {
            if (type.equals(KitType.NONE))
                continue;

            if (member.hasGroupPermission(type.getKit().getGroup()) || status.getBattle().containsKit(type.name().toLowerCase()))
                continue;

            Kit kit = type.getKit();

            ItemStack stack = new ItemBuilder()
                    .type(kit.getIcon())
                    .name("§a" + kit.getName())
                    .lore(
                            "§7" + kit.getLore() +
                            "\n\n" +
                            "§7Preço: §6" + kit.getPrice() +
                            "\n\n" +
                            (status.getCoins() >= kit.getPrice() ? "§aClique para comprar!" : "§cVocê não tem coins suficientes!")
                    ).build();

            items.add(new MenuItem(stack, (human, inventory, clickType, item, slot) -> {
                close(human);

                if (status.getCoins() >= kit.getPrice()) {
                    status.removeCoins(kit.getPrice());
                    status.getBattle().addKit(type.name());

                    status.save("battle");

                    human.sendMessage("§aVocê comprou o kit " + kit.getName() + " com sucesso.");
                } else {
                    human.sendMessage("§cVocê não tem coins suficientes!");
                }

                return false;
            }));
        }

        if (!items.isEmpty()) {
            if (page != 1) {
                setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page - 1)).build(),
                        (p, inventory, clickType, item, slot) -> {
                            new ShopInventory(p, page - 1);
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
                                    new ShopInventory(p, page + 1);
                                    return false;
                                }), 53);
            }
        } else {
            setItem(
                    new MenuItem(new ItemBuilder().type(Material.BARRIER).name("§cOps! Parece que você já tem todos os kits.").build(),
                            (p, inventory, clickType, item, slot) -> false),
                    22);
        }

        open(player);
    }
}
