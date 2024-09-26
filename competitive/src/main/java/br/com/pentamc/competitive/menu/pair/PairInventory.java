package br.com.pentamc.competitive.menu.pair;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.bukkit.api.menu.MenuItem;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.game.Team;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PairInventory extends MenuInventory {

    private Player player;

    private int
            page,
            index = 1;

    private final int itemsPerPage = 21;

    public PairInventory(Player player, int page) {
        super("Duplas", 6);

        this.player = player;
        this.page = page;

        handle(player);
    }

    public void handle(Player player) {
        clear();

        List<Team> teams = GameMain.getInstance().getAliveTeams();
        List<MenuItem> items = new ArrayList<>();

        for (Team team : teams) {
            Player
                    t1 = team.getParticipantsAsPlayer().get(0),
                    t2 = null;

            if (team.getParticipantsAsPlayer().size() > 1)
                t2 = team.getParticipantsAsPlayer().get(1);

            Gamer t1Gamer = GameMain.getInstance().getGeneral().getGamerController().getGamer(t1),
                    t2Gamer = (t2 == null ? null : GameMain.getInstance().getGeneral().getGamerController().getGamer(t2));

            ItemStack stack = new ItemBuilder()
                    .type(Material.SKULL_ITEM)
                    .durability(3)
                    .skin((t1Gamer == null ? t2.getName() : t1.getName()))
                    .name("§aDupla " + index)
                    .lore(
                            "§71. " + (t1Gamer == null ? "Ninguém" : t1.getName()),
                            "§72. " + (t2Gamer == null ? "Ninguém" : t2.getName())
                    ).build();

            TextComponent component = new TextComponent("§b§lCLIQUE AQUI§e para copiar o nome da dupla!");

            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, (t1Gamer == null ? "" : t1.getName()) + (t2Gamer == null ? "" : (t1Gamer == null ? "" : ", ") + t2.getName())));

            items.add(new MenuItem(stack, (p, inv, type, stack1, slot) -> {
                close(p);

                p.spigot().sendMessage(component);
                return false;
            }));

            index++;
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
            setItem(item, w);

            if (w % 9 == 7) {
                w += 3;
                continue;
            }

            w += 1;
        }

        if (page != 1) {
            setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page - 1)).build(),
                    (arg0, arg1, arg2, arg3, arg4) -> {
                        new PairInventory(player, page - 1);
                        return false;
                    }), 45);
        }

        if (Math.ceil(items.size() / itemsPerPage) + 1 > page) {
            setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(),
                    (p, inventory, clickType, item, slot) -> {
                        new PairInventory(player, page + 1);
                        return false;
                    }), 53);
        }

        open(player);
    }
}
