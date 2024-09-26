package br.com.pentamc.bukkit.api.title;

import java.lang.reflect.InvocationTargetException;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.api.packet.PacketBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;

public interface Title {

	void send(Player player);

	void reset(Player player);

	void broadcast();

	public static void send(Player player, String title, String subTitle, Class<? extends Title> clazz) {
		try {
			Title t = (Title) clazz.getConstructor(String.class, String.class).newInstance(title, subTitle);
			t.send(player);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public static void broadcast(String title, String subTitle, Class<? extends Title> clazz) {
		try {
			Title t = (Title) clazz.getConstructor(String.class, String.class).newInstance(title, subTitle);

			Bukkit.getOnlinePlayers().forEach(player -> t.send(player));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public static void clear(Player player) {
		try {
			BukkitMain.getInstance().getProcotolManager().sendServerPacket(player,
					new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, TitleAction.RESET).build());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
