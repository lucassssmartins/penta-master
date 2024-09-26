package br.com.pentamc.lobby.menu.server;

import java.util.List;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.bukkit.api.menu.MenuUpdateHandler;
import br.com.pentamc.lobby.menu.server.ServerInventory.SendClick;

public class LobbyInventory {

    public LobbyInventory(Player player) {
        Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
        List<ProxiedServer> serverList = BukkitMain.getInstance().getServerManager()
                                                   .getBalancer(CommonGeneral.getInstance().getServerType()).getList();

        MenuInventory menu = new MenuInventory("Salas",
                                               2 + (serverList.size() == 0 ? 1 : (serverList.size() / 7) + 1));

        serverList.sort((o1, o2) -> Integer.valueOf(o1.getOnlinePlayers()).compareTo(o2.getOnlinePlayers()));

        create(serverList, member, menu);

        menu.setUpdateHandler(new MenuUpdateHandler() {

            @Override
            public void onUpdate(Player player, MenuInventory menu) {
                create(serverList, member, menu);
            }
        });

        menu.open(player);
    }

    private void create(List<ProxiedServer> serverList, Member member, MenuInventory menu) {
        int w = 10;

        for (ProxiedServer server : serverList) {
            ItemBuilder builder = new ItemBuilder();
            builder.type(Material.STAINED_GLASS_PANE);

            if (CommonGeneral.getInstance().getServerId().equalsIgnoreCase(server.getServerId())) {
                builder.glow();
            }

            builder.name((server.isFull() ? "§e" : "§a") + "Lobby principal #" +
                         server.getServerId().substring(1, 2).toUpperCase());
            builder.durability(server.isFull() ? 7 : 5);
            builder.lore("", "§8" + server.getOnlinePlayers() + " jogadores conectados.",
                         (server.isFull() ? "§c" : "§a") + "Clique para entrar.");

            if (w % 9 == 8) {
                w += 2;
            }
            menu.setItem(w, builder.build(),
                         member.hasGroupPermission(Group.VIP) ? new SendClick(server.getServerId()) :
                         (p, inv, type, stack, slot) -> {
                             p.sendMessage("§cSomente §lVIP§c podem trocar de lobby!");
                             p.closeInventory();
                             return false;
                         });
            w++;
        }
    }
}
