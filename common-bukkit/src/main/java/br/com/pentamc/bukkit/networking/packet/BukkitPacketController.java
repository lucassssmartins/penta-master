package br.com.pentamc.bukkit.networking.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.controller.PacketController;
import br.com.pentamc.common.networking.Packet;
import org.bukkit.entity.Player;

public class BukkitPacketController extends PacketController {

	public void sendPacket(Packet packet, Player player) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF(packet.getJsonObject().toString());
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		player.sendPluginMessage(BukkitMain.getInstance(), "server:packet", b.toByteArray());
	}

}
