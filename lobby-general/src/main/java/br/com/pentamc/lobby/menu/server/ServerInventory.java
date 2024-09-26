package br.com.pentamc.lobby.menu.server;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.server.ServerType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.RequiredArgsConstructor;
import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.bukkit.api.menu.MenuUpdateHandler;
import br.com.pentamc.bukkit.api.menu.click.ClickType;
import br.com.pentamc.bukkit.api.menu.click.MenuClickHandler;

public class ServerInventory {

    public static boolean LOBBY_HG = true;

    public ServerInventory(Player player) {
        MenuInventory menuInventory = new MenuInventory("Jogos", 3);

        createItens(player, menuInventory);

        menuInventory.setUpdateHandler(new MenuUpdateHandler() {

            @Override
            public void onUpdate(Player player, MenuInventory menu) {
                createItens(player, menuInventory);
            }
        });

        menuInventory.open(player);
    }

    public void createItens(Player player, MenuInventory menuInventory) {
        menuInventory.setItem(10, new ItemBuilder().name("§aPvP").type(Material.IRON_CHESTPLATE)
                        .lore("§7" + getTotalNumber(ServerType.LOBBY_PVP, ServerType.PVP) + " jogando.").build(),
                (p, inv, type, stack, slot) -> {
                    BukkitMain.getInstance().sendServer(player, ServerType.LOBBY_PVP);
                    return false;
                });

        menuInventory.setItem(11, new ItemBuilder().name("§aCompetitivo").type(Material.MUSHROOM_SOUP).lore("§7" +
                                                                                                            getTotalNumber(
                                                                                                                    ServerType.LOBBY_HG,
                                                                                                                    ServerType.EVENTO,
                                                                                                                    ServerType.HUNGERGAMES) +
                                                                                                            " jogando.")
                                                   .build(), (p, inv, type, stack, slot) -> {
            BukkitMain.getInstance().sendServer(player, ServerType.LOBBY_HG);
            return false;
        });

        menuInventory.setItem(12, new ItemBuilder().name("§aDuels").type(Material.BLAZE_ROD)
                                                   .lore("§7" + getTotalNumber(ServerType.LOBBY_DUELS, ServerType.GLADIATOR, ServerType.SIMULATOR, ServerType.ONEXONE) + " jogando.").build(),
                              (p, inv, type, stack, slot) -> {
                                  BukkitMain.getInstance().sendServer(player, ServerType.LOBBY_DUELS);
                                  return false;
        });
    }

    public int getTotalNumber(ServerType... serverTypes) {
        return BukkitMain.getInstance().getServerManager().getTotalNumber(serverTypes);
    }

    @RequiredArgsConstructor
    public static class SendClick implements MenuClickHandler {

        private final String serverId;

        @Override
        public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
            BukkitMain.getInstance().sendPlayer(p, serverId);
            return false;
        }
    }
}
