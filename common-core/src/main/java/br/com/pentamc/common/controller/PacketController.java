package br.com.pentamc.common.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.networking.Packet;
import br.com.pentamc.common.networking.PacketHandler;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;

public class PacketController {
	
	private List<PacketHandler> handlerList;
	
	public PacketController() {
		handlerList = new ArrayList<>();
	}
	
	public void registerHandler(PacketHandler handler) {
		if (handlerList.contains(handler))
			return;
		
		handlerList.add(handler);
	}
	
	public void unregisterHandler(PacketHandler handler) {
		if (!handlerList.contains(handler))
			return;
		
		handlerList.remove(handler);
	}
	
	public void handle(Packet packet, ProxiedServer sender, ProxiedPlayer proxiedPlayer) {
		for (PacketHandler handler : handlerList)
			handler.handlePacket(packet, sender, proxiedPlayer);
		
		CommonGeneral.getInstance().debug("[PacketHandler - Message] Sent the packets!");
	}


}
