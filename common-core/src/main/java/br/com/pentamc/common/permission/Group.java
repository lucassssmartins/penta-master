package br.com.pentamc.common.permission;

import net.md_5.bungee.api.ChatColor;
import br.com.pentamc.common.permission.group.GroupInterface;
import br.com.pentamc.common.permission.group.ModeratorGroup;
import br.com.pentamc.common.permission.group.OwnerGroup;
import br.com.pentamc.common.permission.group.SimpleGroup;
import br.com.pentamc.common.permission.group.StreamerGroup;
import br.com.pentamc.common.tag.Tag;
import br.com.pentamc.common.utils.string.StringUtils;

import java.util.Optional;

/**
 * @author yandv
 */

public enum Group {

    MEMBRO,
    VIP,
    PENTA,
    BETA,
    PARTNER,
    STREAMER,
    YOUTUBER,
    YOUTUBERPLUS(new StreamerGroup()),
    TRIAL(new ModeratorGroup()),
    MOD(new ModeratorGroup()),
    MODPLUS(new ModeratorGroup()),
    ADMIN(new OwnerGroup());

    private GroupInterface group;

    Group() {
        this(new SimpleGroup());
    }

    Group(GroupInterface group) {
        this.group = group;
    }

    public GroupInterface getGroup() {
        return group;
    }

    public String getColor() {
        Optional<Tag> optional = Optional.empty();

        try {
            optional = Optional.ofNullable(Tag.valueOf(name()));
        } catch (Exception ignored) {

        }

        return optional.map(Tag::getPrefix).map(StringUtils::getLastColors).orElse(ChatColor.GRAY.toString());
    }
}
