package br.com.pentamc.bungee.networking.packet;

import java.util.UUID;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bungee.BungeeMain;
import br.com.pentamc.common.ban.Category;
import br.com.pentamc.common.ban.constructor.Ban;
import br.com.pentamc.common.networking.Packet;
import br.com.pentamc.common.networking.PacketHandler;
import br.com.pentamc.common.networking.packet.AnticheatAlertPacket;
import br.com.pentamc.common.networking.packet.AnticheatBanPacket;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import br.com.pentamc.common.utils.string.MessageBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import br.com.pentamc.bungee.bungee.BungeeMember;

public class BungeePacketHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, ProxiedServer server, ProxiedPlayer player) {
		BungeeMember member = (BungeeMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
	}
}
