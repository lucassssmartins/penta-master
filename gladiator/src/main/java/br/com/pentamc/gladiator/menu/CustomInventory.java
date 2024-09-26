package br.com.pentamc.gladiator.menu;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.gladiator.menu.custom.Custom;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomInventory extends MenuInventory {

    private final Player human;
    private final Member member;

    protected ItemStack[] itemStacks;

    public CustomInventory(Player human, Member member) {
        super("Editar: Gladiator", 6);

        this.human = human;
        this.member = member;

        itemStacks = Custom.getContents(member.getUniqueId());

        setDragHandler((a, b, c, d, e, f) -> true);
        setReopenInventory(false);

        handle();
    }

    protected void handle() {
        clear();

        for (int i = 9; i < 36; i++) {
            setItem(i - 9, itemStacks[i] == null ? new ItemStack(Material.AIR) : itemStacks[i], (p, inv, type, stack, slot) -> true);
        }

        for (int i = 27; i < 36; i++) {
            setItem(i, new ItemBuilder()
                    .name("§r")
                    .type(Material.STAINED_GLASS_PANE)
                    .durability(11)
                    .build());
        }

        for (int i = 0; i < 9; i++) {
            setItem(36 + i, itemStacks[i] == null ? new ItemStack(Material.AIR) : itemStacks[i], (p, inv, type, stack, slot) -> true);
        }

        setItem(48, new ItemBuilder()
                        .type(Material.STAINED_CLAY)
                        .durability(14)
                        .name("§cCancelar")
                        .build(),
                (human, inventory, type, stack, slot) -> {
                    if (human.getItemOnCursor() != null || human.getItemInHand() != null) {
                        human.setItemOnCursor(null);
                        human.setItemInHand(null);
                    }

                    close(human);

                    human.sendMessage("§cInventário cancelado com sucesso!");
                    return false;
                }
        );

        setItem(50, new ItemBuilder()
                        .type(Material.STAINED_CLAY)
                        .durability(5)
                        .name("§aConfirmar")
                        .build(),
                (human, inventory, type, stack, slot) -> {
                    if (human.getItemOnCursor() != null || human.getItemInHand() != null) {
                        human.setItemOnCursor(null);
                        human.setItemInHand(null);
                    }

                    if (member == null) {
                        return false;
                    }

                    ItemStack[] stacks = new ItemStack[36];

                    for (int i = 0; i < 36; i++) {
                        stacks[i] = i >= 9 ? inventory.getItem(i - 9) : inventory.getItem(i + 36);
                    }

                    member.setGladiatorInventory(Custom.translate(stacks));
                    close(human);

                    human.sendMessage("§aInventário salvo com sucesso!");
                    return false;
                }
        );

        open(human);
    }
}
