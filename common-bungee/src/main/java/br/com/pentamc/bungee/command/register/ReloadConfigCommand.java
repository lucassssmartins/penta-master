package br.com.pentamc.bungee.command.register;

import br.com.pentamc.bungee.BungeeMain;
import br.com.pentamc.bungee.command.BungeeCommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.common.permission.Group;

public class ReloadConfigCommand implements CommandClass {

    @CommandFramework.Command(name = "reloadconfig", usage = "/reloadconfig", groupToUse = Group.ADMIN, runAsync = true)
    public void groupsetCommand(BungeeCommandArgs cmdArgs) {
        BungeeMain.getInstance().reloadConfig();
        cmdArgs.getSender().sendMessage("§a(BungeeCord) Config.yml recarregado!");
    }
}
