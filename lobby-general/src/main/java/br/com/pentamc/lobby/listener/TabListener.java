package br.com.pentamc.lobby.listener;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.tag.Tag;
import br.com.pentamc.lobby.LobbyPlatform;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.pentamc.bukkit.api.tablist.Tablist;
import br.com.pentamc.bukkit.event.account.PlayerChangeGroupEvent;
import br.com.pentamc.bukkit.event.account.PlayerChangeLeagueEvent;

public class TabListener implements Listener {

    private Tablist tablist;

    public TabListener() {
        tablist = new Tablist("§f\n§b§l" + CommonConst.SERVER_NAME.toUpperCase() + "\n§f",
                              "\n§bLoja: §f" + CommonConst.SITE + "\n§bDiscord: §f" +
                              CommonConst.DISCORD.replace("http://", "") + "\n§f ") {

            @Override
            public String[] replace(Player player, String header, String footer) {
                Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

                header = header.replace("%group%", member.getGroup() == Group.MEMBRO ? "§7§lMEMBRO" :
                                                   Tag.valueOf(member.getGroup().name()).getPrefix());
                header = header.replace("%name%", member.getPlayerName());

                footer = footer.replace("%name%", member.getPlayerName());
                footer = footer.replace(".br/", "");

                return new String[]{header, footer};
            }
        };
    }

    @EventHandler
    public void onPlayerChangeGroup(PlayerChangeGroupEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                tablist.updateTab(event.getPlayer());
            }
        }.runTaskLater(LobbyPlatform.getInstance(), 10l);
    }

    @EventHandler
    public void onPlayerChangeLeague(PlayerChangeLeagueEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                tablist.updateTab(event.getPlayer());
            }
        }.runTaskLater(LobbyPlatform.getInstance(), 10l);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        tablist.addViewer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        tablist.removeViewer(e.getPlayer());
    }
}
