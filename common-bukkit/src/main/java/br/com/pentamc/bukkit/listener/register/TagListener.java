package br.com.pentamc.bukkit.listener.register;

import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.account.League;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.clan.enums.ClanDisplayType;
import br.com.pentamc.common.server.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import br.com.pentamc.bukkit.api.scoreboard.ScoreboardAPI;
import br.com.pentamc.bukkit.api.tag.Chroma;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.event.account.PlayerChangeTagEvent;

public class TagListener implements Listener {

    private BukkitMain main;
    private ChromaListener listener;

    public TagListener() {
        main = BukkitMain.getInstance();
        listener = new ChromaListener();

        if (!BukkitMain.getInstance().isTagControl()) {
            HandlerList.unregisterAll(this);
            return;
        }

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

            if (player == null) {
                continue;
            }

            player.setTag(player.getTag());
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(p);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!main.isTagControl()) {
            return;
        }

        Player p = e.getPlayer();

        BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                          .getMember(e.getPlayer().getUniqueId());

        if (player == null) {
            p.kickPlayer("§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§fNão foi possível carregar sua conta!");
            return;
        }

        player.setTag(player.getTag() == null ? player.getDefaultTag() : player.getTag());
        StatusType statusType = CommonGeneral.getInstance().getServerType() == ServerType.ONEXONE ? StatusType.SHADOW : StatusType.HG;

        for (Player o : Bukkit.getOnlinePlayers()) {
            if (!o.getUniqueId().equals(p.getUniqueId())) {
                BukkitMember bp = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                              .getMember(o.getUniqueId());

                if (bp == null) {
                    continue;
                }

                String id = ScoreboardAPI.getTeamName(bp.getTag(), bp.getLeague(statusType),
                                                      bp.getTag().isChroma() || bp.isChroma(), isClanTag(bp),
                                                      bp.getClan());

                String tag = BukkitMain.getInstance().isOldTag() ? ChatColor.getLastColors(bp.getTag().getPrefix())
                                                                 : bp.getTag().getPrefix();
                String suffix = CommonGeneral.getInstance().getServerType() == ServerType.ONEXONE || CommonGeneral
                        .getInstance()
                        .getServerType().name().contains("HG") ? " " + bp.getLeague(statusType).getSimplifiedName() : "";

                ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(p, id,
                                                                                   tag +
                                                                                   (ChatColor.stripColor(tag).trim()
                                                                                             .length() > 0 ? " " : ""),
                                                                                   suffix), o);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangeTag(PlayerChangeTagEvent event) {
        if (!main.isTagControl()) {
            return;
        }

        Player p = event.getPlayer();
        BukkitMember player = (BukkitMember) event.getMember();

        if (player == null) {
            return;
        }

        StatusType statusType = CommonGeneral.getInstance().getServerType() == ServerType.ONEXONE ? StatusType.SHADOW : StatusType.HG;

        String id = ScoreboardAPI.getTeamName(event.getNewTag(),
                                              player.isUsingFake() ? League.values()[0] : player.getLeague(statusType),
                                              event.getNewTag().isChroma() || player.isChroma(), event.isClanTag(),
                                              player.getClan());
        String oldId = ScoreboardAPI.getTeamName(event.getOldTag(),
                                                 player.isUsingFake() ? League.values()[0] : player.getLeague(statusType),
                                                 event.getNewTag().isChroma() || player.isChroma(), event.isClanTag(),
                                                 player.getClan());

        String tag = BukkitMain.getInstance().isOldTag() ? ChatColor.getLastColors(event.getNewTag().getPrefix())
                                                         : event.getNewTag().getPrefix();
        String suffix = CommonGeneral.getInstance().getServerType() == ServerType.ONEXONE || CommonGeneral
                .getInstance()
                .getServerType().name().contains("HG") ? " " + player.getLeague(statusType).getSimplifiedName() : "";

        if (event.getOldTag().isChroma() || player.isChroma()) {
            listener.getChromaList().remove(new Chroma(id, tag, suffix));
        }

        if (event.getNewTag().isChroma() || player.isChroma()) {
            if (!listener.getChromaList().contains(new Chroma(id, tag, suffix))) {
                listener.getChromaList().add(new Chroma(id, tag, suffix));
            }
        }

        /**
         * TICKs++
         */

        if (listener.getChromaList().isEmpty()) {
            listener.unregisterListener();
        } else {
            listener.registerListener();
        }

        for (Player o : Bukkit.getOnlinePlayers()) {
            try {
                ScoreboardAPI.leaveTeamToPlayer(o, oldId, p);
                ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(o, id,
                                                                                   tag +
                                                                                   (ChatColor.stripColor(tag).trim()
                                                                                             .length() > 0 ? " " : ""),
                                                                                   suffix), p);
            } catch (Exception ex) {
            }
        }
    }

    public boolean isClanTag(Member member) {
        if (member.isUsingFake()) {
            return false;
        }

        if (member.getAccountConfiguration().getClanDisplayType() == ClanDisplayType.ALL
            || (CommonGeneral.getInstance().getServerType() == ServerType.LOBBY
                && member.getAccountConfiguration().getClanDisplayType() == ClanDisplayType.LOBBY)) {
            return member.getClan() != null;
        }
        return false;
    }
}
