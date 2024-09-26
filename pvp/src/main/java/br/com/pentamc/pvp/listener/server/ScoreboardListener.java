package br.com.pentamc.pvp.listener.server;

import br.com.pentamc.bukkit.api.scoreboard.Score;
import br.com.pentamc.bukkit.api.scoreboard.Scoreboard;
import br.com.pentamc.bukkit.api.title.types.SimpleTitle;
import br.com.pentamc.pvp.event.KitEvent;
import br.com.pentamc.pvp.kit.Kit;
import br.com.pentamc.pvp.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.github.paperspigot.Title;

import java.util.Arrays;

public class ScoreboardListener implements Listener {

    protected final String[] BLOCKED_SCHEMES = {
            "kangaroo:grappler", "kangaroo:stomper", "grappler:stomper", "grappler:kangaroo", "stomper:kangaroo", "stomper:grappler",
            "meteor:switcher", "meteor:stomper", "meteor:grappler", "meteor:kangaroo", "switcher:meteor", "stomper:meteor", "grappler:meteor", "kangaroo:meteor",
            "stomper:ninja", "ninja:stomper"
    };

    @EventHandler
    public void kitUpdater(KitEvent event) {
        User user = event.getUser();
        Player player = Bukkit.getPlayer(user.getUniqueId());

        if (!user.isProtected()) {
            player.sendMessage("§cVocê só pode selecionar kits no spawn.");
            return;
        }

        if (user.isUsing(event.getType())) {
            player.sendMessage("§cVocê já está usando esse kit.");
            return;
        }

        Scoreboard scoreboard = user.getScoreboard();
        Kit kit = event.getType().getKit();

        switch (event.getLocation()) {
            case PRIMARY: {
                if (user.getKitTwo() != null) {
                    String currentKit = kit.getName() + ":" + user.getKitTwo().getKit().getName();

                    if (Arrays.stream(BLOCKED_SCHEMES).anyMatch(currentKit::equalsIgnoreCase)) {
                        player.sendMessage("§cEstá combinação de kits está bloqueada!");
                        return;
                    }
                }

                user.setKitOne(event.getType());
                scoreboard.updateScore(player, new Score("Kit 1: §a" + kit.getName(), "kit1"));

                player.sendMessage("§aVocê selecionou o kit " + kit.getName() + " como kit primário.");
                new SimpleTitle("§b" + kit.getName(), "§aSelecionado!").send(player);
                break;
            }

            case SECONDARY: {
                if (user.getKitOne() != null) {
                    String currentKit = kit.getName() + ":" + user.getKitOne().getKit().getName();

                    if (Arrays.stream(BLOCKED_SCHEMES).anyMatch(currentKit::equalsIgnoreCase)) {
                        player.sendMessage("§cEstá combinação de kits está bloqueada!");
                        return;
                    }
                }

                user.setKitTwo(event.getType());
                scoreboard.updateScore(player, new Score("Kit 2: §a" + kit.getName(), "kit2"));

                player.sendMessage("§aVocê selecionou o kit " + kit.getName() + " como kit secundário.");
                new SimpleTitle("§b" + kit.getName(), "§aSelecionado!").send(player);
                break;
            }
        }
    }
}
