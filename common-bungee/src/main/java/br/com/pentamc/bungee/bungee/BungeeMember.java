package br.com.pentamc.bungee.bungee;

import java.util.UUID;

import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.MemberModel;
import br.com.pentamc.common.account.configuration.LoginConfiguration;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Getter
@Setter
public class BungeeMember extends Member {

	private transient ProxiedPlayer proxiedPlayer;


	public BungeeMember(MemberModel memberModel) {
		super(memberModel);
	}

	public BungeeMember(String playerName, UUID uniqueId, LoginConfiguration.AccountType accountType) {
		super(playerName, uniqueId, accountType);
	}

	@Override
	public void sendMessage(String message) {
		if (proxiedPlayer != null)
			proxiedPlayer.sendMessage(TextComponent.fromLegacyText(message));
	}

	@Override
	public void sendMessage(BaseComponent message) {
		if (proxiedPlayer != null)
			proxiedPlayer.sendMessage(message);
	}

	@Override
	public void sendMessage(BaseComponent[] message) {
		if (proxiedPlayer != null)
			proxiedPlayer.sendMessage(message);
	}

	@Override
	public void setJoinData(String playerName, String hostString) {
		super.setJoinData(playerName, hostString);
		proxiedPlayer = ProxyServer.getInstance().getPlayer(getUniqueId());
	}

}
