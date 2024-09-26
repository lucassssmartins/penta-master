package br.com.pentamc.bungee.listener;

import java.lang.reflect.InvocationTargetException;

import br.com.pentamc.bungee.BungeeMain;
import br.com.pentamc.common.networking.Packet;
import br.com.pentamc.common.networking.PacketType;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PacketListener implements Listener {

	@EventHandler(priority = -128)
	public void onPluginMessage(PluginMessageEvent event) {
		if (!event.getTag().equals("server:packet"))
			return;

		if (!(event.getSender() instanceof Server)) {
			return;
		}
		if (!(event.getReceiver() instanceof ProxiedPlayer))
			return;

		ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getReceiver();
		Server server = (Server) event.getSender();

		ProxiedServer proxiedServer = BungeeMain.getInstance().getServerManager().getServer(server.getInfo().getName());

		ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
		JsonObject jsonObject = (JsonObject) new JsonParser().parse(in.readUTF());

		Packet packet = null;

		try {
			packet = PacketType.valueOf(jsonObject.get("packetType").getAsString().toUpperCase()).getPacketClass()
					.getConstructor(JsonObject.class).newInstance(jsonObject);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		BungeeMain.getInstance().getPacketController().handle(packet, proxiedServer, proxiedPlayer);
	}
}
