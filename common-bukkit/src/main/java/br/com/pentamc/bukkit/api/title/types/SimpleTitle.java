package br.com.pentamc.bukkit.api.title.types;

import java.lang.reflect.InvocationTargetException;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.api.packet.PacketBuilder;
import br.com.pentamc.bukkit.api.protocol.ProtocolGetter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import lombok.Getter;
import br.com.pentamc.bukkit.api.title.Title;

@Getter
public class SimpleTitle implements Title {

	private String title, subtitle;
	private int fadeInTime, stayTime, fadeOutTime;

	public SimpleTitle(String title, String subtitle) {
		this.title = title == null ? " " : title;
		this.subtitle = subtitle == null ? " " : subtitle;

		this.fadeInTime = 10;
		this.stayTime = 20;
		this.fadeInTime = 10;
	}

	public SimpleTitle(String title, String subtitle, int fadeInTime, int stayTime, int fadeOutTime) {
		this.title = title == null ? " " : title;
		this.subtitle = subtitle == null ? " " : subtitle;

		this.fadeInTime = fadeInTime;
		this.stayTime = stayTime;
		this.fadeInTime = fadeOutTime;
	}

	@Override
	public void send(Player player) {
		if (ProtocolGetter.getVersion(player).getId() >= 47) {
			sendPacket(player, new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, TitleAction.TIMES)
					.writeInteger(0, fadeInTime).writeInteger(1, stayTime).writeInteger(2, fadeOutTime).build());
			sendPacket(player, new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, TitleAction.TITLE)
					.writeChatComponents(0, WrappedChatComponent.fromText(title)).build());
			sendPacket(player, new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, TitleAction.SUBTITLE)
					.writeChatComponents(0, WrappedChatComponent.fromText(subtitle)).build());
		}
	}

	@Override
	public void reset(Player player) {
		sendPacket(player,
				new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, TitleAction.CLEAR).build());
	}

	@Override
	public void broadcast() {
		Bukkit.getOnlinePlayers().forEach(player -> send(player));
	}

	private void sendPacket(Player player, PacketContainer packet) {
		try {
			BukkitMain.getInstance().getProcotolManager().sendServerPacket(player, packet);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
