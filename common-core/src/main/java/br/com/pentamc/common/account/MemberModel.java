package br.com.pentamc.common.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.permission.RankType;
import br.com.pentamc.common.profile.Profile;
import com.google.gson.JsonObject;
import lombok.Getter;
import br.com.pentamc.common.account.configuration.AccountConfiguration;
import br.com.pentamc.common.account.configuration.LoginConfiguration;
import br.com.pentamc.common.account.medal.Medal;
import br.com.pentamc.common.ban.PunishmentHistory;
import br.com.pentamc.common.server.ServerType;
//import tk.yallandev.saintmc.discord.account.DiscordType;
import br.com.pentamc.common.tag.Tag;

@Getter
public class MemberModel {

    /*
     * Player Information
     *
     */

    private String playerName;
    private final UUID uniqueId;

    /*
     * Skin Information
     *
     */

    private Profile skinProfile;

    private String fakeName;
    private Map<String, Long> cooldown;

    private String lastIpAddress;

    /*
     * Configuration
     *
     */

    private AccountConfiguration accountConfiguration;
    private LoginConfiguration loginConfiguration;

    /*
     * History
     *
     */

    private PunishmentHistory punishmentHistory;

    /*
     * Social Midia
     *
     */

    private Long discordId;
    private String discordName;
    private DiscordType discordType;

    private UUID clanUniqueId;

    /*
     * Permission Information
     *
     */

    private Group group;
    private Map<RankType, Long> ranks;
    private Map<String, Long> permissions;

    private Tag tag;
    private boolean chroma;

    private List<Medal> medalList;
    private Medal medal;

    /*
     * Tournament
     *
     */

    private TournamentGroup tournamentGroup;

    /*
     * Status Information
     *
     */

    private int money;
    private int cash;
    private int xp;
    private int position;
    private League league;

    private int reputation;

    /*
     * Player time
     *
     */

    private long firstLogin;
    private long lastLogin;
    private long joinTime;
    private long onlineTime;

    /*
     * Server Info
     *
     */

    private String serverId = "";
    private ServerType serverType;

    private String lastServerId;
    private ServerType lastServerType;

    private boolean online;
    private JsonObject gladiatorInventory;

    public MemberModel(Member member) {
        playerName = member.getPlayerName();
        uniqueId = member.getUniqueId();

        skinProfile = member.getSkinProfile();

        fakeName = member.getFakeName();
        cooldown = member.getCooldown();

        lastIpAddress = member.getLastIpAddress();

        accountConfiguration = member.getAccountConfiguration();
        loginConfiguration = member.getLoginConfiguration();

        punishmentHistory = member.getPunishmentHistory();

        discordId = member.getDiscordId();
        discordName = member.getDiscordName();
        discordType = member.getDiscordType();

        clanUniqueId = member.getClanUniqueId();

        group = member.getGroup();
        permissions = member.getPermissions();
        ranks = member.getRanks();

        tag = member.getTag();
        chroma = member.isChroma();

        medalList = member.getMedalList();
        medal = member.getMedal();

        tournamentGroup = member.getTournamentGroup();

        cash = member.getCash();
        money = member.getMoney();
        position = member.getPosition();
        reputation = member.getReputation();

        firstLogin = member.getFirstLogin();
        lastLogin = member.getLastLogin();
        joinTime = member.getJoinTime();
        onlineTime = member.getOnlineTime();

        serverId = member.getServerId();
        serverType = member.getServerType();

        lastServerId = member.getServerId();
        lastServerType = member.getServerType();

        online = member.isOnline();
        gladiatorInventory = member.getGladiatorInventory();
    }

    public List<Medal> getMedalList() {

        if (medalList == null) {
            medalList = new ArrayList<>();
        }

        return medalList;
    }
}
