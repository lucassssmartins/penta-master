package br.com.pentamc.pvp.command;

import br.com.pentamc.bukkit.command.BukkitCommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.game.Game;
import br.com.pentamc.pvp.user.User;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandClass {

    @CommandFramework.Command(name = "spawn")
    public void spawnCommand(BukkitCommandArgs commandArgs) {
        if (!commandArgs.isPlayer())
            return;

        Player player = commandArgs.getPlayer();

        User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());
        Game game = user.getGame();

        if (GameMain.getPlugin().getCombatController().read(player.getUniqueId()) != null) {
            player.sendMessage("§cVocê não pode fazer isso em combate.");
            return;
        }

        game.load(player);
        user.setProtected(true);

        player.sendMessage("§aVocê foi teleportado ao spawn.");
    }
}
