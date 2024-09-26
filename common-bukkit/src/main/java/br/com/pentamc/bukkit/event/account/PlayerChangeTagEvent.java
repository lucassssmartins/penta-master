package br.com.pentamc.bukkit.event.account;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.event.PlayerCancellableEvent;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.clan.enums.ClanDisplayType;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.common.tag.Tag;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerChangeTagEvent extends PlayerCancellableEvent {

	private Member member;

	private Tag oldTag;
	@Setter
	private Tag newTag;
	private boolean forced;

	public PlayerChangeTagEvent(Player player, Member member, Tag oldTag, Tag newTag, boolean forced) {
		super(player);
		this.member = member;
		this.oldTag = oldTag;
		this.newTag = newTag;
		this.forced = forced;
	}

	public boolean isClanTag() {
		if (member.getAccountConfiguration().getClanDisplayType() == ClanDisplayType.ALL
				|| (CommonGeneral.getInstance().getServerType() == ServerType.LOBBY
						&& member.getAccountConfiguration().getClanDisplayType() == ClanDisplayType.LOBBY))
			return member.getClan() != null;
		return false;
	}

}
