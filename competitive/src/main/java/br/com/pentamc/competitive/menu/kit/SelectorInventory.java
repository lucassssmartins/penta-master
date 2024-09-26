package br.com.pentamc.competitive.menu.kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.pentamc.competitive.utils.ServerConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.kit.Kit;
import br.com.pentamc.competitive.kit.KitType;
import lombok.AllArgsConstructor;
import br.com.pentamc.common.CommonConst;
import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.bukkit.api.menu.MenuItem;
import br.com.pentamc.bukkit.api.menu.click.ClickType;
import br.com.pentamc.bukkit.api.menu.click.MenuClickHandler;
import br.com.pentamc.common.utils.string.NameUtils;

@AllArgsConstructor
public class SelectorInventory {

    private static int itemsPerPage = 21;

    public SelectorInventory(Player player, int page, KitType kitType, OrderType orderType) {
        Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
        MenuInventory menu = new MenuInventory("Selecionar kit", 6, true);
        List<Kit> kits = new ArrayList<>(GameGeneral.getInstance().getKitController().getAllKits());

        Comparator<Kit> comparator = orderType.getComparator(gamer, kitType);

        kits.sort(comparator);

        List<MenuItem> items = new ArrayList<>();

        for (Kit kit : kits) {
            if (ServerConfig.getInstance().isDisabled(kit, kitType)) {
                continue;
            }

            boolean hasKit = gamer.hasKit(kit.getName());

            if (hasKit) {
                items.add(new MenuItem(
                        new ItemBuilder().lore("§7" + kit.getDescription() + "\n\n§aClique para selecionar!")
                                         .type(kit.getKitIcon().getType()).durability(kit.getKitIcon().getDurability())
                                         .name("§a" + NameUtils.formatString(kit.getName())).build(),
                        new OpenKitMenu(kit, kitType)));
            } else {
                ItemStack item = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14)
                                                  .name("§c" + NameUtils.formatString(kit.getName()))
                                                  .lore("\n§cVocê não possui este kit!\n§cCompre em: §e" +
                                                        CommonConst.STORE + "\n\n§7" + kit.getDescription() +
                                                        "\n\n§aClique para selecionar!").build();
                items.add(new MenuItem(item, new StoreKitMenu(kit)));
            }
        }

        int pageStart = 0;
        int pageEnd = itemsPerPage;

        if (page > 1) {
            pageStart = ((page - 1) * itemsPerPage);
            pageEnd = (page * itemsPerPage);
        }

        if (pageEnd > items.size()) {
            pageEnd = items.size();
        }

        int w = 10;

        for (int i = pageStart; i < pageEnd; i++) {
            MenuItem item = items.get(i);
            menu.setItem(item, w);

            if (w % 9 == 7) {
                w += 3;
                continue;
            }

            w += 1;
        }

        if (page != 1) {
            menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page - 1)).build(),
                                      new MenuClickHandler() {

                                          @Override
                                          public boolean onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
                                              new SelectorInventory(arg0, page - 1, kitType, orderType);
                                              return false;
                                          }
                                      }), 45);
        }

        if (Math.ceil(items.size() / itemsPerPage) + 1 > page) {
            menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(),
                                      (p, inventory, clickType, item, slot) -> {
                                          new SelectorInventory(p, page + 1, kitType, orderType);
                                          return false;
                                      }), 53);
        }

        menu.open(player);
    }

    @AllArgsConstructor
    public static class OpenKitMenu implements MenuClickHandler {

        private Kit kit;
        private KitType kitType;

        @Override
        public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
            if (type == ClickType.RIGHT) {
                new InfoInventory(p, kit, kitType);
                return false;
            }

            GameGeneral.getInstance().getKitController().selectKit(p, kit, kitType);
            p.closeInventory();
            return false;
        }
    }

    @AllArgsConstructor
    public static class StoreKitMenu implements MenuClickHandler {

        private Kit kit;

        @Override
        public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
            p.sendMessage(
                    "§6§l> §fCompre o kit §a" + NameUtils.formatString(kit.getName()) + "§f em §a" + CommonConst.STORE +
                    "§f!");
            return false;
        }
    }

    public enum OrderType {

        MINE,
        ALPHABET,
        DE_ALPHABET;

        Comparator<Kit> getComparator(Gamer gamer, KitType kitType) {
            switch (this) {
            case MINE: {
                return new Comparator<Kit>() {

                    @Override
                    public int compare(Kit o1, Kit o2) {
                        boolean hasKitO1 = gamer.hasKit(o1.getName());
                        boolean hasKitO2 = gamer.hasKit(o2.getName());

                        int value1 = Boolean.compare(hasKitO2, hasKitO1);

                        if (value1 == 0) {
                            return o1.getName().compareTo(o2.getName());
                        }

                        return value1;
                    }
                };
            }
            case DE_ALPHABET: {
                return (kit1, kit2) -> kit2.getName().compareTo(kit1.getName());
            }
            default: {
                return Comparator.comparing(kit -> kit.getName());
            }
            }
        }
    }
}
